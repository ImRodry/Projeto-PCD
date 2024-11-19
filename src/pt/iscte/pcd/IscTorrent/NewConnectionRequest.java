package pt.iscte.pcd.IscTorrent;

import java.io.Serializable;

public class NewConnectionRequest implements Serializable {
    private int port;

    public NewConnectionRequest(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }
}
