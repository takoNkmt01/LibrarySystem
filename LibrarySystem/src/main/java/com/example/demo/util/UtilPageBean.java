package com.example.demo.util;

import java.util.List;

import com.example.demo.domain.model.Book;
import com.example.demo.domain.model.User;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UtilPageBean {

	//ユーザー系一覧
	private List<User> userList;
	private int countUser;
	//書籍系一覧
	private List<Book> bookList;
	private int countBook;

	//シーケンス番号
	private int memberSequenceNumber;
	private int bookSequenceNumber;

	//ページング用ページ数
	private int pageCount;
	private int nowPage = 1;

}