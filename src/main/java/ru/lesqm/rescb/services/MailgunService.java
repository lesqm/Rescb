package ru.lesqm.rescb.services;

import com.bunjlabs.fugaframework.configuration.Configuration;
import com.bunjlabs.fugaframework.dependency.Inject;
import com.bunjlabs.fugaframework.services.Service;
import com.mashape.unirest.http.Unirest;

public class MailgunService extends Service {

    @Inject
    public Configuration conf;

    public void sendNoReply(String email, String subject, String body) {
        Unirest.post(conf.get("rescb.mailgun.apiurl"))
                .field("from", conf.get("rescb.mailgun.from"))
                .field("to", email)
                .field("subject", subject)
                .field("text", body)
                .basicAuth("api", conf.get("rescb.mailgun.privatekey"))
                .asJsonAsync();
    }

}
