package pt.iscte.pcd.IscTorrent;

import java.io.Serializable;

public class WordSearchMessage implements Serializable {
    private String searchString;
    private int originPort;

    public WordSearchMessage(String searchString, int originPort) {
        this.searchString = searchString;
        this.originPort = originPort;
    }

    public String getSearchString() {
        return searchString;
    }

    public int getOriginPort() {
        return originPort;
    }
}
