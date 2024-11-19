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

import javax.swing.JOptionPane;

public class Node {
    private Map<Integer, File> files = new HashMap<>();
    private ServerSocket serverSocket;
    private int port;
    private ArrayList<ConnectionHandler> orphanConnections = new ArrayList<>();
    private Map<Integer, ConnectionHandler> connections = new HashMap<Integer, ConnectionHandler>();
    private IscTorrent gui;

    public Node(String path, int port, IscTorrent gui) {
        this.port = port;
        this.gui = gui;
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

    public void readSearchRequest(WordSearchMessage message) {
        List<FileSearchResult> result = new ArrayList<>();
        for (Entry<Integer, File> entry : files.entrySet()) {
            if (entry.getValue().getName().contains(message.getWord())) {
                result.add(new FileSearchResult(message, entry.getKey(),
                        (int) entry.getValue().length(), entry.getValue().getName(), port));
                sendMessage(message.getOriginPort(), result);
            }
        }
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
        }
        Socket connection = new Socket(ip, port);
        ConnectionHandler handler = new ConnectionHandler(connection, this, port);
        connections.put(port, handler);
        handler.start();
        sendMessage(new NewConnectionRequest(port));
        System.out.println("Connection to " + connection.getPort() + " - ready!");
    }

    public void addConnectionParent(int port, ConnectionHandler handler) {
        if (orphanConnections.remove(handler))
            connections.put(port, handler);
        else
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
