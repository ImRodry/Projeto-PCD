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

	synchronized public void submitBlockAnswer(FileBlockAnswerMessage message) {
		// If a null block is received, something went wrong, so we remove the task
		if (message.getBlock() == null)
			downloadTasks.remove(message.getHash());
		else if (downloadTasks.containsKey(message.getHash()))
			downloadTasks.get(message.getHash()).addBytes(message);
	}

	public boolean download(int hash, long fileSize, String fileName, List<Integer> nodePorts) {
		int poolSize = nodePorts.size();
		ExecutorService threads = Executors.newFixedThreadPool(poolSize);

		HashMap<Integer, AtomicBoolean> nodeAvailability = new HashMap<>();
		for (int port : nodePorts) {
			nodeAvailability.put(port, new AtomicBoolean(false));
		}

		downloadTasks.put(hash, new FilePartialDownload(fileName));
		for (int blockIndex = 0; blockIndex < fileSize; blockIndex += MAX_BLOCK_SIZE) {
			// Just to avoid java errors when using it to send the request below
			final int finalBlockIndex = blockIndex;
			threads.execute(() -> {
				for (Entry<Integer, AtomicBoolean> entry : nodeAvailability.entrySet())
					if (entry.getValue().compareAndSet(false, true)) {
						ConnectionHandler connection = node.getConnection(entry.getKey());
						connection.writeMessage(new FileBlockRequestMessage(hash, finalBlockIndex, MAX_BLOCK_SIZE));
						connection.getLatchAndAwait();
						entry.getValue().set(false);
						break;
					}
			});
		}
		// At this point we know the download is complete
		threads.shutdown();
		try {
			threads.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		FilePartialDownload file = downloadTasks.remove(hash);
		// If the file is null (wasn't in the map), the download failed at some point
		if (file == null)
			return false;
		writeFile(file);
		return true;
	}

	private void writeFile(FilePartialDownload file) {
		try (FileOutputStream stream = new FileOutputStream(node.getPath() + "/" + file.getFileName())) {
			stream.write(file.getSortedFileContent());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
