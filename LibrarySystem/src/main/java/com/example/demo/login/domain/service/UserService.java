package com.example.demo.login.domain.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.login.domain.model.User;
import com.example.demo.login.domain.repository.jdbc.UserDaoJdbcImpl;
import com.example.demo.login.domain.repository.mybatis.UserMapper;
import com.example.demo.util.Util;
import com.example.demo.util.UtilPageBean;

@Service
public class UserService {

	Log log = LogFactory.getLog("UserService.class");

	@Autowired
	Util util;

	@Autowired
	UserMapper userMapper;

	@Autowired
	UserDaoJdbcImpl dao;

	//カウント用メソッド
	public int count() {
		return dao.count();
	}

	/**
	 * ユーザー登録
	 * @param user
	 * @return 登録できたか否か
	 */
	public boolean insert(User user) {
		//一件登録
		boolean result = userMapper.insert(user, util.getToday());
		return result;
	}

	/**
	 * ユーザー情報を一件取得
	 * @param memberId
	 * @return ユーザー情報
	 */
	public User selectOne(String memberId) {
		return selectOne(memberId, null);
	}
	public User selectOne(String memberId, String password) {
		return userMapper.selectOne(memberId, password);
	}

	/**
	 * ユーザー情報全件と件数取得
	 * @return ユーザー情報一覧、件数
	 */
	public UtilPageBean selectMany(int divNum){
		UtilPageBean bean = new UtilPageBean();
		bean.setUserList(userMapper.selectMany(divNum-1));
		bean.setCountUser(((Number)userMapper.count()).intValue());
		log.info(bean.getCountUser());
		bean.setPageCount(((Number)Math.ceil(bean.getCountUser()/6.0)).intValue());
		log.info(bean.getPageCount());

		return bean;
	}

	/**
	 * ユーザー情報の更新および削除
	 * @param user
	 * @param updateOrDelete(0=削除、1=更新
	 * @return 更新/削除できたか否か
	 */
	public boolean updateOne(User user, int updateOrDelete) {

		boolean result = false;

		if(1 == updateOrDelete) {
			result = userMapper.updateOne(user, util.getToday());
		}
		else {
			if(userMapper.deleteOne(user, util.getToday()))
				result = userMapper.changeLB(user, util.getToday());
		}
		return result;
	}
}