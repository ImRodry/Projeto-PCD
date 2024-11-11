package pt.iscte.pcd.IscTorrent;

public class FileBlockRequestMessage {
    private String hash;
    private int blockIndex;
    private int blockSize;

    public FileBlockRequestMessage(String hash, int blockIndex, int blockSize) {
        this.hash = hash;
        this.blockIndex = blockIndex;
        this.blockSize = blockSize;
    }

    public String getFileName() {
        return hash;
    }

    public int getBlockIndex() {
        return blockIndex;
    }

    public int getBlockSize() {
        return blockSize;
    }
}
