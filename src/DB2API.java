import java.sql.*;

public class DB2API {
    private java.sql.Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    private static final boolean queries = false;

    public void connect(ConnectionInfo info) {
        connect(info.URL(), info.USER(), info.PASS());
        schema(info.SCHEMA());
    }

    public void connect(String url, String user, String pass) {
        try {
            System.out.println("Connecting...");
            DriverManager.registerDriver(new com.ibm.db2.jcc.DB2Driver());
            connection = DriverManager.getConnection(url, user, pass);
            statement = connection.createStatement();
            System.out.println("Connected to database server\n");
        } catch(SQLException s){
//            s.printStackTrace();
            throw new RuntimeException("Couldn't connect");
        }
    }

    public void schema(String schema) {
        try {
            resultSet = statement.executeQuery("SELECT schemaname FROM syscat.schemata WHERE schemaname = '" + schema +"'");
            if (!resultSet.next()) {
                throw new SQLException();
            }
            statement.executeUpdate("SET SCHEMA '" + schema + "'");
            if (queries) {
                System.out.println("[Schema set to " + schema +"]\n");
            }
        } catch (SQLException s) {
//            s.printStackTrace();
            throw new RuntimeException("Couldn't set schema");
        }
    }
    public void disconnect() {
        try {
            if (null != connection) {
                if(resultSet != null) {
                    resultSet.close();
                }
                statement.close();
                connection.close();
            }
        } catch (SQLException s) {
            throw new RuntimeException("Couldn't disconnect");
        }
    }

    public void select(String stmnt, boolean labels) throws SQLException {
            resultSet = statement.executeQuery(stmnt);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int column = metaData.getColumnCount();

            StringBuilder result = new StringBuilder();

            if (queries) {
                System.out.println("[Executing query: " + stmnt + "]");
            }

            if (!resultSet.next()) {
                System.out.println("\033[3mempty\033[0m\n");
            } else {
                if (labels) {
                    for (int i = 1; i <= column; i++) {
                        result.append(metaData.getColumnLabel(i));

                        if (i == column) result.append(" \n");
                        else result.append(", ");
                    }
                }
                do {
                    for (int i = 1; i <= column; i++) {

                        result.append(resultSet.getString(i));

                        if (i == column) result.append(" \n");
                        else result.append(", ");
                    }
                }
                while (resultSet.next());
                System.out.println(result);
            }
    }

    public void select(String stmnt) throws SQLException {
        select(stmnt,false);
    }

    void update(String stmnt) throws SQLException {
            int changed = statement.executeUpdate(stmnt);
            if (changed != 1) {
                throw new SQLException("Changes couldn't be made, possibly a row out of bounds");
            }
            if (queries) {
                System.out.println("[Executed query: " + stmnt + "]\n");
            }

    }
}
