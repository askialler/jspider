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
				CrawURI uri = mapper.getTodoByMD5("aaaabbbbccc1234");
//				CrawURI uri1=new CrawURI("http://www.sina.com/", 2);
//				mapper.addTodoCrawURI(new CrawURI("http://www.example.org/todo", 5));
//				mapper.addVisitedCrawURI(new CrawURI("http://www.example.org/visited", 6));
//				mapper.deleteTodoCrawURI("aaabbb");
				int totalTodo=mapper.getTodoTotal();
				int totalVisited=mapper.getVisitedTotal();
//				CrawURI uri=mapper.get1stTodoCrawURI();
				logger.info("getTodoTotal: "+totalTodo+", getVisitedTotal: "+totalVisited);
				logger.info(uri==null?"Null":uri.toString());
				session.commit();

			} finally {
				session.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
