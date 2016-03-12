package ru.lesqm.rescb.logic;

import java.util.List;
import org.sql2o.Connection;

public class Application {

    private long id;
    private long userId;
    private String title;
    private int form;
    private int section;
    private long fileId;

    public static Application getById(Database db, long id) {
        String sql = "SELECT * FROM applications WHERE id = :id";
        try (Connection c = db.getSql2o().open()) {
            return c.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(Application.class);
        }
    }

    private static List<Application> getByUserId(Database db, long userId) {
        String sql = "SELECT * FROM applications WHERE userId = :userId";
        try (Connection c = db.getSql2o().open()) {
            return c.createQuery(sql)
                    .addParameter("userId", userId)
                    .executeAndFetch(Application.class);
        }
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public int getForm() {
        return form;
    }

    public int getSection() {
        return section;
    }

    public long getFileId() {
        return fileId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setForm(int form) {
        this.form = form;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }
}
