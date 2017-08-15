package com.client;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServerThread implements Runnable, java.io.Serializable {
	// 对象
	ObjectInputStream ois;
	ObjectOutputStream oos;
	// 文件
	DataOutputStream dos;
	DataInputStream dis;
	private Socket s;
	String ip;

	public ServerThread(Socket s) {
		this.s = s;
		ip = s.getInetAddress().toString();
	}

	@Override
	public void run() {
		try {
			InputStream ins = s.getInputStream();
			OutputStream ous = s.getOutputStream();
			dis = new DataInputStream(ins);
			dos = new DataOutputStream(ous);
			oos = new ObjectOutputStream(ous);
			ois = new ObjectInputStream(ins);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			while (true) {
				String allusename = "";
				for (int i = 0; i < Server.user.size(); i++) {
					allusename = Server.user.get(i);
				}
				allusename.replace('/', '\n');
				Server.jta2.setText(allusename + "\n");

				Message meg = (Message) ois.readObject();
				System.out.println(meg.getType() + "==============");
				if (meg.getType() == 0) {

					File file = new File("D:\\server\\");
					File[] files = file.listFiles();
					FileVersionInfo fvi;
					for (int z = 0; z < files.length; z++) {
						String strname = files[z].getName();
						ServerFileInfo.getFileInfo();
						if (!ServerFileInfo.allFileInfo.containsKey(strname)) {
							Date date = new Date();
							List<FileVersionInfo> hisFile = new ArrayList<FileVersionInfo>();
							// 初始版本为1，文件状态为提交
							fvi = new FileVersionInfo(1, date, ip, hisFile);
							hisFile.add(fvi);
							fvi = new FileVersionInfo(1, date, ip, hisFile);

							ServerFileInfo.allFileInfo.put(strname, fvi);
						}
					}

					// 0 代表发 Map
					System.out.print("正在send map ing....");
					oos.writeObject(ServerFileInfo.getFileInfo());
					oos.flush();
					System.out.print("send map success");
				} else if (meg.getType() == 1) {
					// 1 代表发文件
					System.out.print("正在fasonging....");
					for (int i = 0; i < meg.getFilename().size(); i++) {
						// 选择进行传输的文件
						String filePath = "D:\\server\\"
								+ meg.getFilename().get(i);
						File fi = new File(filePath);
						FileInputStream fis = new FileInputStream(fi);
						int count = fis.available();
						byte[] all = new byte[count];
						fis.read(all);
						dos.writeInt(count);
						dos.write(all);
						dos.flush();
						// 更改本身的map信息
						String bookname = meg.getFilename().get(i);
						Date date = new Date();
						FileVersionInfo Filevi = ServerFileInfo.allFileInfo
								.get(bookname);
						int banben = Filevi.version++;
						java.util.List<FileVersionInfo> al = Filevi.hisFileVersionInfo;
						al.add(Filevi);
						FileVersionInfo fvs = new FileVersionInfo(banben, date,
								ip, al);
						ServerFileInfo.allFileInfo.put(bookname, fvs);
						System.out.print("send File success");
						Server.jta1.append("给" + ip + "发送了一个文件" + bookname
								+ "\n");
						System.out.print("正在send map ing....");
						oos.writeObject(ServerFileInfo.getFileInfo());
						oos.flush();
						System.out.print("send map success");
					}
				} else if (meg.getType() == 2) {
					// 2代表收文件
					System.out.print("正在接收ing....");
					System.out.println("++++++++++++" + meg.getFilename());
					for (int i = 0; i < meg.getFilename().size(); i++) {
						String bookname = meg.getFilename().get(i);
						int len = dis.readInt();
						System.out.println(len + "=====================");
						byte[] all = new byte[len];
						dis.read(all);

						System.out.println("文件" + new String(all));

						System.out.println("=========" + all + "==========");
						// System.out.print(all.toString());
						FileOutputStream fos = new FileOutputStream(
								"D:\\server\\" + bookname);
						DataOutputStream fos1 = new DataOutputStream(
								new BufferedOutputStream(fos));
						fos1.write(all);
						fos1.flush();
						// 更改map
						// 先扫描指定目录下的文件
						File file = new File("G:\\server\\");
						File[] files = file.listFiles();
						FileVersionInfo fvi;
						Boolean find = false;

						for (int z = 0; z < files.length; z++) {
							if (bookname == files[z].getName()) {
								find = true;
								break;
							}
							z++;
						}
						if (find == false) {
							// 没有找到需要 生成一个map
							Date date = new Date();
							List<FileVersionInfo> hisFile = new ArrayList<FileVersionInfo>();
							// 初始版本为1，文件状态为提交
							fvi = new FileVersionInfo(1, date, ip, hisFile);
							hisFile.add(fvi);
							fvi = new FileVersionInfo(1, date, ip, hisFile);

							ServerFileInfo.allFileInfo.put(files[i].getName(),
									fvi);
						} else {// 找到本地文件
							Date date = new Date();
							FileVersionInfo Filevi = ServerFileInfo
									.getFileInfo().get(bookname);
							int banben = Filevi.version++;
							List<FileVersionInfo> al = Filevi.hisFileVersionInfo;
							al.add(Filevi);
							FileVersionInfo fvs = new FileVersionInfo(banben,
									date, ip, al);
							ServerFileInfo.allFileInfo.put(bookname, fvs);
						}
						System.out.println(i + 1 + "个文件传输完成");
						Server.jta1.append(ip + "上传了一个文件" + bookname + "\n");

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				Server.jta1.append(ip + "  断开连接" + "\n");
				Server.jta2.setText("");
				System.out.println("zhegeyichang");
				s.close();
				dos.close();
			} catch (IOException e1) {
				System.out.println("zhegeyichang=====");
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
