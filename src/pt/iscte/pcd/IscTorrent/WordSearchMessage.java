package pt.iscte.pcd.IscTorrent;

import java.io.Serializable;

public class WordSearchMessage implements Serializable {
    private String searchString;

    public WordSearchMessage(String searchString) {
        this.searchString = searchString;
    }

    public String getSearchString() {
        return searchString;
    }
}
