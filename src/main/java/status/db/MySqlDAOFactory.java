package status.db;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author KOleksander
 */
public class MySqlDAOFactory implements DAOFactory {
    
    private String user = "root";//Логін користувача 
    private String password = "1111";//Пароль користувача 
    private String url = "jdbc:mysql://localhost:3306/server";//URL адреса 
    private String driver = "com.mysql.jdbc.Driver";//Ім'я драйвера 
    
    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public RequestDAO getRequestDAO() {
        RequestDAO requestDAO = null; 
        try {
            requestDAO = new MySqlRequestDAO(getConnection());
        } catch (SQLException ex) {
            Logger.getLogger(MySqlDAOFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return requestDAO;
    }

    
}
