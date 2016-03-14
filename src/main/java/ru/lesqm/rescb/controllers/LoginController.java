package ru.lesqm.rescb.controllers;

import com.bunjlabs.fugaframework.dependency.Inject;
import com.bunjlabs.fugaframework.foundation.Controller;
import com.bunjlabs.fugaframework.foundation.Response;
import com.bunjlabs.fugaframework.sessions.Session;
import com.bunjlabs.fugaframework.templates.TemplateNotFoundException;
import com.bunjlabs.fugaframework.templates.TemplateRenderException;
import java.nio.charset.Charset;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import ru.lesqm.rescb.services.Database;
import ru.lesqm.rescb.logic.User;
import ru.lesqm.rescb.services.MailgunService;
import ru.lesqm.rescb.utils.HashUtils;

public class LoginController extends Controller {

    private static final Logger log = LogManager.getLogger(LoginController.class);

    @Inject
    public Database db;

    @Inject
    public MailgunService ms;

    public Response signinProcess() {
        JSONObject json = new JSONObject(ctx.getRequest().getContent().toString(Charset.forName("UTF-8")));

        User u = User.getByEmailPassword(db, json.getString("email"), HashUtils.sha256String(json.getString("password")));

        if (u == null) {
            return ok(new JSONObject().put("status", "error").put("msg", "user not found").toString()).asJson();
        }

        Session session = ctx.getSession();
        session.put("user", u);

        return ok(new JSONObject().put("status", "ok").put("url", urls.that(ctx.get("lang"), "")).toString()).asJson();
    }

    public Response signupProcess() {
        JSONObject json = new JSONObject(ctx.getRequest().getContent().toString(Charset.forName("UTF-8")));

        /*if (!json.getString("password").equals(json.getString("repassword"))) {
            return ok(new JSONObject().put("status", "error").put("msg", "passwords does not match").toString()).asJson();
        }*/
        
        User u = new User();

        u.setFirstname(json.getString("firstname"));
        u.setLastname(json.getString("lastname"));
        u.setMiddlename(json.getString("middlename"));
        u.setEmail(json.getString("email"));
        u.setPassword(HashUtils.sha256String(json.getString("password")));
        u.setCountry(json.getString("country"));
        u.setCity(json.getString("city"));
        u.setPosition(json.getString("position"));
        u.setJob(json.getString("job"));
        u.setDegree(json.getString("degree"));
        u.setContactphone(json.getString("contactphone"));

        if (!u.validate()) {
            return ok(new JSONObject().put("status", "error").put("msg", "invalid request").toString()).asJson();
        }

        if (User.getByEmail(db, u.getEmail()) != null) {
            return ok(new JSONObject().put("status", "error").put("msg", "email already exists").toString()).asJson();
        }

        User.put(db, u);

        Session session = ctx.getSession();
        session.put("user", u);

        ctx.put("password", json.getString("password"));
        ctx.put("user", u);

        try {
            ms.sendNoReply(u.getEmail(), "Новая учетная запись", view("mail/registered.html"));
        } catch (TemplateNotFoundException | TemplateRenderException ex) {
            log.catching(ex);
        }

        return ok(new JSONObject().put("status", "ok").put("url", urls.that(ctx.get("lang"), "")).toString()).asJson();
    }

    public Response signout() {
        Session session = ctx.getSession();
        session.remove("user");

        return temporaryRedirect(urls.that(ctx.get("lang"), ""));
    }

    public Response checkCookie() {
        if (ctx.getSession().containsKey("user")) {
            return proceed();
        }
        //User u = ctx.getRequest().getCookie("user");

        return proceed();
    }

    public Response checkAuth() {
        if (ctx.getSession().containsKey("user")) {
            return proceed();
        }

        return forbidden("You don't have permissions");
    }

}
