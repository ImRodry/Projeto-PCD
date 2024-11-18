package pt.iscte.pcd.IscTorrent;

import java.io.Serializable;

public class FileBlockAnswerMessage implements Serializable {
	private int originPort;
	private byte[] block;

	public FileBlockAnswerMessage(int originPort, byte[] block) {
		this.originPort = originPort;
		this.block = block;
	}

	public int getOriginPort() {
		return originPort;
	}

	public byte[] getBlock() {
		return block;
	}
}
