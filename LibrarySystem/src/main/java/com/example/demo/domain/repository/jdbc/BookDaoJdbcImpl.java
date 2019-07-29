package com.example.demo.domain.repository.jdbc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.domain.model.Book;
import com.example.demo.domain.model.BookRegistForm;
import com.example.demo.util.Util;

@Repository
public class BookDaoJdbcImpl {

	Log log = LogFactory.getLog(BookDaoJdbcImpl.class);

	@Autowired
	Util util;

	@Autowired
	JdbcTemplate jdbc;

	@Autowired
	SequenceDao sequence;

	/**
	 * 出版社名がすでに登録されているかを検索する。
	 * 登録されていたら、出版社IDを返す。登録されていなければ、出版社を新規登録してそのIDを返す。
	 * @param publisherName
	 * @return publisherId
	 * @throws DataAccessException
	 */
	public int getPublisherId(String publisherName) throws DataAccessException{
		int publisherId;

		if(!searchPublisher(publisherName)){
			insertPublisher(publisherName);
		}
		Map<String, Object> map = jdbc.queryForMap("select publisher_id from publisher where publisher_name = ?", publisherName);
		publisherId = ((Number)map.get("publisher_id")).intValue();//TODO queryForObjectで代用可能

		return publisherId;
	}

	/**
	 * 入力された出版社名が登録されているかどうかを返す
	 * @param publisherName
	 * @return 登録されているか否か
	 * @throws DataAccessException
	 */
	public boolean searchPublisher(String publisherName) throws DataAccessException{

		String sql = "select count(*) from publisher where publisher_name = '" + publisherName + "'";
		int count = jdbc.queryForObject(sql, Integer.class);

		boolean result = false;

		if(count > 0) {
			result = true;
		}

		return result;
	}

	/**
	 * 出版社名を登録
	 * @param publisherName
	 * @throws DataAccessException
	 */
	public void insertPublisher(String publisherName) throws DataAccessException{
		jdbc.update("insert into publisher(publisher_name)"
				+ " values(?)", publisherName);

		log.info("出版社名登録完了!");
	}

	/**
	 * 書籍テーブルの件数を取得
	 * @return Bookテーブルに登録されている書籍の種類数
	 * @throws DataAccessException
	 */
	public int count() throws DataAccessException{
		//全件取得してカウント
		int count = jdbc.queryForObject("select count(*) from book where delete_date is null", Integer.class);

		return count;
	}

	/**
	 * 書籍を一件登録
	 * @param BookRegistForm book
	 * @return 登録件数
	 * @throws DataAccessException
	 */
	public int insertBook(BookRegistForm book) throws DataAccessException{

		//現在日付を取得
		String today = util.getToday();

		//出版社名がすでに登録されているかを検索する。
		int publisherId = getPublisherId(book.getPublisherName());

		//シーケンス番号を取得
		int sequenceNumber = sequence.getSequenceNumber("book_sequence");
		String id = "BS" + String.format("%06d", sequenceNumber);

		int rowNumber = jdbc.update("insert into book (isbn,"
				+ " book_name,"
				+ " book_author,"
				+ " publisher_id,"
				+ " book_retention,"
				+ " create_date,"
				+ " delete_date)"
				+ " values(?,?,?,?,?,?,?)"
				, id
				, book.getBookName()
				, book.getBookAuthor()
				, publisherId
				, book.getBookRetention()
				, today
				, null);
		sequence.incrementSequenceNumber(sequenceNumber, "book_sequence");
		return rowNumber;
	}

	/**
	 * 書籍情報を一件取得
	 * @param isbn
	 * @return 取得した書籍情報
	 * @throws DataAccessException
	 */
	public Book selectOne(String isbn) throws DataAccessException{
		//１件取得
		Map<String, Object> map = jdbc.queryForMap("select"
				+ " b.isbn, b.book_name, b.book_author, p.publisher_name, b.book_retention, b.create_date, b.delete_date"
				+ " from book as b"
				+ " left outer join publisher as p"
				+ " on b.publisher_id = p.publisher_id"
				+ " where isbn = ?", isbn);

		//結果返却用変数
		Book book = new Book();

		book.setIsbn((String)map.get("isbn"));
		book.setBookName((String)map.get("book_name"));
		book.setBookAuthor((String)map.get("book_author"));
		book.setPublisherName((String)map.get("publisher_name"));
		book.setBookRetention((Integer)map.get("book_retention"));
		book.setCreateDate((Date)map.get("create_date"));
		book.setDeleteDate((Date)map.get("delete_date"));

		return book;
	}

