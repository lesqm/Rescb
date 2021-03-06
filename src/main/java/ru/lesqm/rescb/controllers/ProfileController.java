package ru.lesqm.rescb.controllers;

import com.bunjlabs.fugaframework.dependency.Inject;
import com.bunjlabs.fugaframework.foundation.Controller;
import com.bunjlabs.fugaframework.foundation.Response;
import com.bunjlabs.fugaframework.sessions.Session;
import com.bunjlabs.fugaframework.templates.TemplateNotFoundException;
import com.bunjlabs.fugaframework.templates.TemplateRenderException;
import java.nio.charset.Charset;
import java.sql.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import ru.lesqm.rescb.logic.Application;
import ru.lesqm.rescb.services.Database;
import ru.lesqm.rescb.logic.TezisFile;
import ru.lesqm.rescb.logic.User;
import ru.lesqm.rescb.services.MailgunService;
import ru.lesqm.rescb.utils.HashUtils;

public class ProfileController extends Controller {

    private static final Logger log = LogManager.getLogger(ProfileController.class);

    @Inject
    public Database db;

    @Inject
    public MailgunService ms;

    public Response profile() throws TemplateNotFoundException, TemplateRenderException {
        User u = (User) ctx.getSession().get("user");

        List<Application> apps = Application.getByUserId(db, u.getId());

        ctx.put("applications", apps);

        return ok(view("profile.html"));
    }

    public Response settingsProcess() {
        User u = (User) ctx.getSession().get("user");

        if (u == null) {
            return forbidden("You don't have permissions");
        }

        JSONObject json = new JSONObject(ctx.getRequest().getContent().toString(Charset.forName("UTF-8")));

        u.setFirstname(json.getString("firstname"));
        u.setLastname(json.getString("lastname"));
        u.setMiddlename(json.getString("middlename"));
        //u.setEmail(json.getString("email"));
        //u.setPassword(HashUtils.sha256String(json.getString("password")));
        u.setCountry(json.getString("country"));
        u.setCity(json.getString("city"));
        u.setPosition(json.getString("position"));
        u.setJob(json.getString("job"));
        u.setDegree(json.getString("degree"));
        u.setContactphone(json.getString("contactphone"));
        u.setGender(Integer.parseInt(json.getString("gender")));
        u.setBirthday(Date.valueOf(json.getString("birthday")));

        if (!u.validate()) {
            return ok(new JSONObject().put("status", "error").put("msg", "invalid request").toString()).asJson();
        }

        User.update(db, u);

        Session session = ctx.getSession();
        session.put("user", u);

        return ok(new JSONObject().put("status", "ok").put("url", urls.that(ctx.get("lang"), "profile")).toString()).asJson();
    }

    public Response passwordProcess() {
        User u = (User) ctx.getSession().get("user");

        if (u == null) {
            return forbidden("You don't have permissions");
        }

        JSONObject json = new JSONObject(ctx.getRequest().getContent().toString(Charset.forName("UTF-8")));

        //u.setEmail(json.getString("email"));
        String newPassword = HashUtils.sha256String(json.getString("password"));
        String currentPassword = HashUtils.sha256String(json.getString("currentpassword"));
        if (!currentPassword.equals(u.getPassword())
                || !json.getString("password").equals(json.getString("repassword"))) {
            return ok(new JSONObject().put("status", "error").put("msg", "passwords does not match").toString()).asJson();
        }

        u.setPassword(newPassword);

        if (!u.validate()) {
            return ok(new JSONObject().put("status", "error").put("msg", "invalid request").toString()).asJson();
        }

        User.update(db, u);

        Session session = ctx.getSession();
        session.put("user", u);

        ctx.put("password", json.getString("password"));
        ctx.put("user", u);

        try {
            ms.sendNoReply(u.getEmail(), "Изменение пароля", view("mail/passwordchanged.html"));
        } catch (TemplateNotFoundException | TemplateRenderException ex) {
            log.catching(ex);
        }

        return ok(new JSONObject().put("status", "ok").put("url", urls.that(ctx.get("lang"), "profile")).toString()).asJson();
    }

    public Response tezisProcess() {
        User u = (User) ctx.getSession().get("user");

        if (u == null) {
            return forbidden("You don't have permissions");
        }

        JSONObject json = new JSONObject(ctx.getRequest().getContent().toString(Charset.forName("UTF-8")));

        Application app = new Application();
        app.setUserId(u.getId());
        app.setForm(Integer.parseInt(json.getString("form")));
        app.setSection(Integer.parseInt(json.getString("section")));
        app.setTitle(json.getString("title"));

        TezisFile tezisFile = TezisFile.getByHash(db, json.getString("fileId"));

        if (tezisFile == null) {
            return badRequest("Bad request");
        }

        app.setFileId(tezisFile.getId());

        app.save(db);

        return ok(new JSONObject().put("status", "ok").put("url", urls.that(ctx.get("lang"), "profile")).toString()).asJson();
    }
    
    public Response tezisDeleteProcess(long aid) {
        User u = (User) ctx.getSession().get("user");

        if (u == null) {
            return forbidden("You don't have permissions");
        }
        
        Application app = Application.getById(db, aid);
        
        if(app.getUserId() != u.getId()) {
            return forbidden("You don't have permissions");
        }
        
        Application.delete(db, app);
        
        return temporaryRedirect(urls.that(ctx.get("lang"), "profile"));
    }
}
