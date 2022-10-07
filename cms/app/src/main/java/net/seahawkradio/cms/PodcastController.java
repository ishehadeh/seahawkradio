package net.seahawkradio.cms;

import io.javalin.http.Handler;
import io.javalin.validation.JavalinValidation;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PodcastController {
    private static final Logger LOG = LoggerFactory.getLogger(PodcastController.class);
    private static final PolicyFactory PODCAST_DESCRIPTION_SANATIZER =
            new HtmlPolicyBuilder()
                    .allowElements("a", "h1", "h2", "h3", "ul", "ol", "li")
                    .toFactory();

    private static final Boolean validateLanguage(String alpha2In) {
        for (var lang : Language.ALL) {
            if (lang.alpha2().isPresent() && lang.alpha2().get().contentEquals(alpha2In)) {
                return true;
            }
        }

        return false;
    }

    public static final Handler createPodcastEndpoint =
            ctx -> {
                LOG.atInfo().addKeyValue("params", ctx.formParamMap()).log("create podcast req");
                var language =
                        ctx.formParamAsClass("language", String.class)
                                .check(
                                        PodcastController::validateLanguage,
                                        "not a valid language code");
                var explicit = ctx.formParamAsClass("explicit", Boolean.class);
                var description = ctx.formParamAsClass("description", String.class);
                var title = ctx.formParamAsClass("title", String.class);
                var copyright = ctx.formParamAsClass("copyright", String.class);

                final var formParamErrors =
                        JavalinValidation.collectErrors(
                                language, explicit, description, title, copyright);
                if (!formParamErrors.isEmpty()) {
                    LOG.atInfo()
                            .addKeyValue("errors", formParamErrors)
                            .log("rejecting podcast create request");
                    ctx.result("invalid request").status(400);
                    return;
                }

                var sanatizedDescription =
                        PodcastController.PODCAST_DESCRIPTION_SANATIZER.sanitize(description.get());
                LOG.atInfo()
                        .addKeyValue("description", description.get())
                        .addKeyValue("sanatizedDescription", sanatizedDescription)
                        .log("sanatized description");
                ctx.result("ok");
            };
}
