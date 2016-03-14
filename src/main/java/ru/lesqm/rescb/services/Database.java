package ru.lesqm.rescb.services;

import com.bunjlabs.fugaframework.configuration.Configuration;
import com.bunjlabs.fugaframework.dependency.Inject;
import com.bunjlabs.fugaframework.services.Service;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

public class Database extends Service {

    private static final Logger log = LogManager.getLogger(Database.class);

    private Sql2o sql2o;

    @Inject
    public Configuration config;

    @Override
    public void onCreate() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.get("rescb.db.jdbcurl"));
        hikariConfig.setUsername(config.get("rescb.db.username"));
        hikariConfig.setPassword(config.get("rescb.db.password"));

        HikariDataSource ds = new HikariDataSource(hikariConfig);

        this.sql2o = new Sql2o(ds);
        init();
    }    

    private void init() {
        String users
                = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                + "firstname VARCHAR(255) NOT NULL,"
                + "lastname VARCHAR(255) NOT NULL,"
                + "middlename VARCHAR(255) NOT NULL,"
                + "email VARCHAR(255) NOT NULL,"
                + "password VARCHAR(255) NOT NULL,"
                + "country VARCHAR(255) NOT NULL,"
                + "city VARCHAR(255) NOT NULL,"
                + "job VARCHAR(255) NOT NULL,"
                + "position VARCHAR(255) NOT NULL,"
                + "degree VARCHAR(255) NOT NULL,"
                + "contactphone VARCHAR(255) NOT NULL"
                + ")";
        String applications
                = "CREATE TABLE IF NOT EXISTS applications ("
                + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                + "userId INTEGER NOT NULL,"
                + "title TEXT NOT NULL,"
                + "form INTEGER NOT NULL,"
                + "section INTEGER NOT NULL,"
                + "fileId INTEGER NOT NULL,"
                + "status INTEGER NOT NULL DEFAULT 0"
                + ")";
        String tezis_files
                = "CREATE TABLE IF NOT EXISTS tezis_files ("
                + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                + "hash VARCHAR(255) NOT NULL"
                + ")";

        String password_recoveries
                = "CREATE TABLE IF NOT EXISTS password_recoveries ("
                + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                + "userId INTEGER NOT NULL,"
                + "token VARCHAR(255) NOT NULL"
                + ")";

        try (Connection c = sql2o.open()) {
            c.createQuery(users).executeUpdate();
            c.createQuery(applications).executeUpdate();
            c.createQuery(tezis_files).executeUpdate();

            c.createQuery(password_recoveries).executeUpdate();
        }
    }

    public Sql2o getSql2o() {
        return sql2o;
    }
}
