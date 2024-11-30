package pt.iscte.pcd.IscTorrent;

public class ListFile {
	private String name;
	private int nodeCount;
	private int hash;

	public ListFile(String name, int nodeCount, int hash) {
		this.name = name;
		this.nodeCount = nodeCount;
		this.hash = hash;
	}

	public String getName() {
		return name;
	}

	public int getNodeCount() {
		return nodeCount;
	}

	public int getHash() {
		return hash;
	}

	@Override
	public String toString() {
		return name + " <" + nodeCount + ">";
	}
}
