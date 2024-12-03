package pt.iscte.pcd.IscTorrent;

import java.io.Serializable;

public class FileBlockAnswerMessage implements Serializable {
	private byte[] block;
	private int index;
	private int hash;
	private int originPort;

	public FileBlockAnswerMessage(int originPort, byte[] block, int index, int hash) {
		this.block = block;
		this.index = index;
		this.hash = hash;
	}

	public byte[] getBlock() {
		return block;
	}

	public int getIndex() {
		return index;
	}

	public int getHash() {
		return hash;
	}

	public int getOriginPort() {
		return originPort;
	}
}
