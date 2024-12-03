package pt.iscte.pcd.IscTorrent;

import java.util.List;

public class ListFile {
	private String name;
	private List<Integer> nodePorts;
	private int hash;
	private long fileSize;

	public ListFile(String fileName, List<Integer> nodePorts, int hash, long fileSize) {
		this.name = fileName;
		this.nodePorts = nodePorts;
		this.hash = hash;
		this.fileSize = fileSize;
	}

	public String getName() {
		return name;
	}

	public List<Integer> getNodePorts() {
		return nodePorts;
	}

	public int getHash() {
		return hash;
	}

	public long getFileSize() {
		return fileSize;
	}

	@Override
	public String toString() {
		return name + " <" + nodePorts.size() + ">";
	}
}
