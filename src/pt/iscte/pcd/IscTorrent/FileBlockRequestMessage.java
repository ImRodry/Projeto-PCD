package pt.iscte.pcd.IscTorrent;

import java.io.Serializable;

public class FileBlockRequestMessage implements Serializable {
    private int hash;
    private int offset;
    private int length;

    public FileBlockRequestMessage(int hash, int offset, int length) {
        this.hash = hash;
        this.offset = offset;
        this.length = length;
    }

    public int getHash() {
        return hash;
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }
}
