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

	// �ύ�����ļ�
	public void Submit() {
		try {
			System.out.println("����Summit");

			// ��������map��Ϣ����
			Message m = new Message(0, filename_list);
			ct.oos.writeObject(m);
			System.out.println("��������map");
			// ��ȡ
			Object obj2;
			try {
				if ((obj2 = ct.ois.readObject()) instanceof Map) {
					System.out.println("���յ�Map");
					serverMap = (Map<String, FileVersionInfo>) obj2;
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (FileisSummit()) {
				System.out.println("����FileisSummit");
				ArrayList filename_list_copy = new ArrayList();
				// filename_list_copy.addAll(filename_list);
				filename_list_copy.add(filename);
				filename_list = filename_list_copy;
				System.out.println("list.size=" + filename_list.size());
				File file = new File(path);
				FileInputStream fis = new FileInputStream(file);
				// ��ȡ�ļ�������ɶ��ֽ���
				int byteNumber = fis.available();
				byte[] count = new byte[byteNumber];
				fis.read(count);
				// �����ύ��Ϣ����
				Message mm = new Message(2, filename_list);
				ct.oos.writeObject(mm);
				System.out.println("��������Message");
				// �����ļ�
				System.out.println("byteNumber=" + byteNumber);
				// ���������
				DataOutputStream dos = new DataOutputStream(ct.os);
				System.out.println("��ȡ���������");
				dos.writeInt(byteNumber);
				System.out.println("count=" + count.length);
				dos.write(count, 0, byteNumber);
				// dos.write(count);
				dos.flush();
				// close();
				System.out.println("�����ļ�");
				// �޸��ļ�״̬Ϊһ��
				ChangeFileStatus();
			}

		} catch (IOException e) {
			e.getStackTrace();
		}
	}

	// �޸��ļ�״̬
	public void ChangeFileStatus() {

		int clientMapSize = clientMap.size();
		Iterator keyValuePairs1 = clientMap.entrySet().iterator();
		for (int i = 0; i < clientMapSize; i++) {
			Map.Entry entry = (Map.Entry) keyValuePairs1.next();
			String key = (String) entry.getKey();
			if (key.equals(filename)) {
				FileVersionInfo value = (FileVersionInfo) entry.getValue();
				// �����ļ�״̬�޸�Ϊ
				value.fileStatus = 1;
				// �����ļ��汾��1
				value.version++;
			}
		}

	}

	// �ж��ļ�״̬�Ƿ�Ϊ�ύ
	public boolean FileisSummit() {

		// �ж������ļ�״̬
		TellFileStatus tfs = new TellFileStatus(serverMap, path);
		int clientMapSize = clientMap.size();
		Iterator keyValuePairs1 = clientMap.entrySet().iterator();
		for (int i = 0; i < clientMapSize; i++) {
			Map.Entry entry = (Map.Entry) keyValuePairs1.next();
			String key = (String) entry.getKey();
			System.out.println("key=" + key + ",filename=" + filename);
			if (key.equals(filename)) {
				FileVersionInfo value = (FileVersionInfo) entry.getValue();
				// �����ļ�״̬�޸�Ϊ
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
