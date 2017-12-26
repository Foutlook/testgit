package com.fan.woss.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.briup.util.BIDR;
import com.briup.util.Configuration;
import com.briup.woss.ConfigurationAWare;
import com.briup.woss.client.Gather;

public class GatherImplRandomAccessFile implements Gather,ConfigurationAWare{
	Configuration configuration;
	List<BIDR> list = new ArrayList<>();
	Map<String, BIDR> map = new HashMap<>();
	String pathname = "src/com/fan/woss/file/radwtmp";
	String pointername = "src/com/fan/woss/file/pointer";
	BIDR bidr;
	Timestamp tp;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	@Override
	public void init(Properties properties) {
		
	}

	@Override
	public Collection<BIDR> gather() throws Exception {
		RandomAccessFile accessFile = new RandomAccessFile(new File(pathname), "r");
		ois = new ObjectInputStream(new FileInputStream(pointername));
		//oos = new ObjectOutputStream(new FileOutputStream("src/com/fan/woss/file/pointer"));
		bidr = new BIDR();
		String readLine;
		Object readObject = ois.readObject();
		System.out.println((Long)readObject);
		accessFile.seek((Long)readObject);
		while((readLine = accessFile.readLine())!=null){
			int i = 0;
			String[] strings = readLine.split("[|]");
			if(strings[2].equals("7")){
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
			}else if(strings[2].equals("8")){
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
			i++;
			if(i>2000){
				return list;
			}
		}
		//得到文件指针的位置
		long pointer = accessFile.getFilePointer();
		//保存到文件中
		
		if(accessFile!=null){
			accessFile.close();
		}
		if(oos!=null){
			oos.close();
		}
		return list;
	}
	public static void main(String[] args) {
		GatherImplRandomAccessFile rt  = new GatherImplRandomAccessFile();
		try {
			Collection<BIDR> collection = rt.gather();
//			for (BIDR bidr : collection) {
//				System.out.println(bidr.toString());
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
}
