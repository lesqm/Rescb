package ru.lesqm.rescb;

import com.bunjlabs.fugaframework.FugaApp;
import ru.lesqm.rescb.logic.Database;

public class RescbApp extends FugaApp {

    private Database db;

    @Override
    public void prepare() {
        getRouter().load("config/default.routes");
        getConfiguration().load("config/default.properties");
        
        db = new Database(this);
        
        getDependencyManager().registerDependency(db);

    }

    public static void main(String[] args) throws Exception {
        new RescbApp().start();
    }

}
