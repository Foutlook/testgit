package com.fan.woss.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;
import com.briup.util.BackUP;
import com.briup.util.Configuration;
import com.briup.util.Logger;
import com.briup.woss.ConfigurationAWare;

public class BackUPImpl implements BackUP,ConfigurationAWare {
	private Configuration configuration;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private FileOutputStream fos;
	private FileInputStream fis;
	private File file;
	private Logger log;
	// 备份文件总路径
	private String filepath;

	@Override
	public void init(Properties properties) {
		filepath =  properties.getProperty("back-temp");
	}

	// 通过键名获取已经备份的数据
	// load("src/file/rollback",false)
	@Override
	public Object load(String fileName, boolean flag) throws Exception {
		log = configuration.getLogger();
		Object object = null;
		file = new File(filepath, fileName);
		if (file.exists()) {
			// 是否为空
			if (file.canRead()) {
				if (file.length() > 0) {
					fis = new FileInputStream(file);
					ois = new ObjectInputStream(fis);
					object = ois.readObject();
				}
				if(ois!=null) ois.close();
				if(fis!=null) fis.close();
				if (flag) {
					// 删除备份文件
					file.delete();
					log.warn("备份文件已删除");
				}
			}
		}
		return object;
	}

	/**
	 * 通过键名来存储数据。
	 * @param s 备份数据的键
	 * @param obj 需要备份的数据
	 * @param flag 如果键值已经存在数据，追加还是覆盖之前的数据。建议使用常量值。
	 */
	// store("src/file/rollback",bidrs(List),true)
	@Override
	public void store(String fileName, Object obj, boolean flag) throws Exception {
		log = configuration.getLogger();
		file = new File(filepath, fileName);
		if (!file.exists()) {
			file.createNewFile();
		}
		if (flag) {
			// 追加文件
			fos = new FileOutputStream(file, flag);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			log.info("追加备份成功...");
		} else {
			// 覆盖文件，
			fos = new FileOutputStream(file, flag);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			log.info("覆盖备份成功...");
		}
	}

	@Override
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
}
