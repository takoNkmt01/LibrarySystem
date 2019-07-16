package com.example.demo.login.domain.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {

	private String memberId;//ユーザーID
	private String password;//パスワード
	private String memberName;//ユーザー名
	private Date birthday;//生年月日
	private String address;//住所
	private String telNumber;//電話番号
	private String mailAddress;//メールアドレス
	private String adminFlg;//権限フラグ
	private Date createDate;//作成日
	private Date deleteDate;//削除日
	private Date updateDate;//更新日
	private String remarks;//変更削除理由専用変数
	private String updateMember;//変更/削除ユーザー
	//借りている本の冊数
	private int lendingBook;





}