package pt.iscte.pcd.IscTorrent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class DownloadTasksManager implements Serializable {
	public static final int MAX_BLOCK_SIZE = 10240;
	private Node node;
	private HashMap<Integer, FilePartialDownload> downloadTasks = new HashMap<>();

	public DownloadTasksManager(Node node) {
		this.node = node;
	}

	public void submitBlockAnswer(FileBlockAnswerMessage message) {
		// If a null block is received, something went wrong, so we remove the task
		if (message.getBlock() == null) {
			downloadTasks.remove(message.getHash());
			System.out.println("Received null block, removing task for file " + message.getHash());
		} else if (downloadTasks.containsKey(message.getHash()))
			downloadTasks.get(message.getHash()).addBytes(message);
	}

	synchronized public boolean download(int hash, long fileSize, String fileName, List<Integer> nodePorts) {
		int poolSize = nodePorts.size();
		ExecutorService threads = Executors.newFixedThreadPool(poolSize);
		System.out.println("Downloading file with hash: " + hash);

		HashMap<Integer, AtomicBoolean> nodeAvailability = new HashMap<>();
		for (int port : nodePorts) {
			nodeAvailability.put(port, new AtomicBoolean(false));
		}

		downloadTasks.put(hash, new FilePartialDownload(fileName, fileSize));
		try {

			for (int blockIndex = 0; blockIndex < fileSize; blockIndex += MAX_BLOCK_SIZE) {
				// Just to avoid java errors when using it to send the request below
				final int finalBlockIndex = blockIndex;
				threads.submit(() -> {
					for (Entry<Integer, AtomicBoolean> entry : nodeAvailability.entrySet())
						if (entry.getValue().compareAndSet(false, true)) {
							ConnectionHandler connection = node.getConnection(entry.getKey());
							if (connection == null) {
								System.out.println("Connection is null");
								nodeAvailability.remove(entry.getKey());
								downloadTasks.remove(hash);
								return;
							}
							try {
								CountDownLatch latch = connection.createLatchForDownload(hash);
								connection.writeMessage(
										new FileBlockRequestMessage(hash, finalBlockIndex, MAX_BLOCK_SIZE));
								latch.await();
							} catch (IOException | InterruptedException e) {
								e.printStackTrace();
								System.out.println("Errored on node " + entry.getKey());
								nodeAvailability.remove(entry.getKey());
								downloadTasks.remove(hash);
							} finally {
								entry.getValue().set(false);
							}
							break;
						}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// At this point we know the download is complete
		threads.shutdown();
		try {
			if (!threads.awaitTermination(180, java.util.concurrent.TimeUnit.SECONDS))
				return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			downloadTasks.remove(hash);
			return false;
		}
		FilePartialDownload file = downloadTasks.remove(hash);
		// If the file is null (wasn't in the map), the download failed at some point
		if (file == null)
			return false;
		System.out.println("Download completed and successful, will write file to disk");
		writeFile(file);
		return true;
	}

	synchronized private void writeFile(FilePartialDownload file) {
		try (FileOutputStream stream = new FileOutputStream(node.getPath() + "/" + file.getFileName())) {
			stream.write(file.getSortedFileContent());
			System.out.println(file.getResultString());
		} catch (IOException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
}
