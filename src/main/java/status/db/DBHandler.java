package status.db;

import java.util.LinkedList;
import java.util.Queue;



/**
 *
 * @author KOleksander
 */
public class DBHandler {
    private static final Queue<RequestDAO> queue = new LinkedList<>();
    static {
        for (int i = 0; i < 100; i++) {
            queue.add(new MySqlDAOFactory().getRequestDAO());
        }
    }

    
    
    public synchronized static RequestDAO factoryInstance() throws InterruptedException {
        if (queue.size() != 0) {
            
            return (RequestDAO)queue.poll();
        } else {
            Thread.sleep(1);
            return factoryInstance();
        }
    }
    
    public synchronized static void end() {
        queue.add(new MySqlDAOFactory().getRequestDAO());
    }
    
    private DBHandler() {
    
    }
}
