package com.fan.woss.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.briup.util.BIDR;
import com.briup.util.Configuration;
import com.briup.util.Logger;
import com.briup.woss.ConfigurationAWare;
import com.briup.woss.server.Server;
import com.fan.woss.thread.ServiceThread;

public class ServerImpl implements Server, ConfigurationAWare {
	private Configuration configuration;
	private ServerSocket ss = null;
	private Socket socket = null;
	private int server_port;

	@Override
	public void init(Properties properties) {
		server_port = Integer.parseInt((String) properties.get("server-port"));
	}

	// 接收客户端对象
	@Override
	public Collection<BIDR> revicer() throws Exception {
		Logger log = configuration.getLogger();
		ss = new ServerSocket(server_port);
		try {
			while (true) {
				log.info("服务已经开启");
				socket = ss.accept();
				ServiceThread serviceThread = new ServiceThread(socket,configuration.getDBStore());
				// 使用线程池的方式
				ExecutorService es = Executors.newCachedThreadPool();
				es.execute(serviceThread);// 开启线程
				es.shutdown();// 关闭线程
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("服务器开启错误...");
		} finally {
			if (ss != null) {
				ss.close();
			}
		}
		return null;
	}

	// 该方法用于使Server安全的停止运行。
	@Override
	public void shutdown() {

	}

	@Override
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

}
