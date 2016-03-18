package ru.lesqm.rescb.logic;

import java.sql.Date;
import java.util.List;
import ru.lesqm.rescb.services.Database;
import org.sql2o.Connection;

public class User {

    private long id = -1;
    private String firstname;
    private String lastname;
    private String middlename;
    private String email;
    private String password;
    private String country;
    private String city;
    private String job;
    private String position;
    private String degree;
    private String contactphone;

    private int gender = 0;
    private Date birthday = null;

    public static User getById(Database db, long id) {
        String sql = "SELECT * FROM users WHERE id = :id";
        try (Connection c = db.getSql2o().open()) {
            return c.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(User.class);
        }
    }

    public static List<User> getAll(Database db) {
        String sql = "SELECT * FROM users";
        try (Connection c = db.getSql2o().open()) {
            return c.createQuery(sql)
                    .executeAndFetch(User.class);
        }
    }

    public static User getByEmailPassword(Database db, String email, String password) {
        String sql = "SELECT * FROM users WHERE email = :email AND password = :password";
        try (Connection c = db.getSql2o().open()) {
            return c.createQuery(sql)
                    .addParameter("email", email)
                    .addParameter("password", password)
                    .executeAndFetchFirst(User.class);
        }
    }

    public static User getByEmail(Database db, String email) {
        String sql = "SELECT * FROM users WHERE email = :email";
        try (Connection c = db.getSql2o().open()) {
            return c.createQuery(sql)
                    .addParameter("email", email)
                    .executeAndFetchFirst(User.class);
        }
    }

    public static void put(Database db, User u) {
        String sql = "INSERT INTO users VALUES"
                + "(NULL, :firstname, :lastname, :middlename, :email, :password,"
                + ":country, :city, :job, :position, :degree, :contactphone, :gender, :birthday)";
        try (Connection c = db.getSql2o().open()) {
            u.setId(c.createQuery(sql).bind(u).executeUpdate().getKey(long.class));
        }
    }

    public static void update(Database db, User u) {
        String sql = "UPDATE users SET "
                + "firstname = :firstname, lastname = :lastname, middlename = :middlename, email = :email, password = :password,"
                + "country = :country, city = :city, job = :job, position = :position, degree = :degree, contactphone = :contactphone,"
                + "gender = :gender, birthday = :birthday WHERE id = :id";
        try (Connection c = db.getSql2o().open()) {
            c.createQuery(sql).bind(u).executeUpdate();
        }
    }

    public void save(Database db) {
        if (id < 0) {
            put(db, this);
        } else {
            update(db, this);
        }
    }

    public boolean validate() {
        return (firstname != null && !firstname.isEmpty())
                && (lastname != null && !lastname.isEmpty())
                && (middlename != null && !middlename.isEmpty())
                && (email != null && !email.isEmpty())
                && (password != null && !password.isEmpty())
                && (country != null && !country.isEmpty())
                && (city != null && !city.isEmpty())
                && (job != null && !job.isEmpty())
                && (position != null && !position.isEmpty())
                && (degree != null && !degree.isEmpty())
                && (contactphone != null && !contactphone.isEmpty());
    }

    public long getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getPosition() {
        return position;
    }

    public String getDegree() {
        return degree;
    }

    public String getContactphone() {
        return contactphone;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public void setContactphone(String contactphone) {
        this.contactphone = contactphone;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public int getGender() {
        return gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
