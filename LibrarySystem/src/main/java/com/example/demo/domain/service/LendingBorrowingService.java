package com.example.demo.login.domain.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.example.demo.login.domain.model.Book;
import com.example.demo.login.domain.repository.jdbc.LendingBorrowingDaoJdbcImpl;
import com.example.demo.util.UtilPageBean;

@Service
public class LendingBorrowingService {

	@Autowired
	LendingBorrowingDaoJdbcImpl dao;

	//カウント用メソッド
	public int count(String memberId) {
		return dao.count(memberId);
	}

	/**
	 * ユーザーが借りている書籍の一覧表示
	 * @param memberId
	 * @return 書籍一覧、件数
	 */
	public UtilPageBean getBorrowingBook(String memberId){
		UtilPageBean bean = new UtilPageBean();
		bean.setBookList(dao.getBorrowingBook(memberId));
		bean.setCountBook(dao.count(memberId));
		return bean;
	}

	/**
	 * 貸出状況全件取得メソッド
	 * @return 貸出状況一覧
	 */
	public List<Book> getBorrowableBook(){
		return dao.getBorrowableBook();
	}

	/**
	 * ユーザーへの書籍の貸出
	 * @param memberId
	 * @param isbn
	 * @return 貸出できたか否か
	 */
	public boolean borrowBook(String memberId, String isbn) {

		boolean result = false;

		try {
			int rowNumber = dao.borrowBook(memberId, isbn);

			if(rowNumber > 0) {
				result = true;
			}
		} catch (DataAccessException e) {
			return result;
		} catch (NullPointerException e) {
			return result;
		}

		return result;
	}

	/**
	 * 返却
	 * @param memberId
	 * @param isbn
	 * @return 更新できたか否か
	 */
	public boolean returnBook(String memberId, String isbn) {

		int rowNumber = dao.returnBook(memberId, isbn, dao.getLendingDate(isbn, memberId));

		boolean result = false;

		if(rowNumber > 0) {
			result = true;
		}
		return result;
	}
}