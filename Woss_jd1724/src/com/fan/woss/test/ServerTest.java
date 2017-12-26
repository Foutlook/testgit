package com.fan.woss.test;

import org.junit.Test;

import com.briup.util.Configuration;
import com.briup.woss.server.Server;
import com.fan.woss.util.ConfigurationImpl;

public class ServerTest {
	@Test
	public void serverTest() throws Exception {
		Configuration configuration = new ConfigurationImpl();
		Server server = configuration.getServer();
		server.revicer();
	}
}
