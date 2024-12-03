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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

public class Node {
    private Map<Integer, File> files = new HashMap<>();
    private ServerSocket serverSocket;
    private int port;
    private String path;
    private ArrayList<ConnectionHandler> orphanConnections = new ArrayList<>();
    private Map<Integer, ConnectionHandler> connections = new HashMap<Integer, ConnectionHandler>();
    private IscTorrent gui;
    private DownloadTasksManager downloadTasksManager = new DownloadTasksManager(this);
    private ExecutorService downloadThreads = Executors.newFixedThreadPool(5);

    public Node(String path, int port, IscTorrent gui) {
        this.path = path;
        this.port = port;
        this.gui = gui;
        readFiles();
    }

    private void readFiles() {
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

    public Map<Integer, ConnectionHandler> getConnections() {
        return connections;
    }

    public ConnectionHandler getConnection(int port) {
        return connections.get(port);
    }

    public String getPath() {
        return path;
    }

    public int getPort() {
        return port;
    }

    public IscTorrent getGui() {
        return gui;
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

    public List<FileSearchResult> readSearchRequest(WordSearchMessage message) {
        List<FileSearchResult> result = new ArrayList<>();
        for (Entry<Integer, File> entry : files.entrySet()) {
            if (entry.getValue().getName().contains(message.getSearchString())) {
                result.add(new FileSearchResult(message, entry.getKey(),
                        entry.getValue().length(), entry.getValue().getName(), "localhost", port));
            }
        }
        if (result.isEmpty())
            return null;
        return result;
    }

    public FileBlockAnswerMessage readBlockRequest(FileBlockRequestMessage message) throws IOException {
        File file = files.get(message.getHash());
        byte[] fileContents = Files.readAllBytes(file.toPath());
        // Get either the requested block size or the available bytes
        int length = Math.min(message.getLength(), fileContents.length - message.getOffset());
        byte[] block = new byte[length];
        System.arraycopy(fileContents, message.getOffset(), block, 0, length);
        return new FileBlockAnswerMessage(port, block, message.getOffset(), message.getHash());
    }

    public void executeInThreadPool(Runnable task) {
        downloadThreads.execute(task);
    }

    public void submitBlockAnswer(FileBlockAnswerMessage message) {
        downloadTasksManager.submitBlockAnswer(message);
    }

    public boolean download(int hash, long fileSize, String fileName, List<Integer> nodePorts) {
        boolean success = downloadTasksManager.download(hash, fileSize, fileName, nodePorts);
        if (success)
            readFiles();
        return success;
    }

    public void sendMessage(int port, Object message) {
        ConnectionHandler connection = connections.get(port);
        if (connection == null)
            throw new IllegalArgumentException("Connection to port " + port + " not found");
        connection.writeMessage(message);
    }

    /**
     * Sends a message to all connected nodes
     * 
     * @param message The object to send
     */
    public void sendMessage(Object message) {
        for (ConnectionHandler connection : connections.values()) {
            connection.writeMessage(message);
        }
    }

    private void waitForConnection() throws IOException {
        Socket connection = serverSocket.accept();
        ConnectionHandler handler = new ConnectionHandler(connection, this);
        orphanConnections.add(handler);
        handler.start();
        System.out.println("Received connection, waiting for NewConnectionRequest");
    }

    public void connectToNode(String ip, int port) throws IOException {
        if (connections.containsKey(port)) {
            JOptionPane.showMessageDialog(gui, "A conexão para esse nó já está estabelecida.");
            return;
        } else if (port == this.port) {
            JOptionPane.showMessageDialog(gui, "Não é possível ligar a si mesmo.");
            return;
        }
        Socket connection = new Socket(ip, port);
        ConnectionHandler handler = new ConnectionHandler(connection, this, port);
        handler.start();
        connections.put(port, handler);
        System.out.println("Connection to " + connection.getPort() + " - ready!");
    }

    public void addConnectionParent(int port, ConnectionHandler handler) {
        if (orphanConnections.remove(handler)) {
            connections.put(port, handler);
        } else
            throw new IllegalArgumentException("Connection not found in orphanConnections");
    }

    public void removeConnection(int port) {
        connections.remove(port);
    }

    @Override
    public String toString() {
        return "Node [serverSocket=" + serverSocket + ", port=" + port + "]";
    }
}
