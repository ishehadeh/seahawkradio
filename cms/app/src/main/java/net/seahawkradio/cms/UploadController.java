package net.seahawkradio.cms;

import io.javalin.http.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class UploadController {
    private static final Logger LOG = LoggerFactory.getLogger(UploadController.class);
    private static final FileStore STORE = new FileStore(Path.of("./uploads"));
    private static final ImageUploadConfig CONFIG =
            new ImageUploadConfig(
                    1024 * 1024 * 512,
                    1920,
                    1080,
                    Set.of("image/jpeg", "image/png"),
                    "http://localhost:8080/media/");

    public record ImageUploadConfig(
            long maxContentSize,
            int maxWidth,
            int maxHeight,
            Set<String> allowedContentTypes,
            String imageBaseURL) {}

    private UploadController() {}

    public static final Handler uploadAudio =
            ctx -> {
                final var media = new MediaDao(ctx.appAttribute("database"));

                var file = ctx.uploadedFile("audio");
                if (file == null) {
                    ctx.status(415).result("unsupported media type");
                    LOG.atWarn().setMessage("missing file in upload request").log();
                    return;
                }

                // TODO check content size
                final var contentType = file.getContentType();
                final var audioContent = file.getContent().readAllBytes();
                try {
                    final var audioStream =
                            AudioSystem.getAudioInputStream(new ByteArrayInputStream(audioContent));
                } catch (UnsupportedAudioFileException e) {
                    ctx.status(415).result("unsupported media type");
                    LOG.atWarn().setMessage("audio format not supported").log();
                    return;
                }
                // TODO verify stream?

                final var mediaRecord = media.create(file.getFilename(), contentType);
                var key =
                        STORE.put(
                                mediaRecord.id().toString(),
                                new ByteArrayInputStream(audioContent));
                LOG.atInfo().setMessage("uploaded image").addKeyValue("key", key).log();
                ctx.status(200).result("success");
            };

    public static final Handler getMediaEndpoint =
            ctx -> {
                final var media = new MediaDao(ctx.appAttribute("database"));

                final var id = UUID.fromString(ctx.pathParam("id"));
                LOG.atInfo().addKeyValue("id", id).log("media request");

                final var mediaRecord = media.byId(id);
                final var content = STORE.get(id.toString());
                LOG.atInfo()
                        .addKeyValue("id", id)
                        .addKeyValue("media", mediaRecord)
                        .log("found media record matching ID");
                if (mediaRecord.isPresent() && content.isPresent()) {
                    ctx.status(200)
                            .contentType(mediaRecord.get().contentType())
                            .result(content.get());
                    return;
                }

                ctx.status(404).result("file not found");
            };

    public record CKEditorErrorResponse(String error) {}

    public record CKEditorImageResponse(String url) {}

    // endpoint for CKEditor 5 SimpleUploadAdapter
    // see:
    // https://ckeditor.com/docs/ckeditor5/latest/features/images/image-upload/simple-upload-adapter.html
    public static final Handler ckEditorImageEndpoint =
            ctx -> {
                final var media = new MediaDao(ctx.appAttribute("database"));

                final var file = ctx.uploadedFiles().get(0);
                if (file == null) {
                    ctx.status(415).result("unsupported media type");
                    LOG.atWarn().setMessage("missing file in upload request").log();
                    return;
                }
                final var contentType = file.getContentType();

                if (contentType == null || !CONFIG.allowedContentTypes().contains(contentType)) {
                    ctx.status(415).json(new CKEditorErrorResponse("unsupported media type"));
                    LOG.atWarn()
                            .setMessage("content type not in allow-list")
                            .addKeyValue("Content-Type", contentType)
                            .addKeyValue(
                                    "allowedContentTypes",
                                    String.join(", ", CONFIG.allowedContentTypes()))
                            .log();
                    return;
                }

                final var contentSize = file.getSize();
                if (contentSize > CONFIG.maxContentSize()) {
                    ctx.status(413).json(new CKEditorErrorResponse("payload to large"));
                    LOG.atWarn()
                            .setMessage("max content size exceeded")
                            .addKeyValue("contentSize", contentSize)
                            .addKeyValue("maxContentSize", CONFIG.maxContentSize())
                            .log();
                    return;
                }

                // validate the image
                var readers = ImageIO.getImageReadersByMIMEType(contentType);
                if (!readers.hasNext()) {
                    ctx.status(415).json(new CKEditorErrorResponse("unsupported media type"));
                    LOG.atError()
                            .setMessage("Content-Type in allow list, but not supported by ImageIO")
                            .addKeyValue("Content-Type", contentType)
                            .log();
                    return;
                }
                var reader = readers.next();
                var imgContent = file.getContent().readAllBytes();
                reader.setInput(
                        ImageIO.createImageInputStream(new ByteArrayInputStream(imgContent)),
                        true,
                        true);
                var img = reader.read(0); // why 0?

                final var mediaRecord = media.create("", contentType);
                var key =
                        STORE.put(
                                mediaRecord.id().toString(), new ByteArrayInputStream(imgContent));
                LOG.atInfo().setMessage("uploaded image").addKeyValue("key", key).log();
                ctx.status(200)
                        .json(
                                new CKEditorImageResponse(
                                        CONFIG.imageBaseURL + mediaRecord.id().toString()));
                return;
            };
    public static final Handler uploadImage =
            ctx -> {
                final var media = new MediaDao(ctx.appAttribute("database"));

                var file = ctx.uploadedFile("image");
                if (file == null) {
                    ctx.status(415).result("unsupported media type");
                    LOG.atWarn().setMessage("missing file in upload request").log();
                    return;
                }
                final var contentType = file.getContentType();

                // TODO check content size
                // validate the image
                var readers = ImageIO.getImageReadersByMIMEType(contentType);
                if (!readers.hasNext()) {
                    ctx.status(415).result("unsupported media type");
                    LOG.atWarn()
                            .setMessage("unsupported content type")
                            .addKeyValue("Content-Type", contentType)
                            .log();
                }
                var reader = readers.next();
                var imgContent = file.getContent().readAllBytes();
                reader.setInput(
                        ImageIO.createImageInputStream(new ByteArrayInputStream(imgContent)),
                        true,
                        true);
                var img = reader.read(0); // why 0?

                final var mediaRecord = media.create(file.getFilename(), contentType);
                var key =
                        STORE.put(
                                mediaRecord.id().toString(), new ByteArrayInputStream(imgContent));
                LOG.atInfo().setMessage("uploaded image").addKeyValue("key", key).log();
                ctx.status(200).result("success");
                return;
            };
}
