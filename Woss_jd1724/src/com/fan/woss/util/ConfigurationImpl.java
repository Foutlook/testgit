package com.fan.woss.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.briup.util.BackUP;
import com.briup.util.Configuration;
import com.briup.util.Logger;
import com.briup.woss.ConfigurationAWare;
import com.briup.woss.WossModule;
import com.briup.woss.client.Client;
import com.briup.woss.client.Gather;
import com.briup.woss.server.DBStore;
import com.briup.woss.server.Server;

public class ConfigurationImpl implements Configuration {
	String pathname = "src/com/fan/woss/file/conf.xml";
	Map<String, WossModule> map = new HashMap<>();
	Properties properties = new Properties();
	WossModule woss;

	public ConfigurationImpl() {
		// dom4j解析
		// 1.得到解析器
		SAXReader saxReader = new SAXReader();
		try {
			// 2.得到根节点
			Document document = saxReader.read(pathname);
			Element root = document.getRootElement();
			// 3.得到子节点，
			Iterator iterator = root.elementIterator();
			while (iterator.hasNext()) {
				Element element = (Element) iterator.next();
				// 4.得到子节点的属性和子节点的孩子节点
				// 得到子元素中的class属性
				// 属性反射得到对象放入到list集合中，子节点封装到properties对象中
				String value = element.attributeValue("class");
				// 通过类的全路径名，得到类的实例化对象
				woss = (WossModule) Class.forName(value).newInstance();
				// 封装到Map集合中
				map.put(element.getName(), woss);

				// 得到子元素的子元素节点
				Iterator elementIterator = element.elementIterator();
				while (elementIterator.hasNext()) {
					Element ele = (Element) elementIterator.next();
					//封装到properties中
					properties.put(ele.getName(), ele.getText());
				}
			}
			for (Object obj : map.values()) {
				// 依赖注入
				//configuration信息依赖注入
				if(obj instanceof ConfigurationAWare){
					//把当前对象传过去
					((ConfigurationAWare) obj).setConfiguration(this);
				}
				// 模块信息依赖注入
				if (obj instanceof WossModule) {//都符合
					// 注入配置信息
					((WossModule) obj).init(properties);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 获取<tt>日志</tt>模块的实例
	@Override
	public Logger getLogger() throws Exception {

		return (Logger) map.get("logger");
	}

	/**
	 * 获取<tt>备份</tt>模块的实例
	 */
	@Override
	public BackUP getBackup() throws Exception {
		return (BackUP) map.get("backup");
	}

	/**
	 * 获取<tt>采集</tt>模块的实例
	 */
	@Override
	public Gather getGather() throws Exception {

		return (Gather) map.get("gather");
	}

	/**
	 * 获取<tt>客户端</tt>的实例
	 */
	@Override
	public Client getClient() throws Exception {

		return (Client) map.get("client");
	}

	/**
	 * 获取<tt>服务器端</tt>的实例
	 */
	@Override
	public Server getServer() throws Exception {

		return (Server) map.get("server");
	}

	/**
	 * 获取<tt>入库</tt>模块的实例
	 */
	@Override
	public DBStore getDBStore() throws Exception {

		return (DBStore) map.get("dbstore");
	}
}
