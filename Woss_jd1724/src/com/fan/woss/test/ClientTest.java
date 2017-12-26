package com.fan.woss.test;

import java.util.Collection;

import org.junit.Test;

import com.briup.util.BIDR;
import com.briup.util.Configuration;
import com.briup.util.Logger;
import com.briup.woss.client.Client;
import com.fan.woss.util.ConfigurationImpl;

public class ClientTest {
	@Test
	public void clientTest() {
		// 调用客户端
		Client client;
		Logger log = null;
		Collection<BIDR> collection;
		try {
			Configuration configuration = new ConfigurationImpl();
			log = configuration.getLogger();
			log.info("客户端开启...");
			client = configuration.getClient();
			collection = configuration.getGather().gather();
			client.send(collection);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("客户端开启异常...");
		}
	}
}
