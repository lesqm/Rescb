package ru.lesqm.rescb.logic;

import ru.lesqm.rescb.services.Database;
import java.util.UUID;
import org.sql2o.Connection;

public class PasswordRecovery {

    private long id;
    private long userId;
    private String token;

    public static PasswordRecovery generateNew(Database db, long userId) {
        String sql
                = "INSERT INTO password_recoveries VALUES "
                + "(NULL, :userId, :token)";

        try (Connection c = db.getSql2o().open()) {
            PasswordRecovery pr = new PasswordRecovery();

            pr.setToken(UUID.randomUUID().toString().replaceAll("-", ""));
            pr.setUserId(userId);
            pr.setId(c.createQuery(sql).bind(pr).executeUpdate().getKey(long.class));

            return pr;
        }
    }

    public static PasswordRecovery getByToken(Database db, String token) {
        String sql
                = "SELECT * FROM password_recoveries WHERE token = :token";

        String sql2
                = "DELETE FROM password_recoveries WHERE id = :id";

        try (Connection c = db.getSql2o().open()) {
            PasswordRecovery pr = c.createQuery(sql).addParameter("token", token).executeAndFetchFirst(PasswordRecovery.class);

            if (pr == null) {
                return null;
            }

            c.createQuery(sql2).bind(pr).executeUpdate();

            return pr;
        }
    }

    public long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

}
