package com.example.demo.domain.repository.jdbc;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SequenceDao {

	@Autowired
	JdbcTemplate jdbc;

	/**
	 * シーケンス番号の取得
	 * @param whichSequence(BookテーブルかUserテーブルで使うシークエンスを指定する)
	 * @return 発行したシーケンス番号
	 * @throws DataAccessException
	 */
	public int getSequenceNumber(String whichSequence) throws DataAccessException {
		//sequenceデータを取得
		Map<String, Object> map = jdbc.queryForMap("select * from sequence"
													+ " where key_item = 1");
		int sequenceNumber = (Integer)map.get(whichSequence);
		return sequenceNumber;
	}

	/**
	 * シーケンス番号の更新(インクリメント)
	 * @param sequenceNumber(現在のシーケンス番号)
	 * @param whichSequence (BookテーブルかUserテーブルで使うシークエンスを指定する)
	 * @throws DataAccessException
	 */
	public void incrementSequenceNumber(int sequenceNumber, String whichSequence) throws DataAccessException{

		int newNumber = ++sequenceNumber;

		switch(whichSequence) {
		case "book_sequence":
			jdbc.update("update sequence set book_sequence = ?"
					+ " where key_item = 1", newNumber);
			break;
		case "member_sequence":
			jdbc.update("update sequence set member_sequence = ?"
					+ " where key_item = 1", newNumber);
			break;
		}
		System.out.println("シーケンス更新成功");
	}
}