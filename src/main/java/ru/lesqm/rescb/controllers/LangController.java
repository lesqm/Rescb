package ru.lesqm.rescb.controllers;

import com.bunjlabs.fugaframework.foundation.Controller;
import com.bunjlabs.fugaframework.foundation.Response;
import com.bunjlabs.fugaframework.sessions.Session;

public class LangController extends Controller {

    public Response noLangOnUrl() {
        Session session = ctx.getSession();

        String lang = "ru";
        if (session.containsKey("lang")) {
            lang = (String) session.get("lang");
        }

        return seeOther(urls.that(lang, ""));
    }

    public Response noSlashOnLang(String lang) {
        ctx.getSession().put("lang", lang);
        
        return seeOther(urls.that(lang, ""));
    }

    public Response setLang(String lang) {
        ctx.getSession().put("lang", lang);

        return proceed();
    }
}
