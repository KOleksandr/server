package status.db.entity;

import java.net.UnknownHostException;

/**
 *
 * @author KOleksander
 */
public class Request {
    private String address;
    private String uri;
    private String timestamp;
    private int send_bytes;
    private int received_bytes;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) throws UnknownHostException {
        this.address = address;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getSend_bytes() {
        return send_bytes;
    }

    public void setSend_bytes(int send_bytes) {
        this.send_bytes = send_bytes;
    }

    public int getReceived_bytes() {
        return received_bytes;
    }

    public void setReceived_bytes(int received_bytes) {
        this.received_bytes = received_bytes;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
    private double speed;

    @Override
    public String toString() {
        return "Request:\n\t"
                + "Address:" + address + "\n\t"
                + "uri:" + uri + "\n\t"
                + "Timestamp:" + timestamp + "\n\t"
                + "Send bytes:" + send_bytes + "\n\t"
                + "Received bytes:" + received_bytes + "\n\t"
                + "Speed:" + speed + "\n"; 
    }

    public Request() {
    }

    
    public Request(String address, String uri, String timestamp, int send_bytes, int received_bytes, double speed) throws UnknownHostException {
        setAddress(address);
        this.uri = uri;
        this.timestamp = timestamp;
        this.send_bytes = send_bytes;
        this.received_bytes = received_bytes;
        this.speed = speed;
    }
}
