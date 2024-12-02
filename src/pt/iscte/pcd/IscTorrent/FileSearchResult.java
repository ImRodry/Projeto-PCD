package pt.iscte.pcd.IscTorrent;

import java.io.Serializable;

public class FileSearchResult implements Serializable {
    private WordSearchMessage wordSearchMessage;
    private int hash;
    private long fileSize;
    private String fileName;
    private String hostname;
    private int originPort;

    public FileSearchResult(WordSearchMessage wordSearchMessage, int hash, long fileSize, String fileName,
            String hostname, int originPort) {
        this.wordSearchMessage = wordSearchMessage;
        this.hash = hash;
        this.fileSize = fileSize;
        this.fileName = fileName;
        this.hostname = hostname;
        this.originPort = originPort;
    }

    public WordSearchMessage getWordSearchMessage() {
        return wordSearchMessage;
    }

    public int getHash() {
        return hash;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public String getHostname() {
        return hostname;
    }

    public int getOriginPort() {
        return originPort;
    }
}
