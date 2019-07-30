package com.example.demo.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import com.example.demo.domain.service.UserDetailsImpl;

@Component
public class Util {

	@Autowired
	HttpSession session;

	//登録、更新、削除字の日にちを取得
	public String getToday() {

		return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDateTime.now());
	}

	public void getNowLoginUser(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
		model.addAttribute("nowLoginUser", "◎" + userDetails.getUser().getMemberName() + "さん");
	}


	//ホーム画面に戻りたいマン
	public void getHomePage(Model model, String message, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		model.addAttribute("contents", "home :: home_contents");
		model.addAttribute("message", message + "が完了しました!");
		getNowLoginUser(userDetails, model);
	}
}