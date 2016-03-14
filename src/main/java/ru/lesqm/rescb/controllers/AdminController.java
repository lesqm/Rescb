package ru.lesqm.rescb.controllers;

import com.bunjlabs.fugaframework.dependency.Inject;
import com.bunjlabs.fugaframework.foundation.Controller;
import com.bunjlabs.fugaframework.foundation.Response;
import com.bunjlabs.fugaframework.templates.TemplateNotFoundException;
import com.bunjlabs.fugaframework.templates.TemplateRenderException;
import java.util.List;
import ru.lesqm.rescb.logic.TezisHuman;
import ru.lesqm.rescb.services.Database;

public class AdminController extends Controller {
    
    @Inject
    public Database db;
    
    public Response listAllTezises() throws TemplateNotFoundException, TemplateRenderException {
        List<TezisHuman> thlist = TezisHuman.getAll(db);
        
        ctx.put("thlist", thlist);
        
        return ok(view("admin/listalltezises.html"));
    }

}
