package com.example.demo.domain.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BookRegistForm {
	//変更用専用ISBN番号
	private String isbn;

	//必須入力
	@NotBlank(groups = ValidGroup1.class)
	private String bookName;

	//必須入力
	@NotBlank(groups = ValidGroup1.class)
	private String bookAuthor; //著者名

	//必須入力
	@NotBlank(groups = ValidGroup1.class)
	private String publisherName; //出版社名

	//登録冊数
	@Min(value = 1, groups=ValidGroup1.class)
	@Max(value = 10, groups=ValidGroup2.class)
	private int bookRetention;

	//変更削除のための入力フォーム用変数
	private String remarks;

	//貸出バリデーション
	private int lendingBook;
}