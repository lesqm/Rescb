package ru.lesqm.rescb.logic;

import java.util.List;
import org.sql2o.Connection;
import ru.lesqm.rescb.services.Database;

public class TezisHuman {

    private long id = -1;
    private long userId;
    private User user;
    private String title;
    private int form;
    private int section;
    private long fileId;
    private int status = 0;

    public static List<TezisHuman> getAll(Database db) {
        String sql
                = "SELECT * FROM applications";
        try (Connection c = db.getSql2o().open()) {
            List<TezisHuman> list = c.createQuery(sql).executeAndFetch(TezisHuman.class);

            list.stream().forEach((th) -> {
                th.setUser(User.getById(db, th.getUserId()));
            });

            return list;
        }
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public User getUser() {
        return user;
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

    public int getStatus() {
        return status;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setUser(User user) {
        this.user = user;
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

    public void setStatus(int status) {
        this.status = status;
    }
}
