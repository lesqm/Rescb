package ru.lesqm.rescb;

import com.bunjlabs.fugaframework.FugaApp;
import ru.lesqm.rescb.services.Database;
import ru.lesqm.rescb.services.MailgunService;

public class RescbApp extends FugaApp {

    @Override
    public void prepare() {
        getRouter().load("config/default.routes");
        getConfiguration().load("config/default.properties");

        getServiceManager().registerService(Database.class);
        getServiceManager().registerService(MailgunService.class);

    }

    public static void main(String[] args) throws Exception {
        new RescbApp().start();
    }

}
