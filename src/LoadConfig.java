import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class LoadConfig {
    private final String defaultSchema = "VIDEOBASE";

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isBlank();
    }

    public ConnectionInfo load(String loc) {
        final String URL;
        final String USER;
        final String PASS;
        final String SCHEMA;

        try {
            Properties config = new Properties();
            FileInputStream file = new FileInputStream(loc);
            config.load(file);
            file.close();

            String IP = config.getProperty("ip");
            String DB = config.getProperty("database");
            USER = config.getProperty("user");
            PASS = config.getProperty("password");
            SCHEMA = config.getProperty("schema");

            if (isNullOrEmpty(IP) || isNullOrEmpty(DB) ||
                 isNullOrEmpty(USER) ||isNullOrEmpty(PASS) || isNullOrEmpty(SCHEMA)) {
                throw new RuntimeException("Missing properties");
            }

            URL = String.format("jdbc:db2://%s/%s", IP, DB);
            return new ConnectionInfo(URL,USER,PASS,SCHEMA);

        } catch (FileNotFoundException e) {
            try {
                FileOutputStream file = new FileOutputStream(loc);
                Properties config = new Properties();

                config.setProperty("ip","");
                config.setProperty("port","");
                config.setProperty("database","");
                config.setProperty("user","");
                config.setProperty("password","");
                config.setProperty("schema", defaultSchema);
                config.store(file,"");

                file.close();

                throw new RuntimeException(e);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


