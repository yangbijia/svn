package com.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientThread extends Thread {

	Socket socket;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	InputStream is;
	OutputStream os;
	Message message;
	ClientFileInfo cfi;

	public ClientThread(Socket socket) {
		this.socket = socket;
		try {
			is = socket.getInputStream();
			os = socket.getOutputStream();
			oos = new ObjectOutputStream(os);
			ois = new ObjectInputStream(is);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
