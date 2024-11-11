package pt.iscte.pcd.IscTorrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionHandler extends Thread {
	private Socket connection;
	private ObjectOutputStream out;
	private ObjectInputStream in;

	public ConnectionHandler(Socket connection) {
		this.connection = connection;
		System.out.println("Connection to " + connection.getLocalPort() + " ready!");
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
			// TODO
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
