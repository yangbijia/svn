package com.client;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class FileVersionInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	public int version;
	public Date modifyDate;
	public String modifyUser;
	public int fileStatus;
	public List<FileVersionInfo> hisFileVersionInfo;

	public FileVersionInfo(int version, Date modifyDate, String modifyUser,
			int fileStatus, List<FileVersionInfo> hisFileVersionInfo) {
		this.version = version;
		this.modifyDate = modifyDate;
		this.modifyUser = modifyUser;
		this.fileStatus = fileStatus;
		this.hisFileVersionInfo = hisFileVersionInfo;
	}

	public FileVersionInfo(int version, Date modifyDate, String modifyUser,
			List<FileVersionInfo> hisFileVersionInfo) {
		this.version = version;
		this.modifyDate = modifyDate;
		this.modifyUser = modifyUser;
		this.hisFileVersionInfo = hisFileVersionInfo;
	}

}
