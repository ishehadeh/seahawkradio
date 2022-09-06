package seahawkradio.cms;

import org.slf4j.LoggerFactory;
import java.util.Optional;
import org.slf4j.Logger;

import io.javalin.http.Handler;

// Container for User-related Handlers
public class UserController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    // declare private constructor to remove implicit public constructor
    private UserController() {}

    public static final Handler login = ctx -> {
        final int maxAge = 60 * 60 * 24 * 7; // 1 week in seconds
        final UserDao userAccessor = new UserDao(ctx.appAttribute("database"));

        final String username = ctx.formParam("username");
        final String password = ctx.formParam("password");

        LOG.info("authorizing user username='{}'", username);
        // TODO validate form params

        Optional<User> user = userAccessor.login(username, password);
        if (user.isPresent()) {
            String session = user.get().username();
            ctx.cookie("session", session, maxAge);
            ctx.redirect("/");
        } else {
            ctx.redirect("/?loginSuccess=false");
        }

    };

}
