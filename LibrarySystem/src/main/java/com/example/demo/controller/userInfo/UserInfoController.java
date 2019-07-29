package com.example.demo.controller.userInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.domain.model.SignupForm;
import com.example.demo.domain.model.User;
import com.example.demo.domain.service.BookService;
import com.example.demo.domain.service.UserDetailsImpl;
import com.example.demo.domain.service.UserService;
import com.example.demo.util.Util;
import com.example.demo.util.UtilPageBean;

@Controller
public class UserInfoController {

	Log log = LogFactory.getLog(UserInfoController.class);

	@Autowired
	Util util;
	@Autowired
	UserService userService;
	@Autowired
	BookService bookService;

	//ユーザー一覧画面のGET用メソッド
	//@GetMapping("/userList?page={id}")
	@RequestMapping(value = "/userList", method = RequestMethod.GET)
	public String getUserList(@AuthenticationPrincipal UserDetailsImpl userDetails,
							Model model, @RequestParam("page") int divNum) {

		//コンテンツ部分にユーザー一覧を表示するための文字列を登録
		model.addAttribute("contents", "login/userList :: userList_contents");
		log.info(divNum);
		//ユーザー一覧と件数をmodelに登録
		UtilPageBean bean = userService.selectMany(divNum);
		model.addAttribute("userList", bean.getUserList());
		model.addAttribute("userListCount", bean.getCountUser());
		model.addAttribute("pageCount", bean.getPageCount());
		model.addAttribute("page", bean.getNowPage());
		util.getNowLoginUser(userDetails, model);

		return "login/homeLayout";
	}

	//ユーザー詳細画面
	@GetMapping("/userDetail/{id:.+}")
	public String getUserDetail(@AuthenticationPrincipal UserDetailsImpl userDetails,
	@ModelAttribute SignupForm form, Model model, @PathVariable("id") String memberId) {
		//ユーザーID確認
		log.info("member_id = " + memberId);

		//コンテンツ部分にユーザー詳細画面を表示するための文字列を登録
		model.addAttribute("contents", "login/userDetail :: userDetail_contents");

		//MemberIdのチェック
		if (memberId != null && memberId.length() > 0) {
			User user = userService.selectOne(memberId);
			//userをformにコピーする
			BeanUtils.copyProperties(user, form);
		}
		//Modelに登録
		model.addAttribute("signupForm", form);
		util.getNowLoginUser(userDetails, model);

		return "login/homeLayout";
	}

	//ユーザー更新用
	@PostMapping(value = "/userDetail", params = "update")
	public String postUserDetailUpdate(@AuthenticationPrincipal UserDetailsImpl userDetails,
										@ModelAttribute  SignupForm form, Model model) {
		User user = new User();

		//formクラスをUserクラスに登録
		BeanUtils.copyProperties(form, user);
		user.setUpdateMember(userDetails.getUser().getMemberName());

		boolean reuslt = userService.updateOne(user, 1);

		if (reuslt == true) {
			model.addAttribute("result", "更新成功");
		} else {
			model.addAttribute("result", "更新失敗");
		}
		util.getHomePage(model, "ユーザー情報の更新", userDetails);

		return "login/homeLayout";
	}

	//ユーザー削除する。
	@PostMapping(value = "/userDetail", params = "delete")
	public String postUserDetailDelete(@AuthenticationPrincipal UserDetailsImpl userDetails,
										@ModelAttribute SignupForm form, Model model) {
		User user = new User();
		user.setUpdateMember(userDetails.getUser().getMemberName());
		user.setMemberId(form.getMemberId());
		user.setRemarks(form.getRemarks());

		boolean result = userService.updateOne(user, 0);

		if (result == true) {
			model.addAttribute("result", "削除成功");
		} else {
			model.addAttribute("result", "削除失敗");
		}
		//コンテンツ部分にユーザー一覧を表示するための文字列を登録
		model.addAttribute("contents", "login/userList :: userList_contents");

		util.getHomePage(model, "ユーザーの強制退会", userDetails);

		return "login/homeLayout";
	}
}