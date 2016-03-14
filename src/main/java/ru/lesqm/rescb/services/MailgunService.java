package ru.lesqm.rescb.services;

import com.bunjlabs.fugaframework.configuration.Configuration;
import com.bunjlabs.fugaframework.dependency.Inject;
import com.bunjlabs.fugaframework.services.Service;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MailgunService extends Service {

    private static final Logger log = LogManager.getLogger(MailgunService.class);
    
    @Inject
    public Configuration conf;

    public void sendNoReply(String email, String subject, String body) {
        Unirest.post(conf.get("rescb.mailgun.apiurl"))
                .field("from", "Конференция РЭСХС-22 <noreply@rescb.ru>")
                .field("to", email)
                .field("subject", subject)
                .field("text", body)
                .basicAuth("api", conf.get("rescb.mailgun.privatekey"))
                .asStringAsync();
        
        log.debug("Mail sent");
        
    }

}
