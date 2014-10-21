package status.db;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import status.db.entity.IpRequestCount;
import status.db.entity.Redirections;
import status.db.entity.Request;

/**
 *
 * @author KOleksander
 */
public class MySqlRequestDAO implements RequestDAO {

    private final Connection connection;
    private Statement stmt;
    
    @Override
    public int countAllRequest() {
        int count = 0;
        try {
            stmt = connection.createStatement();
            ResultSet result = stmt.executeQuery("select count(`request_id`) `result` from `request`");
            while (result.next()) {
                count = result.getInt("result");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySqlRequestDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return count;
    }

    @Override
    public int countUniqueRequestsByIp() {
        int count = 0;
        
        try {
            stmt = connection.createStatement();
            ResultSet result = stmt.executeQuery("select count(distinct uri) result from request\n" +
            "where src_ip = '/127.0.0.1'");
            while (result.next()) {
                count = result.getInt("result");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySqlRequestDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return count;
    }

    @Override
    public List<IpRequestCount> getIpRequestCounter() {
        List<IpRequestCount> list = new ArrayList<>();
        try {
            IpRequestCount ipRequestCount;
            stmt = connection.createStatement();
            ResultSet result = stmt.executeQuery("select distinct src_ip ip from request");
            while (result.next()) {
                ipRequestCount = new IpRequestCount(result.getString("ip"), 0, null);
                list.add(ipRequestCount);
            }
            for (IpRequestCount count: list) {
                result = stmt.executeQuery("select count(src_ip) ip, max(timestamp) timestamp from request where src_ip = '" + count.getAddress() + "'");
                result.next();
                count.setRequests(result.getInt("ip"));
                count.setTimestamp(result.getString("timestamp"));
            }
        } catch (SQLException | UnknownHostException ex) {
            Logger.getLogger(MySqlRequestDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    @Override
    public List<Redirections> getRedirections() {
        List<Redirections> list = new ArrayList<>();
        try {
            stmt = connection.createStatement();
            ResultSet result = stmt.executeQuery("select distinct uri from request where uri like '/redirect?url=%'");
            while (result.next()) {
                list.add(new Redirections(result.getString("uri"), 0));
            }
            for (Redirections count: list) {
                result = stmt.executeQuery("select count(uri) uriCount from request where uri = '" + count.getRedirectUrl() + "'");
                result.next();
                count.setCountRedirect(result.getInt("uriCount"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySqlRequestDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    @Override
    public List<Request> getLastNumberLog(int number) {
        List<Request> list = new ArrayList<>();
         try {
            stmt = connection.createStatement();
            ResultSet result = stmt.executeQuery("select src_ip,uri,timestamp,send_bites,received_bytes,speed from request order by request_id desc limit " + number);
            while (result.next()) {
                list.add(new Request(result.getString("src_ip"), result.getString("uri"), result.getString("timestamp"),result.getInt("send_bites"), result.getInt("received_bytes"), result.getDouble("speed")));
            }
        } catch (SQLException | UnknownHostException ex) {
            Logger.getLogger(MySqlRequestDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public MySqlRequestDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean request(Request request) {
        boolean result;
        try {
            stmt = connection.createStatement();
            stmt.execute("insert into request(src_ip, uri, timestamp, send_bites, received_bytes, speed)\n" +
                            "values('" + request.getAddress() + "','" + request.getUri() + "',now()," + request.getSend_bytes() +
                            "," + request.getReceived_bytes() + "," + request.getSpeed() + ")");
            result = true;
        } catch (SQLException ex) {
            result = false;
            Logger.getLogger(MySqlRequestDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    
    @Override
    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(MySqlRequestDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
