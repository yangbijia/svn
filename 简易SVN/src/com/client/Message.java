package com.client;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	public int type;
	public List<String> filename;

	public Message(int type, List<String> filename) {
		this.type = type;
		this.filename = filename;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public List<String> getFilename() {
		return filename;
	}

	public void setFilename(List<String> filename) {
		this.filename = filename;
	}

}
