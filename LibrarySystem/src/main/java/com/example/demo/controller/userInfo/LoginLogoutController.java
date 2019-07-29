package com.example.demo.login.controller.userInfo;

import java.util.Objects;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.login.domain.model.SignupForm;
import com.example.demo.login.domain.model.User;
import com.example.demo.login.domain.service.LendingBorrowingService;
import com.example.demo.login.domain.service.UserDetailsImpl;
import com.example.demo.login.domain.service.UserService;
import com.example.demo.util.Util;

@Controller
public class LoginLogoutController {

	Log log = LogFactory.getLog(LoginLogoutController.class);
	@Autowired
	UserService userService;
	@Autowired
	Util util;
	@Autowired
	LendingBorrowingService lendingBorrowingService;
	@Autowired
	HttpSession session;

	//ログイン画面のGET用コントローラ
	@GetMapping("/login")
	public String getLogin(@ModelAttribute SignupForm form, Model model) {
		return "login/login";
	}

	//ログイン画面のPOST用コントローラ
	@PostMapping("/login")
	public String postLogin(@ModelAttribute SignupForm form, Model model) {
		//ユーザーIDとパスワードでアカウントデータを一件取得
		User loginUser = userService.selectOne(form.getMemberId(), form.getPassword());
		if(form.getMemberId() == "" && form.getPassword() == "") {
			String error = "ユーザーIDとパスワードを入力してください";
			model.addAttribute("error", error);
			log.info("login is failed by missing userName or Password or blank");
			return "login/login";
		}
		if(Objects.equals(loginUser, null)) {
			String error = "ユーザーIDかパスワードが間違っています";
			model.addAttribute("error", error);
			log.info("login is failed by missing userName or Password or blank");
			return "login/login";
		}
		if(loginUser.getDeleteDate() != null) {
			String error = "そのユーザーは退会済みです";
			model.addAttribute("error", error);
			log.info("login is failed by getting unsubscribe user");
			return "login/login";
		}
		log.info("LoginUser：" + loginUser.getMemberName() + "(" + loginUser.getMemberId() + ")");
		session.setAttribute("loginUser", loginUser);

		return "redirect:/home";
	}

	//ホーム画面ＧＥＴメソッド
	@GetMapping("/home")
	public String getHome(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		//コンテンツ部分にユーザー一覧を表示するための文字列を登録
		model.addAttribute("contents", "login/home :: home_contents");
		util.getNowLoginUser(userDetails, model);

		return "login/homeLayout";
	}

	//ログアウト用メソッド
		@PostMapping("/logout")
		public String postLogout() {

			session.invalidate();

			return "redirect:/login";
		}
}