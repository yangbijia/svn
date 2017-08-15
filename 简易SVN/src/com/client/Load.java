package com.client;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class Load {

	ClientThread ct;

	public Load(Socket socket, String path, ClientThread ct) {

		try {
			Message message = new Message(0, null);
			ct.oos.writeObject(message);
			// 获取服务器的Map
			Map m1 = (Map) ct.ois.readObject();

			TellFileStatus n = new TellFileStatus(m1, path);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
