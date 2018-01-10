package com.fan.woss.client;

import java.io.File;
import java.io.RandomAccessFile;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import com.briup.util.BIDR;
import com.briup.util.BackUP;
import com.briup.util.Configuration;
import com.briup.util.Logger;
import com.briup.woss.ConfigurationAWare;
import com.briup.woss.client.Gather;

public class GatherImpl implements Gather, ConfigurationAWare {
	private Configuration configuration;
	private String pathname;
	private String fileName;
	private String point;
	// 存储不完整的数据
	private Map<String, BIDR> map = new HashMap<>();
	// 完整的数据保存到list集合中
	private List<BIDR> list = new ArrayList<>();
	private BIDR bidr = null;
	private BackUP backUP;
	private Logger log;
	private String str;
	private Timestamp tp;

	@Override
	public void init(Properties properties) {
		try {
			backUP = configuration.getBackup();
			log = configuration.getLogger();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 日志对象
		// 得到解析文件路径
		pathname =  properties.getProperty("src-file");
		// 得到备份文件名
		fileName = properties.getProperty("gather-backfile");
		//得到文件指针的位置
		point = properties.getProperty("pointer");
	}

	@Override
	public Collection<BIDR> gather() throws Exception {
		//每次读取的行数
		int k = 0;
		// 1.读文件，temp.txt ,一行一行的读,这里使用的是bufferReader,下面使用的是随机流读取文件
		//BufferedReader br = new BufferedReader(new FileReader(new File(pathname)));
		//使用随机流读取文件
		RandomAccessFile accessFile = new RandomAccessFile(new File(pathname), "r");
		//先读取文件指针的位置
		Object pointer = backUP.load(point, BackUP.LOAD_UNREMOVE);
		//设置读取文件的位置
		if(pointer != null){
			accessFile.seek((Long)pointer);
		}
		// 读取备份文件
		Object load = backUP.load(fileName, BackUP.LOAD_UNREMOVE);
		// 强转成map对象
		Map<String, BIDR> backMap = (Map<String, BIDR>) load;
		if (backMap != null) {
			// 如果backMap不等null,把backMap放入到Map中
			map.putAll(backMap);
			log.info("读取备份不完整对象成功...");
		}
		//通过
		while ((str = accessFile.readLine()) != null) {
			// 2.构建BIDR对象
			String[] strings = str.split("[|]");
			bidr = new BIDR();
			// 3.封装数据
			// 		数据分类--七上八下
			// 		上线记录:(包含7的数据行)
			// #briup1660|037:wKgB1660A|7|1239110900|44.211.221.247
			// 用户名 | NAS_IP | 7 | 上线时间 | 用户IP
			if (strings[2].equals("7")) {
				bidr.setAAA_login_name(strings[0].substring(1));
				bidr.setNAS_ip(strings[1]);
				// 对日期的处理
				long parseLong = Long.parseLong(strings[3]);
				// 因为parseLong是s类型的，需要正换成ms
				tp = new Timestamp(parseLong * 1000);
				bidr.setLogin_date(tp);
				bidr.setLogin_ip(strings[4]);
				// 保存到集合中
				map.put(strings[4], bidr);
			} else if (strings[2].equals("8")) {
				BIDR bidr_map = map.get(strings[4]);
				long parseLong = Long.parseLong(strings[3]);
				tp = new Timestamp(parseLong * 1000);
				// 把登出的时间添加到对象中
				bidr_map.setLogout_date(tp);
				// 从不完整的对象从对象去取出来
				Timestamp login_date = bidr_map.getLogin_date();
				// 把两个数据转成long类型毫秒，相减
				Integer time_deration = (int) (tp.getTime() - login_date.getTime());
				// 然后登录登出，差值封装
				bidr_map.setTime_deration(time_deration);
				// 封装完成的对象放入list集合中
				list.add(bidr_map);
				// 删除封装已经完整的对象
				map.remove(strings[4]);
			}
			k++;
			//每次保存的数据只有1000条
			if(k>=1000){
				//保存文件指针的位置和不完整对象
				save(accessFile);
				if(accessFile!=null)
					accessFile.close();
				return list;
			}
		}
		//调用自己创建的方法，来保存文件指针的位置和不完整对象
		save(accessFile);
		return list;
	}
	
	private void save(RandomAccessFile accessFile) throws Exception {
		//得到文件指正的位置
		long filePointer = accessFile.getFilePointer();
		//备份不完整对象， 覆盖掉以前的文件
		log.info("备份不完整对象开始.....");
		backUP.store(fileName, map, BackUP.STORE_OVERRIDE);
		//保存文件指针
		log.info("保存文件指针对象...");
		backUP.store(point, filePointer, BackUP.STORE_OVERRIDE);
		// 关闭流
		if (accessFile != null) {
			accessFile.close();
		}
	}

	/**
	 * 该方法用于传递配置模块
	 * @param configuration
	 *            传递的配置模块
	 */
	@Override
	public void setConfiguration(Configuration configuration) {
		// 通过接口的方法穿过来configuration，动态注入
		this.configuration = configuration;
	}
}
