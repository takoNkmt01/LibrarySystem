package com.example.demo.login.domain.model;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignupForm {

	//必須入力、
	@NotBlank(groups = ValidGroup1.class)
	@Pattern(regexp = "^[^ -~｡-ﾟ]+$", groups = ValidGroup2.class)
	private String memberName; //ユーザー名

	//必須入力
	@NotBlank(groups = ValidGroup1.class)
	@Pattern(regexp = "^[\\u3040-\\u309F]+$", groups = ValidGroup2.class)
	private String kanaMemberName;//ユーザー名(ひらがな)

	//必須入力
	@NotBlank(groups = ValidGroup1.class)
	@Length(groups = ValidGroup2.class)
	@Pattern(regexp = "^[a-zA-Z0-9]+$", groups = ValidGroup3.class)
	private String password; // パスワード

	//必須入力,
	@NotNull(groups = ValidGroup1.class)
    @DateTimeFormat(pattern = "yyyy/MM/dd")
	private Date birthday;//生年月日

	//必須入力,
	@NotBlank(groups = ValidGroup1.class)
	@Length(groups = ValidGroup2.class)
	@Pattern(regexp = "^[^ -~｡-ﾟ]+$", groups = ValidGroup3.class)
	private String address;

	//任意
	private String telNumber;//電話番号

	//必須
	@NotBlank(groups = ValidGroup1.class)
	@Email(groups = ValidGroup2.class)
	private String mailAddress;//メールアドレス

	//表示するためのユーザーID
	private String memberId;

	//変更削除のための入力フォーム用変数
	//@NotBlank(groups = ValidGroup1.class)
	private String remarks;
}