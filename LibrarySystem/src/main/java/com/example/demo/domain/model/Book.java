package com.example.demo.login.domain.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Book {

	//bookテーブル
	private String isbn; //ISBN番号
	private String bookName; //書籍名
	private String bookAuthor; //著者名
	private int publisherId; //出版社ID
	private int bookRetention;//管理本数
	private Date createDate; //作成日
	private Date deleteDate; //削除日付

	//出版社名
	private String publisherName;
	//貸出冊数
	private int lendingBook;
	//備考
	private String remarks;
	//借りている書籍を格納する変数
	private String  nowLendingBook;
	//借りているユーザー
	private String memberId;
	private String memberName;
	//借りた日
	private Date lendingDate;
	//貸し出せる冊数
	private String borrowableBook = "全冊貸出中";
	private boolean condition;

	//変更ユーザー
	private String updateMember;
}