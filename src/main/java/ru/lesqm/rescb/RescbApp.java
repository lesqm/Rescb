package ru.lesqm.rescb;

import com.bunjlabs.fugaframework.FugaApp;

public class RescbApp extends FugaApp {

    @Override
    public void prepare() {
        getRouter().load("config/default.routes");
        getConfiguration().load("config/default.properties");
    }

    public static void main(String[] args) throws Exception {
        new RescbApp().start();
    }

}
