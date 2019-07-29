package com.example.demo.login.controller.bookInfo;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.login.domain.model.BookRegistForm;
import com.example.demo.login.domain.service.BookService;
import com.example.demo.login.domain.service.LendingBorrowingService;
import com.example.demo.login.domain.service.UserDetailsImpl;
import com.example.demo.login.domain.service.UserService;
import com.example.demo.util.Util;
import com.example.demo.util.UtilPageBean;


@Controller
public class SearchBookController {

	@Autowired
	Util util;

	@Autowired
	UserService userService;

	@Autowired
	BookService bookService;

	@Autowired
	LendingBorrowingService lendingBorrowingService;

	@Autowired
	HttpSession session;

	//書籍検索ページ
	@GetMapping("/searchBook/form")
	public String getSearchBookPage(@AuthenticationPrincipal UserDetailsImpl userDetails,
									@ModelAttribute BookRegistForm form ,Model model) {
		model.addAttribute("contents", "login/searchBook :: searchBookForm_contents");
		util.getNowLoginUser(userDetails, model);

		return "login/homeLayout";
	}
	//ログインユーザーが借りている書籍一覧
		@PostMapping("/searchBook/list")
		public String getSearchBookList(@AuthenticationPrincipal UserDetailsImpl userDetails,
										@ModelAttribute BookRegistForm form, Model model) {

			model.addAttribute("contents", "login/borrowableList :: borrowableList_contents");

			System.out.println(form.getIsbn() + "," + form.getBookName()
								+ "," + form.getBookAuthor() + "," + form.getPublisherName());

			UtilPageBean bean = new UtilPageBean();
			bean = bookService.searchBookList(form.getIsbn(), form.getBookName(), form.getBookAuthor(), form.getPublisherName());

			model.addAttribute("borrowableList", bean.getBookList());
			model.addAttribute("bookListCount", bean.getCountBook());
			util.getNowLoginUser(userDetails, model);
			return "login/homeLayout";
		}
}