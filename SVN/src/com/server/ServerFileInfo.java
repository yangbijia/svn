package com.server;

import java.io.Serializable;
import java.util.HashMap;
/*
 * ���еİ汾��Ϣ ���� �ļ��� 
 * 					�汾
 * 					�ϴ�����
 * 					ʹ����
 * 					 
 */
import java.util.Map;

import com.client.FileVersionInfo;

public class ServerFileInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	public static Map<String, FileVersionInfo> allFileInfo;

	private ServerFileInfo() {

	}

	public synchronized static Map<String, FileVersionInfo> getFileInfo() {

		if (allFileInfo == null) {
			allFileInfo = new HashMap<String, FileVersionInfo>();
		}
		return allFileInfo;
	}
	// public static void serialize() throws IOException {
	// ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
	// new File("C:/ClientFileInfo.txt")));
	// oos.writeObject(ClientFileInfo.getFileInfo());
	// }
	//
	// public static void deserialize() throws FileNotFoundException,
	// IOException,
	// ClassNotFoundException {
	//
	// ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
	// new File("C:/ClientFileInfo.txt")));
	// allFileInfo = (Map) ois.readObject();
	// }

}
