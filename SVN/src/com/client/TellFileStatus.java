package com.client;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TellFileStatus {

	Map m1 = ClientFileInfo.getFileInfo();
	boolean flag = true;

	public TellFileStatus(Map serverMap, String path) {
		Set set1 = m1.keySet();
		Iterator clientIter = set1.iterator();
		File file = new File(path);
		// ����ͻ���keyֵ
		List<String> list = new ArrayList<String>();
		// �������״̬��keyֵ
		List<String> _list = new ArrayList<String>();
		while (clientIter.hasNext()) {
			flag = false;
			String key = (String) clientIter.next();
			FileVersionInfo value = (FileVersionInfo) m1.get(key);
			if (value.fileStatus == 0) {
				_list.add(key);
			} else
				list.add(key);
		}
		for (int i = 0; i < _list.size(); i++) {
			m1.remove(_list.get(i));
		}
		Set set = serverMap.keySet();
		Iterator serverIter = set.iterator();
		int i = 0;
		while (serverIter.hasNext()) {
			flag = true;
			String _key = (String) serverIter.next();
			FileVersionInfo _value = (FileVersionInfo) serverMap.get(_key);
			System.out.println("clientkey=" + list.get(i) + ",serverkey="
					+ _key);
			if (_key.equals(list.get(i))) {
				compare((FileVersionInfo) serverMap.get(_key),
						(FileVersionInfo) m1.get(list.get(i)), path + "\\"
								+ list.get(i));
				break;
			}
		}
		Set set2 = serverMap.keySet();
		Iterator serverIter1 = set2.iterator();
		while (serverIter1.hasNext()) {
			String _key = (String) serverIter1.next();
			FileVersionInfo _value = (FileVersionInfo) serverMap.get(_key);
			if (!m1.containsKey(_key)) {
				_value.fileStatus = 0;
				m1.put(_key, _value);
			}
		}
		// if (!flag) {// ���������MapΪ�գ���������Map���ļ�״̬Ϊ����
		// while (clientIter.hasNext()) {
		// key = (String) clientIter.next();
		// FileVersionInfo value = (FileVersionInfo) m1.get(key);
		// New(value);// �ı��ļ�״̬
		// }
		// }

	}

	/**
	 * �Աȿͻ��˺ͷ��������ļ��������Ϣ
	 * 
	 * @param serverFileVersionInfo
	 * @param clientFileVersionInfo
	 * @param path
	 */
	void compare(FileVersionInfo serverFileVersionInfo,
			FileVersionInfo clientFileVersionInfo, String path) {
		File file = new File(path);

		long time = file.lastModified();
		Date lastClientmotifydate = new Date(time);
		int CurrentlyServervision = serverFileVersionInfo.version;
		int LocalClientvision = clientFileVersionInfo.version;
		Date LocalClientmotifyDate = clientFileVersionInfo.modifyDate;
		// FileVersionInfo list1 = serverFileVersionInfo.hisFileVersionInfo
		// .get(hisFileVersionInfo.length - 1);
		// FileVersionInfo list2 = clientFileVersionInfo.hisFileVersionInfo
		// .get(hisFileVersionInfo.length - 1);
		// if (LocalClientvision + 1 == CurrentlyServervision) {
		// ��������޸����ļ�����Ҫ�ύ�ģ��������������������Ŀͻ����ύ�����ļ������·������ϵĸ��ļ��汾�ȱ��ش���Ҫ���£��������ͻ
		if (LocalClientmotifyDate.getTime() != lastClientmotifydate.getTime()
				&& LocalClientvision < CurrentlyServervision) {
			conflict(clientFileVersionInfo);
			// ��ͻ
		} else {
			// System.out.println("LocalClientvision=" + LocalClientvision
			// + ",CurrentlyServervision=" + CurrentlyServervision);
			if (LocalClientvision == CurrentlyServervision) {
				System.out.println("LocalClientmotifyDate="
						+ LocalClientmotifyDate + ",,,lastClientmotifydate="
						+ lastClientmotifydate);

				// ���ʵ������޸�ʱ���뱣��Ĳ�ͬ����Ӧ�ύ�����������޸�״̬Ϊ�ύ
				if (LocalClientmotifyDate.getTime() == lastClientmotifydate
						.getTime()) {
					System.out.println("����һ��");
					same(clientFileVersionInfo);// ��������ļ�һ�£�����δ���£�������
				} else {
					submit(clientFileVersionInfo);
				}
			} else {
				update(clientFileVersionInfo);
			}
		}
	}

	/*
	 * void compare(Date currentlyServerModifyDate, FileVersionInfo
	 * currentlyServerFileVersion, Date localClientModifyDate, FileVersionInfo
	 * localClientFileVersion) { long CurrentlyServerModifyDate =
	 * currentlyServerModifyDate.getTime(); long LocalClientModifyDate =
	 * localClientModifyDate.getTime(); long diff = CurrentlyServerModifyDate -
	 * LocalClientModifyDate; if (diff == 0) { same();// ��������ļ�һ�£�����δ���£������� }
	 * else if (diff < 0) { submit();// �ύ,�������޸ģ����µ������� } else if (diff > 0) {
	 * if (currentlyServerFileVersion. == localClientFileVersion) { conflict();
	 * } else if (currentlyServerFileVersion < localClientFileVersion) {
	 * update(); } // ����ͻ���������޸ģ��򣨸��£�����δ���£����ӷ��������������ļ� } }
	 * 
	 * void compare(String currentlyServerModifyDate, String
	 * lastServerModifyDate, String localClientModifyDate) throws ParseException
	 * { SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); long
	 * CurrentlyServerModifyDate = df.parse(currentlyServerModifyDate)
	 * .getTime(); long LastServerModifyDate =
	 * df.parse(lastServerModifyDate).getTime(); long LocalClientModifyDate =
	 * df.parse(localClientModifyDate).getTime(); if (CurrentlyServerModifyDate
	 * == LastServerModifyDate) { // ʱ����ͬ���������ļ�δ���� if (CurrentlyServerModifyDate
	 * == LocalClientModifyDate) { same();// ��������ļ�һ�£�����δ���£������� } else {
	 * submit();// �ύ,�������޸ģ����µ������� } } else { // ʱ�䲻ͬ���������ļ��Ѹ��� if
	 * (LastServerModifyDate == LocalClientModifyDate) { update();//
	 * ���£�����δ���£��ӷ��������������ļ� } else { conflict();// ��ͻ���������޸ģ��ӷ��������������ļ� } } }
	 */
	static void same(FileVersionInfo m) {// һ��
		m.fileStatus = 1;
	}

	void submit(FileVersionInfo m) {// �ύ
		m.fileStatus = 2;
	}

	void update(FileVersionInfo m) {// ����
		m.fileStatus = 0;
	}

	void conflict(FileVersionInfo m) {// ��ͻ
		m.fileStatus = 3;
	}

	void New(FileVersionInfo m) {// ��ͻ
		m.fileStatus = 4;
	}
}
