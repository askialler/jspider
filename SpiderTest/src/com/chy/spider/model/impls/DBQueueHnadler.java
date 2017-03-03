package com.chy.spider.model.impls;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chy.spider.CrawURI;
import com.chy.spider.SafeTodoQueue;
import com.chy.spider.VisitedQueue;
import com.chy.spider.db.mapper.TodoMapper;
import com.chy.spider.model.interfaces.CrawleHandler;
import com.chy.spider.test.MybatisTest;

public class DBQueueHnadler implements CrawleHandler {

	private static Logger logger = LoggerFactory.getLogger(MybatisTest.class);

	private static SqlSessionFactory sqlSessionFactory;

	static {
		Reader reader = null;
		try {
			reader = Resources.getResourceAsReader("mybatis-config.xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public DBQueueHnadler(CrawURI seedUrl) {
		addTodo(seedUrl);
	}

	@Override
	public void addTodo(CrawURI crawUri) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			TodoMapper mapper = session.getMapper(TodoMapper.class);
			mapper.addTodoCrawURI(crawUri);
			session.commit();
		} finally {
			session.close();
		}

	}

	@Override
	public CrawURI removeTodo() {

		SqlSession session = null;
		CrawURI crawUri = null;
		try {
			session = sqlSessionFactory.openSession();
			TodoMapper mapper = session.getMapper(TodoMapper.class);
			// 查询与删除为原子操作
			synchronized (sqlSessionFactory) {
				if (!isEmpty()) {
					crawUri = mapper.get1stTodoCrawURI();
					String md5 = crawUri.getMd5();
					mapper.deleteTodoCrawURI(md5);
					session.commit();
				}
			}
		} finally {
			session.close();
		}
		return crawUri;
	}

	@Override
	public void addVisited(CrawURI crawUri) {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			TodoMapper mapper = session.getMapper(TodoMapper.class);
			mapper.addVisitedCrawURI(crawUri);
			session.commit();
		} finally {
			session.close();
		}
	}

	@Override
	public int totalVisited() {

		SqlSession session = null;
		int total = 0;
		try {
			session = sqlSessionFactory.openSession();
			TodoMapper mapper = session.getMapper(TodoMapper.class);
			total = mapper.getVisitedTotal();
			// session.commit();
		} finally {
			session.close();
		}
		return total;
	}

	@Override
	public boolean visitedContains(CrawURI crawUri) {

		SqlSession session = null;
		CrawURI cr = null;
		try {
			session = sqlSessionFactory.openSession();
			TodoMapper mapper = session.getMapper(TodoMapper.class);
			cr = mapper.getVisitedByMD5(crawUri.getMd5());
		} finally {
			session.close();
		}
		return cr != null;
	}

	@Override
	public boolean todoContains(CrawURI crawUri) {

		SqlSession session = null;
		CrawURI cr = null;
		try {
			session = sqlSessionFactory.openSession();
			TodoMapper mapper = session.getMapper(TodoMapper.class);
			cr = mapper.getTodoByMD5(crawUri.getMd5());
		} finally {
			session.close();
		}
		return cr != null;
	}

	@Override
	public boolean isEmpty() {

		SqlSession session = null;
		int count = 0;
		try {
			session = sqlSessionFactory.openSession();
			TodoMapper mapper = session.getMapper(TodoMapper.class);
			count = mapper.getTodoTotal();
		} finally {
			session.close();
		}
		return count == 0;
	}

}
