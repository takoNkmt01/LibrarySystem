package com.example.demo.controller;

import org.springframework.ui.Model;

public interface ControllerInterface {
	//一覧画面ＧＥＴ
	public String getAllData(Model model);
	//詳細画面ＧＥＴ
	public String getDataDetail();
	//更新用ＰＯＳＴ
	public String updateData();
	//削除用ＰＯＳＴ
	public String deleteData();
	//登録画面ＧＥＴ
	public String getDataRegister();
	//登録POST
	public String registData();
}
