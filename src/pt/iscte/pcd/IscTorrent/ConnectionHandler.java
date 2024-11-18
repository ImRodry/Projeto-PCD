package pt.iscte.pcd.IscTorrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class ConnectionHandler extends Thread {
	private Socket connection;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private int port;

	public ConnectionHandler(Socket connection) {
		this.connection = connection;
	}

	public ConnectionHandler(Socket connection, int port) {
		this.connection = connection;
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

	private void processConnection() {
		while (true) {
			try {
				Object message = in.readObject();
				if (message instanceof WordSearchMessage) {
					IscTorrent.getInstance().getNode().readSearchRequest((WordSearchMessage) message);
				}
			} catch (ClassNotFoundException | IOException e) {
				if (e instanceof SocketException)
					IscTorrent.getInstance().removeConnection(port);
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
}
