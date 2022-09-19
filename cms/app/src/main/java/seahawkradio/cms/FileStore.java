package seahawkradio.cms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;

public class FileStore {
    private static final Logger LOG = LoggerFactory.getLogger(FileStore.class);

    public final Path storePath;

    public FileStore(Path storePath) {
        this.storePath = storePath;
    }

    public String put(String id, InputStream data) throws IOException {
        LOG.atInfo().addKeyValue("id", id).log("storing blob");
        var file = storePath.resolve(id).toFile();
        if (!file.createNewFile()) {
            LOG.atError().addKeyValue("id", id).log("object already exists, overwriting...");
            // TODO don't just overwrite
        }

        try (var fileStream = new FileOutputStream(file)) {
            data.transferTo(fileStream);
        }
        return id;
    }

    public Optional<InputStream> get(String id) throws IOException {
        final var file = storePath.resolve(id).toFile();
        try (var fileStream = new FileInputStream(file)) {
            return Optional.of(fileStream);
        } catch (FileNotFoundException e) {
            return Optional.empty();
        }
    }
}
