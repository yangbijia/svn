package com.client;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class Update {
	// 数据
	Socket s;
	public static ArrayList filename_list = new ArrayList();
	String filename;
	Map<String, FileVersionInfo> clientMap = ClientFileInfo.getFileInfo();
	Map<String, FileVersionInfo> serverMap;
	String path;
	ClientThread ct;

	// 构造函数
	public Update(ClientThread ct, Socket s, String filename, String path) {
		this.s = s;
		this.filename = filename;
		this.ct = ct;
		this.path = path;
	}

	// 接收服务器端文件
	public void Update() {
		try {

			// 对象输入输出流
			// ObjectOutputStream oos = new ObjectOutputStream(load.os);
			// ObjectInputStream ois = new ObjectInputStream(load.is);
			// 数据输入流
			DataInputStream dis = new DataInputStream(ct.is);

			filename_list.add(filename);
			// 发送更新消息对象
			Message mm = new Message(1, filename_list); //
			ct.oos.writeObject(mm);
			System.out.println("发送更新请求消息");
			// 读文件
			// 获取文件长度
			int length = dis.readInt();
			byte[] all = new byte[length];
			dis.read(all);
			System.out.println("读取文件");
			// 用文件保存
			File file = new File(path + "\\" + filename);
			System.out.println("读取路径path=" + path);
			FileOutputStream fos = new FileOutputStream(file);
			System.out.println("FileOutputStream构造成功");
			DataOutputStream dos = new DataOutputStream(
					new BufferedOutputStream(fos));
			System.out.println("DataOutputStream构造成功");
			dos.write(all);
			dos.flush();
			// fos.close();
			System.out.println("保存文件");
			// 接收map
			try {
				serverMap = (Map<String, FileVersionInfo>) ct.ois.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 修改文件状态为一致
			ChangeFileStatus();
			// }

		} catch (IOException e) {
			e.getStackTrace();
		}
	}

	// 修改文件状态
	public void ChangeFileStatus() {

		int clientMapSize = clientMap.size();
		Iterator keyValuePairs1 = clientMap.entrySet().iterator();
		for (int i = 0; i < clientMapSize; i++) {
			Map.Entry entry = (Map.Entry) keyValuePairs1.next();
			String key = (String) entry.getKey();
			if (key.equals(filename)) {
				FileVersionInfo value = (FileVersionInfo) entry.getValue();
				FileVersionInfo _value = serverMap.get(key);
				value.version = _value.version;
				// 本地文件状态修改为
				value.fileStatus = 1;

			}

		}
	}

	// 判断文件状态是否为更新
	public boolean FileisUpdate() {

		// 判断所有文件状态
		TellFileStatus tfs = new TellFileStatus(serverMap, path);

		FileVersionInfo value = clientMap.get(filename);
		if (value.fileStatus == 0) {
			return true;
		} else {
			return false;
		}
	}

}
