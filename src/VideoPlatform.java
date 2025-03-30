import java.sql.SQLException;

public class VideoPlatform {

    private final DB2API db2;
    private final ConnectionInfo info;

    VideoPlatform() {
        String path = "connection.properties";

        db2 = new DB2API();
        LoadConfig cfg = new LoadConfig();
        info = cfg.load(path);
    }

    public void connect() {
        db2.connect(info);
    }
    public void disconnect() {
        db2.disconnect();
    }

    public void show(String what) throws SQLException, IllegalArgumentException {
        switch (what.toLowerCase()) {
            //tables
            case "users" -> db2.select("SELECT USER_ID,USERNAME FROM USERS",true);
            case "cameras" -> db2.select("SELECT * FROM CAMERAS");
            case "videos" -> db2.select("SELECT * FROM VIDEOS",true);
            case "deleted_users" -> db2.select("SELECT * FROM DELETED",true);
            // views
            case "user_overview" -> db2.select("SELECT * FROM V_USER_STATS",true);
            case "camera_usage" -> db2.select("SELECT * FROM V_CAMERA_USAGE",true);
            default -> throw new IllegalArgumentException("Unknown argument\n");
        }
    }

    public void user(String what, String userID) throws SQLException, IllegalArgumentException {
        switch (what.toLowerCase()) {
            case "username" -> db2.select("VALUES " + info.SCHEMA() + ".F_USERNAME(" + userID + ")");
            case "overview" -> db2.select("SELECT * FROM TABLE(" + info.SCHEMA() + ".F_USER_STATS(" + userID + ")) T",true);
            case "videos" -> db2.select("SELECT * FROM TABLE(" + info.SCHEMA() + ".F_USER_VIDEOS(" + userID + ")) T",true);
            case "subscriptions" -> db2.select("SELECT * FROM TABLE(" + info.SCHEMA() + ".F_USER_SUBSCRIPTIONS(" + userID + ")) T",true);
            default -> throw new IllegalArgumentException("Unknown argument\n");
        }
    }

    public void register(String username, String password) throws SQLException {
        db2.update("INSERT INTO USERS " +
                "VALUES (NEXTVAL FOR S_USER,'" + username + "','" + password + "')");
        System.out.println("User " + username + "was registered");
    }
    public void upload(String userID, String title, String length, String cam_man, String cam_mod) throws SQLException {
        String buffer;
        if(!cam_man.isEmpty() && !cam_mod.isEmpty()) {
            buffer = ",'" + cam_man + "','" + cam_mod + "'";
        } else {
            buffer = ", null, null";
        }

        db2.update("INSERT INTO V_VIDEOS VALUES (NEXTVAL FOR S_VIDEO,'" + title + "'," + length + "," + userID + buffer +")");
        System.out.println("Video " + title + " was uploaded user by " + userID);
    }
    public void upload(String userID, String title, String length) throws SQLException {
        upload(userID,title,length,"","");
    }

    public void get(String what, String ID) throws SQLException, IllegalArgumentException {
        switch (what.toLowerCase()) {
            case "user" -> db2.select("SELECT USERNAME FROM USERS WHERE USER_ID = " + ID);
            case "video" -> db2.select("SELECT TITLE,LENGTH,USER_ID,CAM_MANUFACTURER,CAM_MODEL FROM VIDEOS WHERE VIDEO_ID = " + ID);
            default -> throw new IllegalArgumentException("Unknown argument\n");
        }
    }

    public void delete(String what, String ID) throws SQLException, IllegalArgumentException {
        switch (what.toLowerCase()) {
            case "user" -> {
                db2.update("DELETE FROM USERS WHERE USER_ID = " + ID);
                System.out.println("User " + ID + "was deleted");
            }
            case "video" -> {
                db2.update("DELETE FROM VIDEOS WHERE VIDEO_ID = " + ID);
                System.out.println("Video " + ID + "was deleted");
            }
            default -> throw new IllegalArgumentException("Unknown argument\n");
        }
    }

    public void ban(String userID, String reason) throws SQLException {
        //deleteUser(userID);
        delete("user", userID);
        String buffer = "";

        if(!reason.isEmpty()) buffer = " for " + reason;

        db2.update("UPDATE DELETED SET REASON = 'BANNED" + buffer + "' WHERE USER_ID = " + userID);
        System.out.println("User " + userID + " was banned" + buffer);
    }
    public void ban(String userID) throws SQLException {
        ban(userID,"");
    }

    public void subscribe(String userID, String creatorID) throws SQLException {
        db2.update("INSERT INTO SUBSCRIBERS " +
                "VALUES ("+ userID +","+ creatorID +")");
        System.out.println(userID + " subscribed to " + creatorID);
    }

    public void unsubscribe(String userID, String creatorID) throws SQLException {
        db2.update("DELETE FROM SUBSCRIBERS " +
                "WHERE SUBSCRIBER_ID = " + userID + " " +
                "AND SUBSCRIBED_TO_ID = " + creatorID);
        System.out.println(userID + " unsubscribed from " + creatorID);
    }

    public void rename(String what, String ID, String newName) throws SQLException {
        switch (what.toLowerCase()) {
            case "user" -> {
                db2.update("UPDATE USERS SET USERNAME = '" + newName + "' " +
                        "WHERE USER_ID = " + ID);
                System.out.println("User " + ID + " was renamed to " + newName);
            }
            case "video" -> {
                db2.update("UPDATE VIDEOS SET TITLE = '" + newName + "' " +
                        "WHERE VIDEO_ID = " + ID);
                System.out.println("Video " + ID + " was renamed to " + newName);
            }
            default -> throw new IllegalArgumentException("Unknown argument\n");
        }
    }
}
