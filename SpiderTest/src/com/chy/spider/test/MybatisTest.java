package com.chy.spider.test;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chy.spider.CrawURI;
import com.chy.spider.db.mapper.TodoMapper;

public class MybatisTest {
	
	private static Logger logger=LoggerFactory.getLogger(MybatisTest.class);

	private static SqlSessionFactory sqlSessionFactory;

	public static void main(String[] args) throws URISyntaxException {

		try {
			Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
			sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
			reader.close();

			SqlSession session = sqlSessionFactory.openSession();

			try {
				TodoMapper mapper = session.getMapper(TodoMapper.class);
				CrawURI uri = mapper.getCrawURI(1);
				CrawURI uri1=new CrawURI("http://www.sina.com/", 2);
				logger.info(uri.toString());
				mapper.insertCrawURI( uri);
//				mapper.insertCrawURI( "http://mebook.cc/", "aaaaaaaaa", 4);
				session.commit();

			} finally {
				session.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
