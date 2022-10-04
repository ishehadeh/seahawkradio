package seahawkradio.cms;

import io.javalin.http.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PodcastController {
    private static final Logger LOG = LoggerFactory.getLogger(PodcastController.class);

    public static final Handler createPodcastEndpoint =
            ctx -> {
                LOG.atInfo().addKeyValue("params", ctx.formParamMap()).log("create podcast req");
                ctx.result("todo");
            };
}
