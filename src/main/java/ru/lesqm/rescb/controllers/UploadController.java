package ru.lesqm.rescb.controllers;

import com.bunjlabs.fugaframework.configuration.Configuration;
import com.bunjlabs.fugaframework.dependency.Inject;
import com.bunjlabs.fugaframework.foundation.Controller;
import com.bunjlabs.fugaframework.foundation.Response;
import static com.bunjlabs.fugaframework.foundation.Responses.badRequest;
import static com.bunjlabs.fugaframework.foundation.Responses.internalServerError;
import static com.bunjlabs.fugaframework.foundation.Responses.notFound;
import static com.bunjlabs.fugaframework.foundation.Responses.ok;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import ru.lesqm.rescb.logic.Database;
import ru.lesqm.rescb.logic.TezisFile;
import ru.lesqm.rescb.logic.User;

public class UploadController extends Controller {

    private static final Logger log = LogManager.getLogger(UploadController.class);

    private static final Map<String, File> files = new HashMap<>();
    private static final String uploadTmpDirDefault = "tmp" + File.separator;
    private static final String uploadDirDefault = "upload" + File.separator;

    @Inject
    public Database db;

    @Inject
    public Configuration config;

    private File getDataFile(String uploadId) {
        String uploadDir = config.get("rescb.data.dir", uploadDirDefault) + File.separator + uploadDirDefault;

        return new File(uploadDir + uploadId);
    }

    public Response download(long id) throws IOException {
        User u = (User) ctx.getSession().get("user");

        TezisFile tezisFile = TezisFile.getById(db, id);

        return ok(getDataFile(tezisFile.getHash()))
                .header("Content-Disposition", "inline; filename=" + tezisFile.getHash());
    }

    public Response uploadProcess() {
        if (!ctx.getSession().containsKey("user")) {
            return forbidden("You don't have permissions");
        }

        Map<String, String> headers = ctx.getRequest().getHeaders();

        if (!headers.containsKey("X-Upload-Size") || !headers.containsKey("X-Upload-Start") || !headers.containsKey("X-Upload-End") || !headers.containsKey("X-Upload-Name")) {
            return badRequest();
        }

        int fileSize = Integer.parseInt(headers.get("X-Upload-Size"));
        int start = Integer.parseInt(headers.get("X-Upload-Start"));
        int end = Integer.parseInt(headers.get("X-Upload-End"));
        String fileName = headers.get("X-Upload-Name");
        String fileType = fileName.indexOf('.') >= 0 ? fileName.substring(fileName.lastIndexOf('.')) : "";
        String uploadTmpDir = ctx.getApp().getConfiguration().get("rescb.data.dir", uploadTmpDirDefault) + File.separator + uploadTmpDirDefault;

        try {
            String uploadId;
            File file;
            if (!headers.containsKey("X-Upload-Id")) {
                uploadId = UUID.randomUUID().toString() + "-" + fileSize + fileType;
                file = new File(uploadTmpDir + uploadId + ".part");
                file.getParentFile().mkdirs();
                file.createNewFile();
                files.put(uploadId, file);
            } else {
                uploadId = headers.get("X-Upload-Id");
                file = files.get(uploadId);
            }

            if (file == null) {
                return badRequest();
            }

            FileOutputStream fos = new FileOutputStream(file, true);

            fos.write(ctx.getRequest().getContent().array());

            if (end >= fileSize - 1) {

                File finalFile = getDataFile(uploadId);
                finalFile.getParentFile().mkdirs();
                Files.move(file.toPath(), finalFile.toPath());
                files.remove(uploadId);

                TezisFile tezisFile = new TezisFile();
                tezisFile.setHash(uploadId);
                tezisFile.save(db);

                return ok(new JSONObject().put("continue", false).put("uploadId", uploadId).toString()).asJson();
            } else {
                return ok(new JSONObject().put("continue", true).put("uploadId", uploadId).toString()).asJson();
            }
        } catch (Exception ex) {
            log.catching(ex);
            return internalServerError();
        }
    }

}
