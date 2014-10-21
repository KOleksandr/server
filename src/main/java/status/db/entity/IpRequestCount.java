package status.db.entity;

import java.net.UnknownHostException;

/**
 *
 * @author KOleksander
 */
public class IpRequestCount {
    private String address;
    private int requests;
    private String timestamp;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) throws UnknownHostException {
        this.address = address;
    }

    public int getRequests() {
        return requests;
    }

    public void setRequests(int requests) {
        this.requests = requests;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "IpRequestCount:\n\t"
                + "Address:" + address + "\n\t"
                + "Requests:" + requests + "\n\t"
                + "Timestamp:" + timestamp + "\n\t";  
    }
    

    public IpRequestCount(String address, int requests, String timestamp) throws UnknownHostException {
        setAddress(address);
        this.requests = requests;
        this.timestamp = timestamp;
    }
}
