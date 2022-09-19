package seahawkradio.cms;

import io.javalin.http.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class UploadController {
    private static final Logger LOG = LoggerFactory.getLogger(UploadController.class);
    private static FileStore STORE = new FileStore(Path.of("./uploads"));

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
