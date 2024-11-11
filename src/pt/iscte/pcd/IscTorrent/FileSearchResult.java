package pt.iscte.pcd.IscTorrent;

public class FileSearchResult {
    private WordSearchMessage wordSearchMessage;
    private String hash;
    private int fileSize;
    private String fileName;
    private String ip;
    private int port;

    public FileSearchResult(WordSearchMessage wordSearchMessage, String hash, int fileSize, String fileName, String ip, int port) {
        this.wordSearchMessage = wordSearchMessage;
        this.hash = hash;
        this.fileSize = fileSize;
        this.fileName = fileName;
        this.ip = ip;
        this.port = port;
    }
}
