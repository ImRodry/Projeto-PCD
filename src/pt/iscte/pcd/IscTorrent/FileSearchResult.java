package pt.iscte.pcd.IscTorrent;

import java.io.Serializable;

public class FileSearchResult implements Serializable {
    private WordSearchMessage wordSearchMessage;
    private int hash;
    private int fileSize;
    private String fileName;
    private int port;

    public FileSearchResult(WordSearchMessage wordSearchMessage, int hash, int fileSize, String fileName, int port) {
        this.wordSearchMessage = wordSearchMessage;
        this.hash = hash;
        this.fileSize = fileSize;
        this.fileName = fileName;
        this.port = port;
    }

    public WordSearchMessage getWordSearchMessage() {
        return wordSearchMessage;
    }

    public int getHash() {
        return hash;
    }

    public int getFileSize() {
        return fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public int getPort() {
        return port;
    }

}
