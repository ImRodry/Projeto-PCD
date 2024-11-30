package pt.iscte.pcd.IscTorrent;

import java.io.Serializable;

public class FileBlockRequestMessage implements Serializable {
    private int hash;
    private int blockIndex;
    private int blockSize;

    public FileBlockRequestMessage(int hash, int blockIndex, int blockSize) {
        this.hash = hash;
        this.blockIndex = blockIndex;
        this.blockSize = blockSize;
    }

    public int getHash() {
        return hash;
    }

    public int getBlockIndex() {
        return blockIndex;
    }

    public int getBlockSize() {
        return blockSize;
    }
}
