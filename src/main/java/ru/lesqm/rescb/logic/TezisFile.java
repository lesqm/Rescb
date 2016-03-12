package ru.lesqm.rescb.logic;

import org.sql2o.Connection;

public class TezisFile {

    private long id;
    private String hash;

    public static TezisFile getById(Database db, long id) {
        String sql = "SELECT * FROM tezis_files WHERE id = :id";
        try (Connection c = db.getSql2o().open()) {
            return c.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(TezisFile.class);
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
