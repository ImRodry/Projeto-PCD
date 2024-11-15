package pt.iscte.pcd.IscTorrent;

public class NewConnectionRequest {
    private String ip;
    private int port;
    private Node node;

    public NewConnectionRequest(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public Node getNode() {
        return node;
    }
}
