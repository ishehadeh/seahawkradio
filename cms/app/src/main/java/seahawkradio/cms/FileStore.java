package seahawkradio.cms;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class FileStore {
    private static final Logger LOG = LoggerFactory.getLogger(FileStore.class);

    public final Path storePath;
    public MessageDigest hasher;

    public FileStore(Path storePath) {
        this.storePath = storePath;
        try {
            this.hasher = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            this.hasher = null;
            assert false; // TODO what do we do here
        }
        if (!storePath.toFile().exists()) {
            storePath.toFile().mkdir();
        }
    }

    public String put(byte[] data) throws IOException {
        final var BASE64_ENCODER = Base64.getEncoder();

        final String id = BASE64_ENCODER.encodeToString(hasher.digest(data));
        LOG.atInfo().addKeyValue("size", data.length).addKeyValue("id", id).log("storing blob");
        var file = storePath.resolve(id).toFile();
        if (!file.createNewFile()) {
            LOG.atError().addKeyValue("id", id).log("object already exists, overwriting...");
            // TODO don't just overwrite
        }

        try (var fileStream = new FileOutputStream(file)) {
            fileStream.write(data);
        }
        return id;
    }

    public Optional<byte[]> get(String id) throws IOException {
        final var file = storePath.resolve(id).toFile();
        try (var fileStream = new FileInputStream(file)) {
            return Optional.of(fileStream.readAllBytes());
        } catch (FileNotFoundException e) {
            return Optional.empty();
        }
    }
}
