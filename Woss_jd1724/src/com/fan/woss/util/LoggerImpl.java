package com.fan.woss.util;

import java.util.Properties;
import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;

import com.briup.util.Configuration;
import com.briup.util.Logger;
import com.briup.woss.ConfigurationAWare;

public class LoggerImpl implements Logger, ConfigurationAWare {
	private Configuration configuration;
	// log4j配置文件的路径
	private String log_properties;
	// 构建log4j对象，得到记录器
	private org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LoggerImpl.class);

	// org.apache.log4j.Logger log = org.apache.log4j.Logger.getRootLogger();
	@Override
	public void init(Properties properties) {
		log_properties = (String) properties.get("log-properties");
		// 设置log的级别
		log.setLevel(Level.INFO);
		// 使用默认的log4j配置文件
		// BasicConfigurator.configure();
		// 使用自己制定的日志配置文件
		PropertyConfigurator.configure(log_properties);
	}

	// 输出debug级别的日志信息；
	@Override
	public void debug(String s) {
		log.debug(s);
	}

	// 输出info级别的日志信息；
	@Override
	public void info(String s) {
		log.info(s);
	}

	// 输出warn级别的日志信息；
	@Override
	public void warn(String s) {
		log.warn(s);
	}

	// 输出error级别的日志信息；
	@Override
	public void error(String s) {
		log.error(s);
	}

	// 输出fatal级别的日志信息；
	@Override
	public void fatal(String s) {
		log.fatal(s);
	}

	@Override
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;

	}

}
