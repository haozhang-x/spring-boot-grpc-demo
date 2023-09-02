package request;

/**
 * reload long connection request.
 */
public class ServerReloadRequest extends Request {

    int reloadCount = 0;

    String reloadServer;

    /**
     * Getter method for property <tt>reloadCount</tt>.
     *
     * @return property value of reloadCount
     */
    public int getReloadCount() {
        return reloadCount;
    }

    /**
     * Setter method for property <tt>reloadCount</tt>.
     *
     * @param reloadCount value to be assigned to property reloadCount
     */
    public void setReloadCount(int reloadCount) {
        this.reloadCount = reloadCount;
    }

    public String getReloadServer() {
        return reloadServer;
    }

    public void setReloadServer(String reloadServer) {
        this.reloadServer = reloadServer;
    }

    @Override
    public String getModule() {
        return null;
    }
}
