package com.example.demo;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.repository.mybatis.UserMapper;

//テスト用アノテーション
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserDaoTest {
	@Autowired
	UserMapper userMapper;

	@Test
	public void countTest() {
		assertEquals(userMapper.count(), 20);
	}

	@Test
	@Sql("/testdata.sql")
	public void countTest2() {
		assertEquals(userMapper.count(), 21);
	}
}
