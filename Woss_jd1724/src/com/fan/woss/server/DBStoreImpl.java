package com.fan.woss.server;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import org.apache.ibatis.session.SqlSession;
import com.briup.util.BIDR;
import com.briup.util.BackUP;
import com.briup.util.Configuration;
import com.briup.util.Logger;
import com.briup.woss.ConfigurationAWare;
import com.briup.woss.server.DBStore;
import com.fan.woss.common.MyBatisSqlSessionFactory;
import com.fan.woss.mapper.DataMapper;

public class DBStoreImpl implements DBStore, ConfigurationAWare {
	private Configuration configuration;
	private Logger log;
	private BackUP backUP;
	// 备份文件路径
	private String fileName;
	private List<BIDR> list = null;

	@Override
	public void init(Properties properties) {
		// 初始化需要的对象
		fileName = (String) properties.get("server-backfile");
		try {
			log = configuration.getLogger();
			backUP = configuration.getBackup();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveToDB(Collection<BIDR> collection) throws Exception {
		
		SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession();
		DataMapper mapper = sqlSession.getMapper(DataMapper.class);
		int k = 0;
		/* 得到当前的几号 */
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DATE);
		// 在存入数据库前取出备份的list对象
		Object load = backUP.load(fileName, BackUP.LOAD_REMOVE);
		if (load != null) {// 不为空
			list = (List<BIDR>) load;
			list.addAll(collection);
			log.info("保存数据库前 加载备份文件成功!");
		} else { // 为空
			list = new ArrayList<>();
			list.addAll(collection);
		}
		try {
			for (BIDR bidr : list) {
				// 通过最后登出的日期作为辨别不同表的条件
				Timestamp logout_date = bidr.getLogout_date();
				String string = logout_date.toString();
				String[] split = string.split(" ");
				String[] split2 = split[0].split("[-]");
				int date = Integer.parseInt(split2[2]);
				mapper.insertDataCollection(bidr, day);
				// int i = 1/0;
				sqlSession.commit();
				k++;
				// int i = 1/0;
				// 删除集合中的已经出入数据库中的对象
				// 会发生并发修改异常
				// list.remove(bidr);

			}
			log.info("插入成功-共插入了:" + k + "条数据");
		} catch (Exception e) {
			log.warn("开始回滚...");
			sqlSession.rollback();
			for (int i = 0; i < k; i++) {
				list.remove(i);
			}
			log.info("list的长度:" + list.size());
			// 出现异常，备份没有存入的list集合
			backUP.store(fileName, list, BackUP.STORE_OVERRIDE);
			e.printStackTrace();
		} finally {
			if (sqlSession != null)
				sqlSession.close();
		}
	}

	@Override
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

}
