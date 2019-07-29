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

import com.example.demo.login.domain.model.Book;
import com.example.demo.util.Util;

@Repository
public class LendingBorrowingDaoJdbcImpl {

	@Autowired
	Util util;

	@Autowired
	JdbcTemplate jdbc;

	@Autowired
	BookDaoJdbcImpl bookDao;

	@Autowired
	SequenceDao sequence;

	public Date getLendingDate(String isbn, String memberId) throws DataAccessException {
		String sql = "select lending_date from lending_and_borrowing"
				 + " where borrowing_date is null"
				 + " and isbn = '" + isbn + "'"
				 + " and member_id = '" + memberId + "'";

		Date lendingDate = (Date)jdbc.queryForObject(sql, Date.class);

		return lendingDate;
	}

	/**
	 * ユーザーが借りている書籍リストを返す
	 * @param memberId
	 * @return 借りている書籍
	 */
	public List<Book> getBorrowingBook(String memberId) throws DataAccessException {

		//memberテーブルのデータを全件取得
		List<Map<String, Object>> getList
		= jdbc.queryForList("select"
		+ " l.isbn, l.member_id, b.book_author, b.book_name, p.publisher_name, l.lending_date, m.member_name, l.borrowing_date"
		+ " from book as b"
		+ " left outer join lending_and_borrowing as l"
		+ " on l.isbn = b.isbn"
		+ " left outer join publisher as p"
		+ " on b.publisher_id = p.publisher_id"
		+ " left outer join member as m"
		+ " on l.member_id = m.member_id"
		+ " where l.member_id = ? and l.borrowing_date is null", memberId);

		List<Book> borrowingList = new ArrayList<>();

		for(Map<String, Object> map : getList) {

			Book book = new Book();

			book.setIsbn((String)map.get("isbn"));
			book.setMemberId((String)map.get("member_id"));
			book.setBookAuthor((String) map.get("book_author"));
			book.setBookName((String)map.get("book_name"));
			book.setPublisherName((String)map.get("publisher_name"));
			book.setLendingDate((Date)map.get("lending_date"));
			book.setMemberName((String)map.get("member_name"));

			borrowingList.add(book);
		}
		return borrowingList;
	}

	/**
	 * 貸借書籍の冊数を返す
	 * @param memberId
	 * @return 書籍の冊数
	 * @throws DataAccessException
	 */
	public int count(String memberId) throws DataAccessException{

		int count = 0;

		try {
			//全件取得してカウント
			String sql = "select count(*) from lending_and_borrowing"
						+ " where borrowing_date is null and member_id = '" + memberId + "'";
			count = jdbc.queryForObject(sql, Integer.class);
		} catch(EmptyResultDataAccessException e) {
			count = 0;
			return count;
		}

		return count;
	}

	/**
	 * ユーザーが借りれる書籍の一覧を返す
	 * @return 借りれる書籍一覧
	 * @throws DataAccessException
	 */
	public List<Book> getBorrowableBook() throws DataAccessException{

		return bookDao.selectAllBook();
	}

	/**
	 * @param 書籍IDとユーザーID
	 * @return 更新件数
	 */
	public int borrowBook(String memberId, String isbn) throws DataAccessException{

		//借りた日付
		String today = util.getToday();

		int rowNumber = jdbc.update("insert into lending_and_borrowing ("
								+ " member_id, isbn, lending_date)"
								+ " values(?, ?, ?)"
								, memberId, isbn, today);
		return rowNumber;
	}


	/**
	 * 書籍返却用
	 * @param memberId
	 * @param isbn
	 * @return 更新件数
	 * @throws DataAccessException
	 */
	public int returnBook(String memberId, String isbn, Date lendingDate) throws DataAccessException{
		//返却日
		String today = util.getToday();

		int rowNumber = jdbc.update("update lending_and_borrowing set borrowing_date = ?"
									+ " where member_id = ? and isbn = ? and lending_date = ?"
									, today, memberId, isbn, lendingDate);//TODO and lending_date = ?も加えないとダメ
		return rowNumber;
	}
}