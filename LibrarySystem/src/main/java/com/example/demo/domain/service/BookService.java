package com.example.demo.domain.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.domain.model.Book;
import com.example.demo.domain.model.BookRegistForm;
import com.example.demo.domain.repository.jdbc.BookDaoJdbcImpl;
import com.example.demo.domain.repository.jdbc.SeachBookJdbcImpl;
import com.example.demo.domain.repository.mybatis.BookMapper;
import com.example.demo.util.Util;
import com.example.demo.util.UtilPageBean;

@Service
public class BookService {

	Log log = LogFactory.getLog("BookService.class");

	@Autowired
	BookMapper bookMapper;
	@Autowired
	Util util;

	@Autowired
	BookDaoJdbcImpl dao;
	@Autowired
	SeachBookJdbcImpl daoS;
	//カウント用メソッド
	public int count() {
		return dao.count();
	}

	/**
	 * 書籍登録
	 * @param bookRegistForm
	 * @return 登録できたか否か
	 */
	public boolean insert(BookRegistForm book) {
		boolean result = false;
		log.info(bookMapper.searchPublisher(book.getPublisherName()));
		if(bookMapper.searchPublisher(book.getPublisherName()) == 0) {
			bookMapper.insertPublisher(book.getPublisherName());
		}
	    Integer publisherId = bookMapper.getPublisherId(book.getPublisherName());
		if(publisherId != null) {
			if(bookMapper.insertBook(book, publisherId, util.getToday())) {
				result = true;
			}
		}
		log.info(result);
		return result;
	}

	/**
	 * 書籍情報一件取得
	 * @param isbn
	 * @return Instance of Book class
	 */
	public Book selectOne(String isbn) {
		return bookMapper.selectOneBook(isbn);
	}

	/**
	 * 全件取得用メソッド&件数取得メソッド
	 * @return utilPageBean
	 */
	public UtilPageBean selectAllBook(int divNum){
		UtilPageBean bean = new UtilPageBean();
		bean.setBookList(bookMapper.selectAllBook(divNum - 1));
		bean.setCountBook(bookMapper.count());
		bean.setPageCount(((Number)Math.ceil(bean.getCountBook()/6.0)).intValue());
		return bean;
	}


	/**
	 * 書籍更新削除用メソッド
	 * @param book
	 * @param updateOrDelete
	 * @return
	 */
	public boolean updateOne(Book book, int updateOrDelete) {
		boolean result = false;

		if(1 == updateOrDelete) {
			if(bookMapper.searchPublisher(book.getPublisherName()) == 0) {
				bookMapper.insertPublisher(book.getPublisherName());
			}
			Integer publisherId = bookMapper.getPublisherId(book.getPublisherName());
			if(publisherId != null) {
				if(bookMapper.updateOne(book, publisherId, util.getToday())) {
					result = true;
				}
			}
		} else {
			result = bookMapper.deleteOne(book, util.getToday()) && bookMapper.changeLB(book, util.getToday());
		}
		return result;
	}
	/**
	 * Searching books
	 * @param isbn
	 * @param bookName
	 * @param bookAuthor
	 * @param publisherName
	 * @return utilPageBean
	 */
	public UtilPageBean searchBookList(String isbn, String bookName, String bookAuthor, String publisherName) {
		UtilPageBean bean = new UtilPageBean();
		bean.setBookList(daoS.searchBookList(isbn, bookName, bookAuthor, publisherName));
		bean.setCountBook(daoS.countBookList(isbn, bookName, bookAuthor, publisherName));

		return bean;
	}
}