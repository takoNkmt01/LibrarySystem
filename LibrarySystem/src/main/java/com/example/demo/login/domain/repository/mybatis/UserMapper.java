package com.example.demo.login.domain.repository.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.login.domain.model.User;

@Mapper
public interface UserMapper {
	public boolean insert(User user, String today);
	public User selectOne(String memberId, String password);
	public int count();
	public List<User> selectMany(int divNum);
	public boolean updateOne(User user, String today);
	public boolean deleteOne(User user, String today);
	public boolean changeLB(User user, String today);
}
