package com.client;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class Submit {
	Socket s;
	public static ArrayList filename_list = new ArrayList();
	String filename;
	Map<String, FileVersionInfo> clientMap = ClientFileInfo.getFileInfo();
	Map<String, FileVersionInfo> serverMap;
	String path;
	ClientThread ct;

	public Submit(ClientThread ct, Socket s, String filename, String path) {
		this.ct = ct;
		this.s = s;
		this.filename = filename;
		this.path = path;
	}

	// 提交本地文件
	public void Submit() {
		try {
			System.out.println("进入Summit");

			// 发送请求map消息对象
			Message m = new Message(0, filename_list);
			ct.oos.writeObject(m);
			System.out.println("发送请求map");
			// 读取
			Object obj2;
			try {
				if ((obj2 = ct.ois.readObject()) instanceof Map) {
					System.out.println("接收到Map");
					serverMap = (Map<String, FileVersionInfo>) obj2;
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (FileisSummit()) {
				System.out.println("进入FileisSummit");
				ArrayList filename_list_copy = new ArrayList();
				// filename_list_copy.addAll(filename_list);
				filename_list_copy.add(filename);
				filename_list = filename_list_copy;
				System.out.println("list.size=" + filename_list.size());
				File file = new File(path);
				FileInputStream fis = new FileInputStream(file);
				// 获取文件输出流可读字节数
				int byteNumber = fis.available();
				byte[] count = new byte[byteNumber];
				fis.read(count);
				// 发送提交消息对象
				Message mm = new Message(2, filename_list);
				ct.oos.writeObject(mm);
				System.out.println("发送请求Message");
				// 发送文件
				System.out.println("byteNumber=" + byteNumber);
				// 数据输出流
				DataOutputStream dos = new DataOutputStream(ct.os);
				System.out.println("获取数据输出流");
				dos.writeInt(byteNumber);
				System.out.println("count=" + count.length);
				dos.write(count, 0, byteNumber);
				// dos.write(count);
				dos.flush();
				// close();
				System.out.println("发送文件");
				// 修改文件状态为一致
				ChangeFileStatus();
			}

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
				// 本地文件状态修改为
				value.fileStatus = 1;
				// 本地文件版本加1
				value.version++;
			}
		}

	}

	// 判断文件状态是否为提交
	public boolean FileisSummit() {

		// 判断所有文件状态
		TellFileStatus tfs = new TellFileStatus(serverMap, path);
		int clientMapSize = clientMap.size();
		Iterator keyValuePairs1 = clientMap.entrySet().iterator();
		for (int i = 0; i < clientMapSize; i++) {
			Map.Entry entry = (Map.Entry) keyValuePairs1.next();
			String key = (String) entry.getKey();
			System.out.println("key=" + key + ",filename=" + filename);
			if (key.equals(filename)) {
				FileVersionInfo value = (FileVersionInfo) entry.getValue();
				// 本地文件状态修改为
				System.out.println("filestatus=" + value.fileStatus);
				if (value.fileStatus == 2) {
					return true;
				} else
					return false;
			}
			// return false;

		}
		return false;

	}

}
