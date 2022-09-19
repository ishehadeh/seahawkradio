package seahawkradio.cms;

import io.javalin.http.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Optional;

// Container for User-related Handlers
public class UserController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    // declare private constructor to remove implicit public constructor
    private UserController() {}

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
