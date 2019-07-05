package RelationalDB;

import java.sql.*;

public class RelationalDatabaseConnection {

    private Connection connection;

    public RelationalDatabaseConnection(String url, String user, String password) throws SQLException,ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        this.connection = DriverManager.getConnection(url + "?user=" + user + "&password=" + password);
    }

    public ResultSet query(String sql) throws SQLException{
        Statement stm = connection.createStatement();
        return stm.executeQuery(sql);

    }

    public void show(ResultSet sql) throws SQLException{
        int col = sql.getMetaData().getColumnCount();
        while(sql.next())
            for( int i = 1; i<=col ; i++ )
                System.out.println(sql.getString(i));

    }

    public ResultSet table( String table ) throws SQLException{
        return this.query( " select * from " + table + ";");

    }

    public void terminateConnection() throws SQLException{
        this.connection.close();
    }
}
