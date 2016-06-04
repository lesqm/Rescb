package ru.lesqm.rescb.logic;

import ru.lesqm.rescb.services.Database;
import java.util.List;
import org.sql2o.Connection;

public class Application {

    private long id = -1;
    private long userId;
    private String title;
    private int form;
    private int section;
    private long fileId;
    private int status = 0;

    public static Application getById(Database db, long id) {
        String sql = "SELECT * FROM applications WHERE id = :id";
        try (Connection c = db.getSql2o().open()) {
            return c.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(Application.class);
        }
    }

    public static List<Application> getByUserId(Database db, long userId) {
        String sql = "SELECT * FROM applications WHERE userId = :userId";
        try (Connection c = db.getSql2o().open()) {
            return c.createQuery(sql)
                    .addParameter("userId", userId)
                    .executeAndFetch(Application.class);
        }
    }

    public static void put(Database db, Application app) {
        String sql = "INSERT INTO applications VALUES"
                + "(NULL, :userId, :title, :form, :section, :fileId, :status)";
        try (Connection c = db.getSql2o().open()) {
            app.setId(c.createQuery(sql).bind(app).executeUpdate().getKey(long.class));
        }
    }

    public static void update(Database db, Application app) {
        String sql = "UPDATE applications SET "
                + "userId = :userId, title = :title, form = :form, section = :section, fileId = :fileId, status = :status";
        try (Connection c = db.getSql2o().open()) {
            c.createQuery(sql).bind(app).executeUpdate();
        }
    }

    public static void delete(Database db, Application app) {
        String sql = "DELETE applications WHERE id = :id";
        try (Connection c = db.getSql2o().open()) {
            c.createQuery(sql).bind(app).executeUpdate();
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
