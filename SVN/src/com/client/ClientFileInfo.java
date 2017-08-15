package com.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

public class ClientFileInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private static Map<String, FileVersionInfo> allFileInfo;
	private static ClientFileInfo cfi;

	private ClientFileInfo(Map<String, FileVersionInfo> map) {
		this.allFileInfo = map;
	}

	public synchronized static Map<String, FileVersionInfo> getFileInfo() {

		if (cfi == null) {
			cfi = new ClientFileInfo(new HashMap<String, FileVersionInfo>());
			// cfi.allFileInfo = new HashMap<String, FileVersionInfo>();
		}
		return cfi.allFileInfo;
	}

	/**
	 * ���л�
	 * 
	 * @throws IOException
	 */
	public static void serialize() throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				new File("C:/ClientFileInfo.txt")));
		oos.writeObject(ClientFileInfo.getFileInfo());
	}

	/**
	 * �����л�
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void deserialize() throws FileNotFoundException, IOException,
			ClassNotFoundException {

		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				new File("C:/ClientFileInfo.txt")));
		allFileInfo = (Map) ois.readObject();
	}

	/**
	 * ��ʼ��ָ��svn·�����ļ���Ϣ
	 * 
	 * @param path
	 * @param modifyUser
	 */
	public static void init(String path, String modifyUser) {
		System.out.println("path=" + path);
		if (!"".equals(path)) {
			File file = new File(path);
			File[] files = file.listFiles();
			FileVersionInfo fvi;
			for (int i = 0; i < files.length; i++) {

				long time = files[i].lastModified();
				Date date = new Date(time);
				// SimpleDateFormat sdf=new
				// SimpleDateFormat("YYYY-mm-dd hh-MM-ss");
				// String modifyDate=sdf.format(date);
				List<FileVersionInfo> hisFile = new ArrayList<FileVersionInfo>();
				// ��ʼ�汾Ϊ1���ļ�״̬Ϊ�ύ
				fvi = new FileVersionInfo(1, date, modifyUser, 2, hisFile);
				hisFile.add(fvi);
				fvi = new FileVersionInfo(1, date, modifyUser, 2, hisFile);

				allFileInfo.put(files[i].getName(), fvi);
			}
		} else {
			JOptionPane.showMessageDialog(null, "��������Ч·������");
		}

	}

	/**
	 * �����ļ�ʱ�õ�������ٴμ���·���µ������ļ����е��ļ����ڿ�ʼ��¼��allFileInfo�У������Щ�ļ�����Ϣ�ӽ�allFileInfo��
	 * ���±����ļ���¼
	 * 
	 * @param path
	 *            ָ����svn·��
	 * @param name
	 *            �޸��ˣ������õ��ǿͻ���������ip��
	 */
	public static void addFile(String path, String name) {
		File file = new File(path);
		File[] files = file.listFiles();
		FileVersionInfo fvi;
		for (int i = 0; i < files.length; i++) {
			// Set set = ClientFileInfo.getFileInfo().keySet();
			// Iterator it=set.iterator();
			// while(it.hasNext()){
			//
			// }
			if (!allFileInfo.containsKey(files[i].getName())) {
				long time = files[i].lastModified();
				Date date = new Date(time);
				// SimpleDateFormat sdf=new
				// SimpleDateFormat("YYYY-mm-dd hh-MM-ss");
				// String modifyDate=sdf.format(date);
				List<FileVersionInfo> hisFile = new ArrayList<FileVersionInfo>();
				// ��ʼ�汾Ϊ1���ļ�״̬Ϊ�ύ
				fvi = new FileVersionInfo(1, date, name, 2, hisFile);
				hisFile.add(fvi);
				fvi = new FileVersionInfo(1, date, name, 2, hisFile);

				allFileInfo.put(files[i].getName(), fvi);
			}
		}
	}
}

// fvi = new FileVersionInfo();
//
