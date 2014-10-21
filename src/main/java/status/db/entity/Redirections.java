package status.db.entity;

/**
 *
 * @author KOleksander
 */
public class Redirections {
    private String redirectUrl;

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public int getCountRedirect() {
        return countRedirect;
    }

    public void setCountRedirect(int countRedirect) {
        this.countRedirect = countRedirect;
    }
    private int countRedirect;

    @Override
    public String toString() {
        return "Redirections:\n\t"
                + "uri:" + redirectUrl + "\n\t"
                + "redirections:" + countRedirect + "\n\t"; 
    }
    
    public Redirections(String redirectUrl, int countRedirect) {
        this.redirectUrl = redirectUrl;
        this.countRedirect = countRedirect;
    }
}
