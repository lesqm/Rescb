package ru.lesqm.rescb.logic;

import com.bunjlabs.fugaframework.FugaApp;
import com.bunjlabs.fugaframework.configuration.Configuration;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

public class Database {

    private static final Logger log = LogManager.getLogger(Database.class);

    private final Configuration config;
    private final Sql2o sql2o;

    public Database(FugaApp app) {
        this.config = app.getConfiguration();

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

        try (Connection c = sql2o.open()) {
            c.createQuery(users).executeUpdate();
            c.createQuery(applications).executeUpdate();
            c.createQuery(tezis_files).executeUpdate();
        }
    }

    protected Sql2o getSql2o() {
        return sql2o;
    }
}
