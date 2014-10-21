package status.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author KOleksander
 */
public interface DAOFactory {
    /* Повертає підключення до бази данних */
    public Connection getConnection() throws SQLException;
    
    /* Повертає об'єкт для керування персистентним станом объекта User */
    public RequestDAO getRequestDAO();
}
