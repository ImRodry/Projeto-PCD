package pt.iscte.pcd.IscTorrent;

import java.io.Serializable;

public class FileBlockAnswerMessage implements Serializable {
	private byte[] block;
	private int offset;
	private int hash;
	private int originPort;

	public FileBlockAnswerMessage(int originPort, byte[] block, int index, int hash) {
		this.originPort = originPort;
		this.block = block;
		this.offset = index;
		this.hash = hash;
	}

	public byte[] getBlock() {
		return block;
	}

	public int getOffset() {
		return offset;
	}

	public int getHash() {
		return hash;
	}

	public int getOriginPort() {
		return originPort;
	}
}
