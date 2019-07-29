package com.example.demo.domain.repository.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.domain.model.Book;

@Repository
public class SeachBookJdbcImpl {

	@Autowired
	JdbcTemplate jdbc;

	@Autowired
	SequenceDao sequence;

	/**
	 * 検索条件に応じた書籍一覧と件数を表示する
	 * @param isbn, bookName, bookAuthor, publisherName
	 * @return 検索条件に応じた書籍一覧と件数
	 */
	public List<Book> searchBookList(String isbn, String bookName, String bookAuthor, String publisherName)
					throws DataAccessException{

		StringBuilder sql = new StringBuilder();

		sql.append("select");
		sql.append(" b.isbn, b.book_author, b.book_name, p.publisher_name, b.book_retention, c.lending_book");
		sql.append(" from book as b");
		sql.append(" left outer join");
		sql.append(" publisher as p");
		sql.append(" on b.publisher_id = p.publisher_id");
		sql.append(" left outer join");
		sql.append(" (select isbn, count(isbn) as lending_book, borrowing_date");
		sql.append(" from lending_and_borrowing as c");
		sql.append(" where borrowing_date is null group by isbn) as c");
		sql.append(" on b.isbn = c.isbn");
		sql.append(" where b.delete_date is null");
		if(isbn != "") {
			sql.append(" and b.isbn = '" + isbn + "'");
		}
		if(bookName != "") {
			sql.append(" and b.book_name like '%" + bookName + "%'");
		}
		if(bookAuthor != "") {
			sql.append(" and b.book_author like '%" + bookAuthor + "%'");
		}
		if(publisherName != "") {
			sql.append(" and p.publisher_name like '%" + publisherName + "%'");
		}

		List<Map<String, Object>> getList = jdbc.queryForList(sql.toString());

		List<Book> borrowableList = new ArrayList<>();

		for(Map<String, Object> map : getList) {
			Book book = new Book();

			book.setIsbn((String)map.get("isbn"));
			book.setBookName((String)map.get("book_name"));
			book.setBookAuthor((String)map.get("book_author"));
			book.setPublisherName((String)map.get("publisher_name"));
			book.setBookRetention((Integer)map.get("book_retention"));
			if(map.get("lending_book") != null) {     //比較用に数値型のlendingBookを採用
				book.setLendingBook(((Number)map.get("lending_book")).intValue());
			} else {
				book.setLendingBook(0);
			}
			if(book.getLendingBook() >= book.getBookRetention()) {    //貸出可能かを表示するフィールド
				book.setBorrowableBook("全冊貸出中");
				book.setCondition(false);
			} else {
				book.setBorrowableBook(String.valueOf(book.getLendingBook()));
				book.setCondition(true);
			}

			borrowableList.add(book);
			}
		return borrowableList;
	}

	/**
	 * 貸借書籍の冊数を返す
	 * @param memberId
	 * @return 書籍の冊数
	 * @throws DataAccessException
	 */
	public int countBookList(String isbn, String bookName, String bookAuthor, String publisherName) throws DataAccessException{

		int count = 0;

		StringBuilder sql = new StringBuilder();

		try {
			//全件取得してカウント
			sql.append("select");
			sql.append(" count(*)");
			sql.append(" from book as b");
			sql.append(" left outer join lending_and_borrowing as l");
			sql.append(" on l.isbn = b.isbn");
			sql.append(" left outer join publisher as p");
			sql.append(" on b.publisher_id = p.publisher_id");
			sql.append(" left outer join member as m");
			sql.append(" on l.member_id = m.member_id");
			sql.append(" where l.borrowing_date is null");
			if(isbn != "") {
				sql.append(" and l.isbn like '%" + isbn + "%'");
			}
			if(bookName != "") {
				sql.append(" and b.book_name = '" + bookName + "'");
			}
			if(bookAuthor != "") {
				sql.append(" and b.book_author = '" + bookAuthor + "'");
			}
			if(publisherName != "") {
				sql.append(" and p.publisher_name = '" + publisherName + "'");
			}
			count = jdbc.queryForObject(sql.toString(), Integer.class);

		} catch(EmptyResultDataAccessException e) {
			count = 0;
			return count;
		}
		return count;
	}
}