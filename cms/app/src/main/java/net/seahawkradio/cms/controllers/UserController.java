package net.seahawkradio.cms.controllers;

import io.javalin.http.Handler;

import net.seahawkradio.cms.dao.SessionDao;
import net.seahawkradio.cms.dao.UserDao;
import net.seahawkradio.cms.models.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

// Container for User-related Handlers
public class UserController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    // declare private constructor to remove implicit public constructor
    private UserController() {}

    // Add a attribute "user" with the current authenticated user if a valid session cookie is
    // present.
    public static final Handler withUserMiddleware =
            ctx -> {
                // the attribute should always be set if this middleware is installed.
                // default to an empty optional, meaning no valid session.
                ctx.attribute("user", Optional.empty());

                final var users = new UserDao(ctx.appAttribute("database"));
                final var sessionIdStr = ctx.cookie("session");
                if (sessionIdStr == null) {
                    LOG.atInfo().log("no session fond");
                    return;
                }

                final var sessionId = UUID.fromString(sessionIdStr);
                if (sessionId == null) {
                    LOG.atInfo()
                            .addKeyValue("session", sessionIdStr)
                            .log("session cookie is not a valid UUID");
                    return;
                }

                try {
                    final var user = users.fromSession(sessionId);
                    if (user.isEmpty()) {
                        LOG.atInfo()
                                .addKeyValue("session", sessionId)
                                .log("invalid or expired session");
                    }
                    ctx.attribute("user", user);
                } catch (SQLException e) {
                    LOG.atError()
                            .addKeyValue("session", sessionId)
                            .setCause(e)
                            .log("SQL Error when querying user by session ID");
                }
            };

    public static final Handler login =
            ctx -> {
                final Duration maxAge = Duration.ofDays(7);
                final UserDao userAccessor = new UserDao(ctx.appAttribute("database"));
                final SessionDao sessions = new SessionDao(ctx.appAttribute("database"));

                final String username = ctx.formParam("username");
                final String password = ctx.formParam("password");

                LOG.info("authorizing user username='{}'", username);
                // TODO validate form params

                Optional<User> user = userAccessor.login(username, password);
                if (user.isPresent()) {
                    var session = sessions.create(user.get(), maxAge);
                    ctx.cookie("session", session.id().toString(), (int) maxAge.toSeconds());
                    ctx.redirect("/");
                } else {
                    ctx.redirect("/?loginSuccess=false");
                }
            };
}
