package pt.iscte.pcd.IscTorrent;

import java.io.Serializable;

public class NewConnectionRequest implements Serializable {
    private int port;
    private Node node;

    public NewConnectionRequest(int port, Node node) {
        this.port = port;
        this.node = node;
    }

    public int getPort() {
        return port;
    }

    public Node getNode() {
        return node;
    }
}
