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
	// ����
	Socket s;
	public static ArrayList filename_list = new ArrayList();
	String filename;
	Map<String, FileVersionInfo> clientMap = ClientFileInfo.getFileInfo();
	Map<String, FileVersionInfo> serverMap;
	String path;
	ClientThread ct;

	// ���캯��
	public Update(ClientThread ct, Socket s, String filename, String path) {
		this.s = s;
		this.filename = filename;
		this.ct = ct;
		this.path = path;
	}

	// ���շ��������ļ�
	public void Update() {
		try {

			// �������������
			// ObjectOutputStream oos = new ObjectOutputStream(load.os);
			// ObjectInputStream ois = new ObjectInputStream(load.is);
			// ����������
			DataInputStream dis = new DataInputStream(ct.is);

			filename_list.add(filename);
			// ���͸�����Ϣ����
			Message mm = new Message(1, filename_list); //
			ct.oos.writeObject(mm);
			System.out.println("���͸���������Ϣ");
			// ���ļ�
			// ��ȡ�ļ�����
			int length = dis.readInt();
			byte[] all = new byte[length];
			dis.read(all);
			System.out.println("��ȡ�ļ�");
			// ���ļ�����
			File file = new File(path + "\\" + filename);
			System.out.println("��ȡ·��path=" + path);
			FileOutputStream fos = new FileOutputStream(file);
			System.out.println("FileOutputStream����ɹ�");
			DataOutputStream dos = new DataOutputStream(
					new BufferedOutputStream(fos));
			System.out.println("DataOutputStream����ɹ�");
			dos.write(all);
			dos.flush();
			// fos.close();
			System.out.println("�����ļ�");
			// ����map
			try {
				serverMap = (Map<String, FileVersionInfo>) ct.ois.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// �޸��ļ�״̬Ϊһ��
			ChangeFileStatus();
			// }

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
				FileVersionInfo _value = serverMap.get(key);
				value.version = _value.version;
				// �����ļ�״̬�޸�Ϊ
				value.fileStatus = 1;

			}

		}
	}

	// �ж��ļ�״̬�Ƿ�Ϊ����
	public boolean FileisUpdate() {

		// �ж������ļ�״̬
		TellFileStatus tfs = new TellFileStatus(serverMap, path);

		FileVersionInfo value = clientMap.get(filename);
		if (value.fileStatus == 0) {
			return true;
		} else {
			return false;
		}
	}

}
