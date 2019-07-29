package com.example.demo.controller.bookInfo;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.domain.model.Book;
import com.example.demo.domain.model.BookRegistForm;
import com.example.demo.domain.model.GroupOrder;
import com.example.demo.domain.service.BookService;
import com.example.demo.domain.service.UserDetailsImpl;
import com.example.demo.domain.service.UserService;
import com.example.demo.util.Util;
import com.example.demo.util.UtilPageBean;

@Controller
public class BookInfoController {
	Log log = LogFactory.getLog(BookInfoController.class);

	@Autowired
	Util util;
	@Autowired
	HttpSession session;
	@Autowired
	UserService userService;
	@Autowired
	BookService bookService;

	//書籍登録画面のGET用メソッド
		@GetMapping("/bookList/regist")
		public String getBookRegistPage(@ModelAttribute BookRegistForm form, Model model) {
			return "login/bookRegist";
		}

		//書籍登録画面のPOST用メソッド
		@PostMapping("/bookList/regist")
		public String postBookRegist(@AuthenticationPrincipal UserDetailsImpl userDetails,
		@ModelAttribute @Validated(GroupOrder.class) BookRegistForm form, BindingResult bindingResult, Model model) {

			if(bindingResult.hasErrors()) {
				return getBookRegistPage(form, model);
			}
			log.info(form);

			model.addAttribute("contents", "home :: home_contents");

			//書籍登録用の処理
			if(bookService.insert(form)) {
				log.info("書籍登録完了");
				util.getHomePage(model, "書籍の新規登録", userDetails);
				util.getNowLoginUser(userDetails, model);
			} else {
				log.info("書籍登録失敗");
			}
			return "homeLayout";
		}

		//書籍一覧画面のGET用メソッド
		@RequestMapping(value = "/bookList", method = RequestMethod.GET)
		public String getBookList(@AuthenticationPrincipal UserDetailsImpl userDetails,
								Model model, @RequestParam("page") int divNum) {

			//コンテンツ部分にユーザー一覧を表示するための文字列を登録
			model.addAttribute("contents", "bookInfo/bookList :: bookList_contents");

			//ユーザー一覧の作成しModelに登録
			UtilPageBean bean = bookService.selectAllBook(divNum);
			model.addAttribute("bookList", bean.getBookList());
			model.addAttribute("bookListCount", bean.getCountBook());
			model.addAttribute("pageCount", bean.getPageCount());
			model.addAttribute("divNum", divNum);
			util.getNowLoginUser(userDetails, model);
			return "homeLayout";

		}

		//書籍詳細画面
		@GetMapping("/bookDetail/{id:.+}")
		public String getBookDetail(@AuthenticationPrincipal UserDetailsImpl userDetails,
		@ModelAttribute BookRegistForm form, Model model, @PathVariable("id") String isbn) {
			//ISBN確認
			log.info("ISBN番号 = " + isbn);

			//コンテンツ部分に書籍詳細画面を表示するための文字列を登録
			model.addAttribute("contents", "bookInfo/bookDetail :: bookDetail_contents");

			//ISBN番号のチェック
			if(isbn != null && isbn.length() > 0) {
				Book book = bookService.selectOne(isbn);
				//bookをformにコピー
				BeanUtils.copyProperties(book, form);
			}
			model.addAttribute("bookRegistForm", form);
			util.getNowLoginUser(userDetails, model);
			return "homeLayout";
		}

		//更新処理
		@PostMapping(value = "/bookDetail", params = "update")
		public String postBookDetailUpdate(@AuthenticationPrincipal UserDetailsImpl userDetails,
											@ModelAttribute BookRegistForm form, Model model) {
			Book book = new Book();

			BeanUtils.copyProperties(form, book);
			book.setUpdateMember(userDetails.getUser().getMemberName());
			boolean result = bookService.updateOne(book, 1);

			if(result == true) {
				model.addAttribute("result", "更新成功");
				util.getHomePage(model, "書籍情報の更新", userDetails);
			} else {
				model.addAttribute("result", "更新失敗");
			}
			return "homeLayout";
		}

		//削除処理
		@PostMapping(value = "/bookDetail", params = "delete")
		public String postBookDetailDelete(@AuthenticationPrincipal UserDetailsImpl userDetails,
											@ModelAttribute BookRegistForm form, Model model) {
			Book book = new Book();

			BeanUtils.copyProperties(form, book);
			book.setUpdateMember(userDetails.getUser().getMemberName());

			boolean result = bookService.updateOne(book, 0);

			if(result == true) {
				model.addAttribute("result", "削除成功");
			} else {
				model.addAttribute("result", "削除失敗");
			}
			util.getHomePage(model, "書籍の削除", userDetails);
			return "homeLayout";
		}
}