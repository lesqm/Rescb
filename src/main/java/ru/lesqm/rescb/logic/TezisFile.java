package ru.lesqm.rescb.logic;

import ru.lesqm.rescb.services.Database;
import org.sql2o.Connection;

public class TezisFile {

    private long id = -1;
    private String hash;

    public static TezisFile getById(Database db, long id) {
        String sql = "SELECT * FROM tezis_files WHERE id = :id";
        try (Connection c = db.getSql2o().open()) {
            return c.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(TezisFile.class);
        }
    }

    public static TezisFile getByHash(Database db, String hash) {
        String sql = "SELECT * FROM tezis_files WHERE hash = :hash";
        try (Connection c = db.getSql2o().open()) {
            return c.createQuery(sql)
                    .addParameter("hash", hash)
                    .executeAndFetchFirst(TezisFile.class);
        }
    }

    public static void put(Database db, TezisFile t) {
        String sql = "INSERT INTO tezis_files VALUES"
                + "(NULL, :hash)";
        try (Connection c = db.getSql2o().open()) {
            t.setId(c.createQuery(sql).bind(t).executeUpdate().getKey(long.class));
        }
    }

    public static void update(Database db, TezisFile t) {
        String sql = "UPDATE tezis_files SET "
                + "hash = :hash";
        try (Connection c = db.getSql2o().open()) {
            c.createQuery(sql).bind(t).executeUpdate();
        }
    }

    public void save(Database db) {
        if (id < 0) {
            put(db, this);
        } else {
            update(db, this);
        }
    }

    public long getId() {
        return id;
    }

    public String getHash() {
        return hash;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
