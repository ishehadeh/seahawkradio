package seahawkradio.cms;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import io.javalin.http.Handler;

// Container for User-related Handlers
public class UserController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    // declare private constructor to remove implicit public constructor
    private UserController() {}

    public static final Handler login = ctx -> {
        final int maxAge = 60 * 60 * 24 * 7; // 1 week in seconds

        final String username = ctx.formParam("username");
        final String password = ctx.formParam("password");

        LOG.info("authorizing user username='{}'", username);
        // TODO validate form params

        // TODO authentication
        // String session = LoginHandler.getSessionToken(username, password);
        String session = "<TODO>";

        ctx.cookie("session", session, maxAge);
        ctx.redirect("/");
    };

}
