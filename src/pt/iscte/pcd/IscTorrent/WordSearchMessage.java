package pt.iscte.pcd.IscTorrent;

import java.io.Serializable;

public class WordSearchMessage implements Serializable {
    private String word;
    private int originPort;

    public WordSearchMessage(String word, int originPort) {
        this.word = word;
        this.originPort = originPort;
    }

    public String getWord() {
        return word;
    }

    public int getOriginPort() {
        return originPort;
    }
}
