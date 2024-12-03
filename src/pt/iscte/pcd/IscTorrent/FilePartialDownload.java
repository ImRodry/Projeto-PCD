package pt.iscte.pcd.IscTorrent;

import java.util.HashMap;

public class FilePartialDownload {
	private String fileName;
	private HashMap<Integer, byte[]> fileBlocks = new HashMap<>();
	private HashMap<Integer, Integer> blocksPerPort = new HashMap<>();

	public FilePartialDownload(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}
	
	public HashMap<Integer, Integer> getBlocksPerPort() {
		return blocksPerPort;
	}

	synchronized public void addBytes(FileBlockAnswerMessage message) {
		fileBlocks.put(message.getIndex(), message.getBlock());
		blocksPerPort.merge(message.getOriginPort(), 1, Integer::sum);
	}

	public byte[] getSortedFileContent() {
		return fileBlocks.entrySet().stream().sorted((a, b) -> a.getKey() - b.getKey()).map(e -> e.getValue())
				.reduce(new byte[0], (acc, curr) -> {
					byte[] result = new byte[acc.length + curr.length];
					System.arraycopy(acc, 0, result, 0, acc.length);
					System.arraycopy(curr, 0, result, acc.length, curr.length);
					return result;
				});
	}

}
