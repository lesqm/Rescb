package ru.lesqm.rescb.controllers;

import com.bunjlabs.fugaframework.foundation.Controller;
import com.bunjlabs.fugaframework.foundation.Response;
import java.util.Map;

public class RestrictController extends Controller {
    
    private static Response restrictedHeaders() {
        return unauthorized("401 Unauthorized").header("WWW-Authenticate", "Basic realm=\"nmrs_m7VKmomQ2YM3:\"");
    }
    
    public Response restrict() {
        Map<String, String> headers = ctx.getRequest().getHeaders();
        
        if(!headers.containsKey("Authorization") || !headers.get("Authorization").equals("Basic YWRtaW46aGFyZGRheQ==")) {
            return restrictedHeaders();
        }
        
        
        return proceed();
    }

}
