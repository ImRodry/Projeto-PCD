package pt.iscte.pcd.IscTorrent;

import java.io.Serializable;

public class WordSearchMessage implements Serializable {
    private String word;
    private int originPort;

    public WordSearchMessage(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }
}
