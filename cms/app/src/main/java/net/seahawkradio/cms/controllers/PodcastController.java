package net.seahawkradio.cms.controllers;

import io.javalin.http.Handler;
import io.javalin.validation.JavalinValidation;

import net.seahawkradio.cms.dao.PodcastDao;
import net.seahawkradio.cms.models.Identity;
import net.seahawkradio.cms.models.Language;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
                final var podcasts = new PodcastDao(ctx.appAttribute("database"));
                LOG.atInfo().addKeyValue("params", ctx.formParamMap()).log("create podcast req");
                var language =
                        ctx.formParamAsClass("language", String.class)
                                .check(
                                        PodcastController::validateLanguage,
                                        "not a valid language code");
                var explicit = ctx.formParamAsClass("explicit", Boolean.class).allowNullable();
                var description = ctx.formParamAsClass("description", String.class);
                var title = ctx.formParamAsClass("title", String.class);
                var copyright = ctx.formParamAsClass("copyright", String.class);
                var author = ctx.formParamAsClass("author", String.class);
                var categoryPrimary = ctx.formParamAsClass("category-primary", String.class);
                var categorySecondary =
                        ctx.formParamAsClass("category-secondary", String.class).allowNullable();

                final var formParamErrors =
                        JavalinValidation.collectErrors(
                                language,
                                explicit,
                                description,
                                title,
                                copyright,
                                author,
                                categoryPrimary,
                                categorySecondary);
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

                Set<String> categories = new HashSet<String>();
                categories.add(categoryPrimary.get());
                if (categorySecondary.get() != null) {
                    categories.add(categorySecondary.get());
                }

                podcasts.create(
                        title.get(),
                        description.get(),
                        author.get(),
                        copyright.get(),
                        Optional.ofNullable(explicit.get()).orElse(false),
                        new Identity("", ""),
                        categories);
                ctx.result("ok");
            };
}
