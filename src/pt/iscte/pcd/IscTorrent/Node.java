package pt.iscte.pcd.IscTorrent;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Node {
    private File[] files;
    private ServerSocket serverSocket;
    private String ip;
    private int port;
    private List<ConnectionHandler> connections;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public Node(String ip, int port, String path) {
        this.ip = ip;
        this.port = port;
        files = new File(path).listFiles((File file) -> file.isFile());
    }

    public File[] getFiles() {
        return files;
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
