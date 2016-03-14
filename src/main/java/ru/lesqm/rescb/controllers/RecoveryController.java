package ru.lesqm.rescb.controllers;

import com.bunjlabs.fugaframework.dependency.Inject;
import com.bunjlabs.fugaframework.foundation.Controller;
import com.bunjlabs.fugaframework.foundation.Response;
import com.bunjlabs.fugaframework.templates.TemplateNotFoundException;
import com.bunjlabs.fugaframework.templates.TemplateRenderException;
import java.util.Random;
import ru.lesqm.rescb.logic.PasswordRecovery;
import ru.lesqm.rescb.logic.User;
import ru.lesqm.rescb.services.Database;
import ru.lesqm.rescb.services.MailgunService;
import ru.lesqm.rescb.utils.HashUtils;

public class RecoveryController extends Controller {
    
    @Inject
    public Database db;
    
    @Inject
    public MailgunService ms;
    
    public Response forgetPassword() throws TemplateNotFoundException, TemplateRenderException {
        if (ctx.getRequest().getQuery().containsKey("email")
                && !ctx.getRequest().getQuery().get("email").isEmpty()) {
            
            String email = ctx.getRequest().getQuery().get("email").get(0);
            
            User u = User.getByEmail(db, email);
            
            if (u == null) {
                return ok(view("recovery/done.html"));
            }
            
            PasswordRecovery pr = PasswordRecovery.generateNew(db, u.getId());
            
            ctx.put("recovery", pr);
            ctx.put("user", u);
            
            ms.sendNoReply(email, "Восстановление пароля", view("mail/recovery.html"));
            return ok(view("recovery/done.html"));
        } else {
            return ok(view("recovery/send.html"));
        }
    }
    
    public Response recover(String token) throws TemplateNotFoundException, TemplateRenderException {
        PasswordRecovery pr = PasswordRecovery.getByToken(db, token);
        
        if (pr == null) {
            return notFound();
        }
        
        User u = User.getById(db, pr.getUserId());
        
        if (u == null) {
            return notFound();
        }
        
        String password = generatePassword();
        u.setPassword(HashUtils.sha256String(password));
        
        u.save(db);
        
        ctx.put("user", u);
        ctx.put("password", password);
        
        ms.sendNoReply(u.getEmail(), "Восстановление пароля", view("mail/recovered.html"));
        
        return ok(view("recovery/recovered.html"));
    }
    
    private static String generatePassword() {
        Random rnd = new Random();
        int length = 12;
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(rnd.nextInt(characters.length()));
        }
        return new String(text);
    }
    
}
