package com.example.demo.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public class Util {

	@Autowired
	HttpSession session;

	//登録、更新、削除字の日にちを取得
	public String getToday() {

		return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDateTime.now());
	}


	//ホーム画面に戻りたいマン
	public void getHomePage(Model model, String message) {
		model.addAttribute("contents", "login/home :: home_contents");
		model.addAttribute("message", message + "が完了しました!");
	}
}