	/**
	 * 登録された書籍を全件取得
	 * @return 削除されていない書籍情報全件
	 * @throws DataAccessException
	 */
	public List<Book> selectAllBook() throws DataAccessException{
		//bookテーブルのデータを全件取得
		List<Map<String, Object>> getList
		= jdbc.queryForList("select"
				+ " b.isbn, b.book_author, b.book_name, p.publisher_name, b.book_retention, c.lending_book"
				+ " from book as b"
				+ " left outer join"
				+ " publisher as p"
				+ " on b.publisher_id = p.publisher_id"
				+ " left outer join"
				+ " (select isbn, count(isbn) as lending_book, borrowing_date"
				+ " from lending_and_borrowing"
				+ " where borrowing_date is null group by isbn) as c"
				+ " on b.isbn = c.isbn"
				+ " where b.delete_date is null");

		//結果返却用の変数
		List<Book> bookList = new ArrayList<>();


		getList.forEach((Map<String, Object> map) -> {//ラムダで書いた
			int lendingBook = 0;
			String probability = null;
			boolean condition = true;

			if(map.get("lending_book") != null) {
				lendingBook = ((Number)map.get("lending_book")).intValue();
			}
			if(lendingBook >= (Integer)map.get("book_retention")) {
				probability = "全冊貸出中";
				condition = false;
			} else {
				probability = String.valueOf(lendingBook);
			}
			Book book = new Book();
			book.setIsbn((String)map.get("isbn"));
			book.setBookName((String)map.get("book_name"));
			book.setBookAuthor((String)map.get("book_author"));
			book.setPublisherName((String)map.get("publisher_name"));
			book.setBookRetention((Integer)map.get("book_retention"));
			book.setLendingBook(lendingBook);
			book.setBorrowableBook(probability);
			book.setCondition(condition);

			bookList.add(book);
		});
		return bookList;
	}

	/**
	 * 選択した書籍を更新する
	 * @param Book book
	 * @param updateOrDelete(0=削除, 1=更新)
	 * @return 更新件数
	 * @throws DataAccessException
	 */
	public int updateOne(Book book) throws DataAccessException {
		//更新時の日付を取得
		String today = util.getToday();

		//出版社名の検索
		int publisherId = getPublisherId(book.getPublisherName());

		int rowNumber = 0;

		StringBuilder sql = new StringBuilder();

		sql.append("update book set executor = ?,");
			sql.append(" book_name = ?,");
			sql.append(" book_author = ?,");
			sql.append(" publisher_id = ?,");
			sql.append(" book_retention = ?,");
			sql.append(" update_date = ?,");
			sql.append(" remarks = ?");
			sql.append(" where isbn = ?");
			rowNumber = jdbc.update(sql.toString()
					, book.getUpdateMember()
					, book.getBookName()
					, book.getBookAuthor()
					, publisherId
					, book.getBookRetention()
					, today
					, book.getRemarks()
					, book.getIsbn());
		return rowNumber;
	}

	/**
	 * 書籍削除メソッド
	 * @param book
	 * @return rowNumber
	 * @throws DataAccessException
	 */
	public int deleteOne(Book book) throws DataAccessException {

		//更新時の日付を取得
				String today = util.getToday();

		StringBuilder sql = new StringBuilder();

		sql.append("update book set executor = ?,");
		sql.append(" delete_date = ?,");
		sql.append(" remarks = ? ");
		sql.append(" where isbn = ?");

		int rowNumber = jdbc.update(sql.toString()
					, book.getUpdateMember(), today, book.getRemarks(), book.getIsbn());
		rowNumber += jdbc.update("update lending_and_borrowing set borrowing_date = ?"
								+ " where isbn = ?", today, book.getIsbn());

		return rowNumber;
	}
}