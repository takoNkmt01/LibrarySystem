package com.example.demo.login.domain.repository.jdbc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.login.domain.model.User;
import com.example.demo.util.Util;

@Repository
public class UserDaoJdbcImpl {

	@Autowired
	Util util;

	@Autowired
	JdbcTemplate jdbc;

	@Autowired
	SequenceDao sequence;

	/**
	 * 登録されているユーザー数
	 * @return ユーザー件数
	 * @throws DataAccessException
	 */
	public int count() throws DataAccessException {

		//全件取得してカウント
		int count = jdbc.queryForObject("select count(*) from member where delete_date is null", Integer.class);

		return count;
	}

	/**
	 * 一件登録
	 * @param User user
	 * @return 更新件数
	 * @throws DataAccessException
	 */
	public int insertOne(User user) throws DataAccessException {

		//管理権限をデフォルトで0に設定
		String adminFlg = "0";
		user.setAdminFlg(adminFlg);

		//現在日付を取得
		String today = util.getToday();

		//シーケンス番号を取得
		int sequenceNumber = sequence.getSequenceNumber("member_sequence");
		String id = "RS" + String.format("%06d", sequenceNumber);

		int rowNumber = jdbc.update("insert into member(member_id,"
				+ " password,"
				+ " member_name,"
				+ " birthday,"
				+ " address,"
				+ " tel_number,"
				+ " mail_address,"
				+ " admin_flg,"
				+ " create_date,"
				+ " delete_date)"
				+ " values(?,?,?,?,?,?,?,?,?,?)", id, user.getPassword(), user.getMemberName(),
				(Date) user.getBirthday(), user.getAddress(), user.getTelNumber(), user.getMailAddress(),
				user.getAdminFlg(), today, user.getDeleteDate());
		//シーケンス更新
		sequence.incrementSequenceNumber(sequenceNumber, "member_sequence");

		return rowNumber;
	}

	/**
	 * ユーザー情報一件取得
	 * @param memberId
	 * @return 取得したユーザー情報
	 */
	public User selectOne(String memberId) {
		return selectOne(memberId, null);
	}

	public User selectOne(String memberId, String password) {

		//結果返却用の変数
		User user = new User();

		try {
			//１件取得
			Map<String, Object> map;
			if (password == null) {
				map = jdbc.queryForMap("select * from member where member_id = ?", memberId);
			} else {
				map = jdbc.queryForMap("select * from member where member_id = ? and password = ?", memberId, password);
			}
			user.setMemberId((String) map.get("member_id"));
			user.setPassword((String) map.get("password"));
			user.setMemberName((String) map.get("member_name"));
			user.setBirthday((Date) map.get("birthday"));
			user.setAddress((String) map.get("address"));
			user.setTelNumber((String) map.get("tel_number"));
			user.setMailAddress((String) map.get("mail_address"));
			user.setAdminFlg((String) map.get("admin_flg"));
			user.setCreateDate((Date) map.get("create_date"));
			user.setDeleteDate((Date) map.get("delete_date"));

		} catch (EmptyResultDataAccessException e) {
			return null;
		}
		return user;
	}

	/**
	 * 登録されているユーザー情報全件
	 * @return ユーザー情報全件
	 * @throws DataAccessException
	 */
	public List<User> selectMany() throws DataAccessException {

		//memberテーブルのデータを全件取得
		List<Map<String, Object>> getList = jdbc.queryForList("select"
				+ " m.member_id, m.member_name, m.birthday, m.tel_number, m.mail_address, lb.lending_book"
				+ " from member as m"
				+ " left outer join"
				+ " (select member_id, count(member_id) as lending_book, borrowing_date"
				+ " from lending_and_borrowing where borrowing_date is null group by member_id) as lb"
				+ " on m.member_id = lb.member_id"
				+ " where m.delete_date is null");
		//結果返却用変数
		List<User> userList = new ArrayList<>();

		//取得したデータを結果返却用のListに格納していく
		for (Map<String, Object> map : getList) {
			int lendingBook = 0;

			if (map.get("lending_book") != null) {
				lendingBook = ((Number) map.get("lending_book")).intValue();
			}
			//userインスタンスの生成
			User user = new User();
			user.setMemberId((String) map.get("member_id"));
			user.setMemberName((String) map.get("member_name"));
			user.setBirthday((Date) map.get("birthday"));
			user.setTelNumber((String) map.get("tel_number"));
			user.setMailAddress((String) map.get("mail_address"));
			user.setLendingBook(lendingBook);
			userList.add(user);
		}
		return userList;
	}

	/**
	 * ユーザー情報の更新メソッド
	 * @param user
	 * @param updateOrDelete(0=削除、1=更新)
	 * @return 更新件数
	 */
	public int updateOne(User user) {//update→1, delete→0
		//更新時の日付を取得
		String today = util.getToday();

		int rowNumber = 0;

		StringBuilder sql = new StringBuilder();

		sql.append("update member set executor = ?,");
		sql.append(" member_name = ?,");
		sql.append(" address = ?,");
		sql.append(" tel_number = ?,");
		sql.append(" mail_address = ?,");
		sql.append(" update_date = ?,");
		sql.append(" remarks = ?");
		sql.append(" where member_id = ?");

		rowNumber = jdbc.update(sql.toString(),
				user.getUpdateMember(), user.getMemberName(), user.getAddress(),
				user.getTelNumber(), user.getMailAddress(), today, user.getRemarks(), user.getMemberId());
		return rowNumber;
	}

	/**
	 * ユーザー削除メソッド
	 * @param user
	 * @return　削除件数
	 * @throws DataAccessException
	 */
	public int deleteOne(User user) throws DataAccessException {

		//更新時の日付を取得
		String today = util.getToday();

		StringBuilder sql = new StringBuilder();
		sql.append("update member set executor = ?,");
		sql.append(" delete_date = ?,");
		sql.append(" remarks = ?");
		sql.append(" where member_id = ?");

		int rowNumber = jdbc.update(sql.toString(), user.getUpdateMember(), today, user.getRemarks(),
				user.getMemberId());

		rowNumber += jdbc.update("update lending_and_borrowing set borrowing_date = ?"
								+ " where member_id = ?", today, user.getMemberId());

		return rowNumber;
	}
}