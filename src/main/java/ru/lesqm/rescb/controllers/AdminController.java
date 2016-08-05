package ru.lesqm.rescb.controllers;

import com.bunjlabs.fugaframework.configuration.Configuration;
import com.bunjlabs.fugaframework.dependency.Inject;
import com.bunjlabs.fugaframework.foundation.Controller;
import com.bunjlabs.fugaframework.foundation.Response;
import com.bunjlabs.fugaframework.templates.TemplateNotFoundException;
import com.bunjlabs.fugaframework.templates.TemplateRenderException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import ru.lesqm.rescb.logic.TezisFile;
import ru.lesqm.rescb.logic.TezisHuman;
import ru.lesqm.rescb.logic.User;
import ru.lesqm.rescb.services.Database;

public class AdminController extends Controller {

    @Inject
    public Database db;
    @Inject
    public Configuration config;
    private static final String uploadDirDefault = "upload" + File.separator;

    public File getDataFile(String uploadId) {
        String uploadDir = config.get("rescb.data.dir", uploadDirDefault) + File.separator + uploadDirDefault;

        return new File(uploadDir + uploadId);
    }

    private static String getSectionName(int id) {
        switch (id) {
            case 0:
                return "Новые тенденции, результаты и теоретические подходы в исследовании и описании электронной структуры";
            case 1:
                return "Новые разработки технологий и лабораторных приборов для исследования строения вещества";
            case 2:
                return "Фотоэлектронная спектроскопия и электронная Оже-спектроскопия";
            case 3:
                return "Фотоэлектронная дифракция";
            case 4:
                return "Рентгеновская эмиссионная спектроскопия. EXAFS, NEXAFS (XANES)";
            case 5:
                return "Применение фотоэлектронной спектроскопии для исследования поверхности, катализаторов и полупроводников";
            case 6:
                return "Применение фотоэлектронной спектроскопии для исследования биомолекул и наноструктурированных функциональных материалов";
            default:
                return "Неизвестно";
        }
    }

    public Response listAllTezises() throws TemplateNotFoundException, TemplateRenderException {
        List<TezisHuman> thlist = TezisHuman.getAll(db);

        ctx.put("thlist", thlist);

        return ok(view("admin/listalltezises.html"));
    }

    public Response listAllUsers() throws TemplateNotFoundException, TemplateRenderException {
        List<User> users = User.getAllWithHaveTezis(db);

        ctx.put("users", users);

        return ok(view("admin/listallusers.html"));
    }

    public Response exportZip() throws Exception {
        byte[] buffer = new byte[1024];

        List<TezisHuman> thlist = TezisHuman.getAll(db);

        File ftmp = File.createTempFile(UUID.randomUUID().toString(), ".zip");

        ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(ftmp));

        for (TezisHuman t : thlist) {
            User u = t.getUser();
            TezisFile tezisFile = TezisFile.getById(db, t.getFileId());

            String userName = u.getLastname() + "-" + u.getMiddlename() + "-" + u.getFirstname();
            ZipEntry ze = new ZipEntry(getSectionName(t.getSection()) + File.separator + userName + "-" + t.getId() + "-" + tezisFile.getHash());

            zout.putNextEntry(ze);

            try (FileInputStream in = new FileInputStream(getDataFile(tezisFile.getHash()))) {
                int len;
                while ((len = in.read(buffer)) > 0) {
                    zout.write(buffer, 0, len);
                }
            }

        }

        zout.flush();
        zout.close();

        return ok(ftmp).as("application/zip")
                .header("Content-Disposition", "inline; filename=" + "TezisExport.zip");
    }

}
