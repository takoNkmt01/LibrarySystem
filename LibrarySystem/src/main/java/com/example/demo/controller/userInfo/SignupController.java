package com.example.demo.login.controller.userInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.login.domain.model.GroupOrder;
import com.example.demo.login.domain.model.SignupForm;
import com.example.demo.login.domain.model.User;
import com.example.demo.login.domain.service.UserService;

@Controller
public class SignupController {

	Log log = LogFactory.getLog(SignupController.class);

	@Autowired
	private UserService userService;

	//ユーザー登録画面のGET用コントローラ
	@GetMapping("/signup")
	public String getSignUp(@ModelAttribute SignupForm form, Model model) {

		return "login/signup";
	}

	//ユーザー登録画面のPOST用コントローラ
	@PostMapping("/signup")
	public String postSignUp(@ModelAttribute @Validated(GroupOrder.class) SignupForm form, BindingResult bindingResult,
			Model model) {

		if (bindingResult.hasErrors()) {
			return getSignUp(form, model);
		}
		log.info(form);

		//insert用変数
		User user = new User();

		user.setMemberName(form.getMemberName());
		user.setPassword(form.getPassword());
		user.setBirthday(form.getBirthday());
		user.setAddress(form.getAddress());
		user.setTelNumber(form.getTelNumber());
		user.setMailAddress(form.getMailAddress());
		user.setAdminFlg("USER");

		//ユーザー登録処理
		boolean result = userService.insert(user);

		if (result == true) {
			log.info("insert成功");
			model.addAttribute("message", "登録完了しました。");
		} else {
			log.info("insert失敗");
		}
		return "redirect:/login";
	}

	@PostMapping(value = "/finalize", params = "regist")
	public String finalizeUserRegist(@ModelAttribute SignupForm form, Model model) {

		//insert用変数
		User user = new User();

		user.setMemberName(form.getMemberName());
		user.setPassword(form.getPassword());
		user.setBirthday(form.getBirthday());
		user.setAddress(form.getAddress());
		user.setTelNumber(form.getTelNumber());
		user.setMailAddress(form.getMailAddress());
		user.setAdminFlg("0");

		//ユーザー登録処理
		boolean result = userService.insert(user);

		if (result == true) {
			log.info("insert成功");
			model.addAttribute("message", "登録完了しました。");
		} else {
			log.info("insert失敗");
		}
		return "redirect:/login";
	}

	@PostMapping(value = "/finalize", params = "cancel")
	public String cancelUserRegist(@ModelAttribute SignupForm form, Model model) {

		model.addAttribute("signupForm", form);
		log.info(form);
		System.out.println(form.getBirthday());

		return getSignUp(form, model);
	}
}
