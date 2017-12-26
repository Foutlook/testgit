package com.fan.woss.client;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Properties;

import com.briup.util.BIDR;
import com.briup.util.BackUP;
import com.briup.util.Configuration;
import com.briup.util.Logger;
import com.briup.woss.ConfigurationAWare;
import com.briup.woss.client.Client;

public class ClientImpl implements Client,ConfigurationAWare {
	private Configuration configuration;
	// 备份文件路径，啦啦啦啦
	private String fileName;
	private BackUP backUP;
	private Logger log;
	// ip地址
	private String ip;
	// 端口号
	private int client_port;
	private Socket socket;
	private ObjectOutputStream oos = null;
	private OutputStream outputStream = null;

	@Override
	public void init(Properties properties) {
		fileName = (String) properties.get("client-backfile");
		client_port = Integer.parseInt((String)properties.getProperty("client-port"));
		ip = (String) properties.get("ip");
	}

	@Override
	public void send(Collection<BIDR> collection) throws Exception {
		backUP = configuration.getBackup();
		log = configuration.getLogger();
		// 读取客户端的备份文件
		Object load = backUP.load(fileName, BackUP.LOAD_REMOVE);
		if (load != null) {
			collection.addAll((Collection<BIDR>) load);
			log.info("读取客户端备份对象成功...");
		}
		try {
			// 通过客户端向服务器发送封装好的数据
			socket = new Socket(ip, client_port);
			outputStream = socket.getOutputStream();
			oos = new ObjectOutputStream(outputStream);
			oos.writeObject(collection);
			log.info("客户端向服务器发送对象成功!");
		} catch (Exception e) {
			log.error("服务器异常,客户端备份开始");
			// 客户端进行备份
			backUP.store(fileName, collection, BackUP.STORE_OVERRIDE);
			e.printStackTrace();
		} finally {
			if (oos != null) {
				oos.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}

	@Override
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
		
	}
}
