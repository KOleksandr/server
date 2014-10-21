package status.db;

import status.db.entity.IpRequestCount;
import status.db.entity.Redirections;
import status.db.entity.Request;
import java.util.List;

/**
 *
 * @author KOleksander
 */
public interface RequestDAO {
    int countAllRequest();
    int countUniqueRequestsByIp();
    List<IpRequestCount> getIpRequestCounter();
    List<Redirections> getRedirections();
    List<Request> getLastNumberLog(int number);
    boolean request(Request request);
    void closeConnection();
}
