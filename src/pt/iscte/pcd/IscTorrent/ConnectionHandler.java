package pt.iscte.pcd.IscTorrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class ConnectionHandler extends Thread {
	private Socket connection;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private int port;
	private Node node;
	private HashMap<Integer, CountDownLatch> downloadLatches = new HashMap<>();

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
				if (message instanceof NewConnectionRequest) {
					port = ((NewConnectionRequest) message).getPort();
					node.addConnectionParent(port, this);
					System.out.println("Connection to " + port + " - ready!");
					SwingUtilities.invokeLater(
							() -> JOptionPane.showMessageDialog(node.getGui(), "Recebida ligação do nó " + port));
				} else if (message instanceof WordSearchMessage) {
					writeMessage(node.readSearchRequest((WordSearchMessage) message));
				} else if (message instanceof ArrayList
						&& ((ArrayList<FileSearchResult>) message).getFirst() instanceof FileSearchResult) {
					node.getGui().updateSearchResults((ArrayList<FileSearchResult>) message);
				} else if (message instanceof FileBlockRequestMessage) {
					node.submitToThreadPool(() -> {
						try {
							writeMessage(node.readBlockRequest((FileBlockRequestMessage) message));
						} catch (IOException e) {
							e.printStackTrace();
							try {
								writeMessage(new FileBlockAnswerMessage(node.getPort(), null,
										((FileBlockRequestMessage) message).getOffset(),
										((FileBlockRequestMessage) message).getHash()));
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					});
				} else if (message instanceof FileBlockAnswerMessage) {
					node.submitBlockAnswer((FileBlockAnswerMessage) message);
					CountDownLatch latch = downloadLatches.remove(((FileBlockAnswerMessage) message).getHash());
					if (latch != null)
						latch.countDown();
					else
						System.out.println("Latch not found for hash " + ((FileBlockAnswerMessage) message).getHash());
				}
			} catch (ClassNotFoundException | IOException e) {
				// If any error happens, chances are the connection is broken forever, so might as well remove it
				if (e instanceof SocketException)
					System.out.println("Connection to " + port + " closed.");
				else {
					e.printStackTrace();
					System.out.println("Ocorreu um erro na ligação à port " + port + ", esta será agora removida");
				}
				// node.getGui().removeConnection(port);
				for (CountDownLatch latch : downloadLatches.values())
					latch.countDown();
				break;
			}
		}
	}

	synchronized public void writeMessage(Object message) throws IOException {
		out.writeObject(message);
		out.flush();
	}

	synchronized public CountDownLatch createLatchForDownload(int hash) {
		if (downloadLatches.containsKey(hash))
			throw new IllegalStateException("Latch already exists for this hash");
		CountDownLatch downloadLatch = new CountDownLatch(1);
		downloadLatches.put(hash, downloadLatch);
		return downloadLatch;
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
