package pt.iscte.pcd.IscTorrent;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Node {
    private Map<Integer, File> files;
    private ServerSocket serverSocket;
    private int port;
    private List<ConnectionHandler> connections = new ArrayList<>();

    public Node(String path, int port) {
        this.port = port;
        for (File f : new File(path).listFiles((File file) -> file.isFile())) {
            try {
                byte[] fileContents = Files.readAllBytes(f.toPath());
                byte[] hash = MessageDigest.getInstance("SHA-256").digest(fileContents);
                files.put(new BigInteger(1, hash).intValue(), f);
            } catch (IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

    public Map<Integer, File> getFiles() {
        return files;
    }

    public List<ConnectionHandler> getConnections() {
        return connections;
    }

    public void runServer() {
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                waitForConnection();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null)
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public void readSearchRequest(WordSearchMessage message) {
        WordSearchMessage wordSearchMessage = (WordSearchMessage) message;
        for (Entry<Integer, File> entry : files.entrySet()) {
            if (entry.getValue().getName().contains(wordSearchMessage.getWord())) {
                List<FileSearchResult> result = new ArrayList<>();
                result.add(new FileSearchResult(wordSearchMessage, entry.getKey(),
                        (int) entry.getValue().length(), entry.getValue().getName(), "localhost", port));
                writeMessage(result);
            }
        }
    }

    private void waitForConnection() throws IOException {
        Socket connection = serverSocket.accept();
        ConnectionHandler handler = new ConnectionHandler(connection);
        connections.add(handler);
        handler.start();
        System.out.println("Connection to " + connection.getLocalPort() + " - ready!");
    }

    public void connectToNode(String ip, int port) throws IOException {
        Socket connection = new Socket(ip, port);
        ConnectionHandler handler = new ConnectionHandler(connection);
        connections.add(handler);
        handler.start();
        System.out.println("Connection to " + port + " - ready!");
    }
}
