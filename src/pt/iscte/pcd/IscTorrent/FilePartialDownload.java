package pt.iscte.pcd.IscTorrent;

import java.util.HashMap;

public class FilePartialDownload {
	private String fileName;
	private HashMap<Integer, Integer> blocksPerPort = new HashMap<>();
	private byte[] fileContent;

	public FilePartialDownload(String fileName, long fileSize) {
		this.fileName = fileName;
		this.fileContent = new byte[(int) fileSize];
	}

	public String getFileName() {
		return fileName;
	}

	public String getResultString() {
		return blocksPerPort.entrySet().stream()
				.map(e -> "Fornecedor [endereço=localhost, porta=" + e.getKey() + "]: " + e.getValue())
				.reduce("", (a, b) -> a + "\n" + b);
	}

	synchronized public void addBytes(FileBlockAnswerMessage message) {
		System.arraycopy(message.getBlock(), 0, fileContent, message.getOffset(), message.getBlock().length);
		blocksPerPort.merge(message.getOriginPort(), 1, Integer::sum);
	}

	public byte[] getSortedFileContent() {
		return fileContent;
	}

}
