package com.client;

import java.awt.FlowLayout;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Server extends JFrame {
	public static JTextArea jta1;
	public static JTextArea jta2;
	public static List<String> user = new ArrayList<String>();

	public void init() {
		this.setTitle("SVN服务器");
		this.setSize(500, 500);
		this.setResizable(false);
		this.setLayout(new FlowLayout());
		this.add(new JLabel(
				"进行的操作                                                       "));
		this.add(new JLabel("                        用户列表 "));
		JPanel jp1 = new JPanel();
		jta1 = new JTextArea(22, 30);
		JPanel jp = new JPanel();
		JScrollPane jsp1 = new JScrollPane(jta1);
		this.add(jsp1);
		jta2 = new JTextArea(22, 10);
		JScrollPane jsp2 = new JScrollPane(jta2);
		this.add(jsp2);
		this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	public static void main(String[] args) throws IOException {
		new Server().init();
		ServerSocket ss = new ServerSocket(7979);
		while (true) {
			// 等待客户端的请求
			System.out.println("=======");
			Socket s = ss.accept();
			ServerThread st = new ServerThread(s);
			new Thread(st).start();
			// 输出谁连接上了
			jta1.append(s.getInetAddress() + "连接成功" + "\n");
			// jta2.append(s.getInetAddress());
			String suser = s.getInetAddress().toString();
			user.add(suser);
			System.out.println(s.getInetAddress() + "连接成功");
		}

	}
}
