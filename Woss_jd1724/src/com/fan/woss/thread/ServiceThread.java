package com.fan.woss.thread;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Collection;

import com.briup.util.BIDR;
import com.briup.woss.server.DBStore;

public class ServiceThread implements Runnable {
	private Socket socket;
	InputStream is = null;
	ObjectInputStream ois = null;
	Object object = null;
	DBStore dbStore;
	public ServiceThread(Socket socket,DBStore dbStore) {
		this.socket = socket;
		this.dbStore = dbStore;
	}

	@Override
	public void run() {
		try {
			try {
				is = socket.getInputStream();
				ois = new ObjectInputStream(is);
				object = ois.readObject();
				dbStore.saveToDB((Collection<BIDR>) object);
			} finally {
				if (ois != null) {
					ois.close();
				}
				if (is != null) {
					is.close();
				}
				if(socket!=null){
					socket.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
