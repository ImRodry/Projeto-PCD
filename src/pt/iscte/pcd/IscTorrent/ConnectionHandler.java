package pt.iscte.pcd.IscTorrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class ConnectionHandler extends Thread {
	private Socket connection;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private int port;
	private Node node;

	public ConnectionHandler(Socket connection, Node node) {
		this.connection = connection;
		this.node = node;
	}

	public ConnectionHandler(Socket connection, Node node, int port) {
		this.connection = connection;
		this.node = node;
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public void run() {
		try {
			getStreams();
			if (port != 0)
				writeMessage(new NewConnectionRequest(node.getPort()));
			processConnection();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeConnection();
		}
	}

	private void getStreams() throws IOException {
		out = new ObjectOutputStream(connection.getOutputStream());
		out.flush();
		in = new ObjectInputStream(connection.getInputStream());
		System.out.println("Streams ready!");
	}

	@SuppressWarnings("unchecked")
	private void processConnection() {
		while (true) {
			try {
				Object message = in.readObject();
				Object answer = null;
				if (message instanceof NewConnectionRequest) {
					port = ((NewConnectionRequest) message).getPort();
					node.addConnectionParent(port, this);
					System.out.println("Connection to " + port + " - ready!");
				} else if (message instanceof WordSearchMessage) {
					answer = node.readSearchRequest((WordSearchMessage) message);
				} else if (message instanceof ArrayList
						&& ((ArrayList<FileSearchResult>) message).getFirst() instanceof FileSearchResult) {
					node.getGui().updateSearchResults((ArrayList<FileSearchResult>) message);
				}
				if (answer != null)
					writeMessage(answer);
			} catch (ClassNotFoundException | IOException e) {
				if (e instanceof SocketException) {
					node.getGui().removeConnection(port);
					return;
				}
				e.printStackTrace();
				break;
			}
		}
	}

	public void writeMessage(Object message) {
		try {
			out.writeObject(message);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void closeConnection() {
		try {
			if (out != null)
				out.close();
			if (in != null)
				in.close();
			if (connection != null)
				connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "ConnectionHandler [connection=" + connection + ", port=" + port + ", node=" + node + "]";
	}
}
