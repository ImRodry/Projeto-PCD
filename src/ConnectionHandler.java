import java.io.IOException;
import java.net.Socket;

public class ConnectionHandler extends Thread {
	private Socket connection;

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
		// TODO
	}

	private void processConnection() {
		while (true) {
			// TODO
		}
	}

	private void closeConnection() {
		try {
			// TODO close in and out streams
			if (connection != null)
				connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
