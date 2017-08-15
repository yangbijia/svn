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
		// 保存客户端key值
		List<String> list = new ArrayList<String>();
		// 保存更新状态的key值
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
		// if (!flag) {// 如果服务器Map为空，迭代本地Map，文件状态为新增
		// while (clientIter.hasNext()) {
		// key = (String) clientIter.next();
		// FileVersionInfo value = (FileVersionInfo) m1.get(key);
		// New(value);// 改变文件状态
		// }
		// }

	}

	/**
	 * 对比客户端和服务器端文件的相关信息
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
		// 如果本地修改了文件是需要提交的，而服务器上又有其他的客户端提交过该文件，导致服务器上的该文件版本比本地大需要更新，则产生冲突
		if (LocalClientmotifyDate.getTime() != lastClientmotifydate.getTime()
				&& LocalClientvision < CurrentlyServervision) {
			conflict(clientFileVersionInfo);
			// 冲突
		} else {
			// System.out.println("LocalClientvision=" + LocalClientvision
			// + ",CurrentlyServervision=" + CurrentlyServervision);
			if (LocalClientvision == CurrentlyServervision) {
				System.out.println("LocalClientmotifyDate="
						+ LocalClientmotifyDate + ",,,lastClientmotifydate="
						+ lastClientmotifydate);

				// 如果实际最后修改时间与保存的不同，则应提交到服务器，修改状态为提交
				if (LocalClientmotifyDate.getTime() == lastClientmotifydate
						.getTime()) {
					System.out.println("进入一致");
					same(clientFileVersionInfo);// 与服务器文件一致，本地未更新，不操作
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
	 * LocalClientModifyDate; if (diff == 0) { same();// 与服务器文件一致，本地未更新，不操作 }
	 * else if (diff < 0) { submit();// 提交,本地已修改，更新到服务器 } else if (diff > 0) {
	 * if (currentlyServerFileVersion. == localClientFileVersion) { conflict();
	 * } else if (currentlyServerFileVersion < localClientFileVersion) {
	 * update(); } // （冲突，本地已修改）或（更新，本地未更新），从服务器下载最新文件 } }
	 * 
	 * void compare(String currentlyServerModifyDate, String
	 * lastServerModifyDate, String localClientModifyDate) throws ParseException
	 * { SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); long
	 * CurrentlyServerModifyDate = df.parse(currentlyServerModifyDate)
	 * .getTime(); long LastServerModifyDate =
	 * df.parse(lastServerModifyDate).getTime(); long LocalClientModifyDate =
	 * df.parse(localClientModifyDate).getTime(); if (CurrentlyServerModifyDate
	 * == LastServerModifyDate) { // 时间相同，服务器文件未更新 if (CurrentlyServerModifyDate
	 * == LocalClientModifyDate) { same();// 与服务器文件一致，本地未更新，不操作 } else {
	 * submit();// 提交,本地已修改，更新到服务器 } } else { // 时间不同，服务器文件已更新 if
	 * (LastServerModifyDate == LocalClientModifyDate) { update();//
	 * 更新，本地未更新，从服务器下载最新文件 } else { conflict();// 冲突，本地已修改，从服务器下载最新文件 } } }
	 */
	static void same(FileVersionInfo m) {// 一致
		m.fileStatus = 1;
	}

	void submit(FileVersionInfo m) {// 提交
		m.fileStatus = 2;
	}

	void update(FileVersionInfo m) {// 更新
		m.fileStatus = 0;
	}

	void conflict(FileVersionInfo m) {// 冲突
		m.fileStatus = 3;
	}

	void New(FileVersionInfo m) {// 冲突
		m.fileStatus = 4;
	}
}
