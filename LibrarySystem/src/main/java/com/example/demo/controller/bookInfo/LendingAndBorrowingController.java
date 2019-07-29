package com.example.demo.controller.bookInfo;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.controller.ControllerBasic;
import com.example.demo.domain.model.Book;
import com.example.demo.domain.model.SignupForm;
import com.example.demo.domain.service.BookService;
import com.example.demo.domain.service.LendingBorrowingService;
import com.example.demo.domain.service.UserDetailsImpl;
import com.example.demo.domain.service.UserService;
import com.example.demo.util.Util;
import com.example.demo.util.UtilPageBean;

@Controller
public class LendingAndBorrowingController extends ControllerBasic {

	Log log = LogFactory.getLog(LendingAndBorrowingController.class);

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

	//書籍一覧を返す共通メソッド
	public String getBookList(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
		model.addAttribute("contents", "login/borrowableList :: borrowableList_contents");

		List<Book> borrowableList = lendingBorrowingService.getBorrowableBook();
		model.addAttribute("borrowableList", borrowableList);
		util.getNowLoginUser(userDetails, model);
		return "login/homeLayout";
	}

	//ログインユーザーが借りれる書籍リスト
	@GetMapping("/borrowableList")
	public String getBorrowableBook(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
		return getBookList(userDetails, model);
	}

	//ログインユーザーが借りている書籍一覧
	@GetMapping("/borrowingList")
	public String getLendingList(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {

		model.addAttribute("contents", "login/borrowingList :: borrowingList_contents");

		UtilPageBean bean = lendingBorrowingService.getBorrowingBook(userDetails.getUsername());
		model.addAttribute("borrowingList", bean.getBookList());
		model.addAttribute("borrowingBookCount", bean.getCountBook());
		util.getNowLoginUser(userDetails, model);

		return "login/homeLayout";
	}

	//書籍貸し出しの最終確認画面へ
	@GetMapping("/borrowBook/{id:.+}")
	public String borrowConfirm(@AuthenticationPrincipal UserDetailsImpl userDetails,
			@ModelAttribute SignupForm form, Model model, @PathVariable("id") String isbn) {

		log.info("ISBN:" + isbn);

		model.addAttribute("contents", "login/borrowConfirm :: borrowConfirm_contents");

		Book book = null;

		//ISBN番号のチェック
		if(isbn != null && isbn.length() > 0) {
			book = bookService.selectOne(isbn);
		}
		model.addAttribute("book", book);
		util.getNowLoginUser(userDetails, model);
		return "login/homeLayout";
	}
	//書籍貸し出しメソッドPOST
	@PostMapping(value = "/borrowDecide", params = "borrow")
	public String borrowBook(@AuthenticationPrincipal UserDetailsImpl userDetails,
							@ModelAttribute Book book, Model model) {
	    log.info("ISBN:" + book.getIsbn());
	    log.info("NowLoginUser:" + userDetails.getUser().getMemberName() + "(" + userDetails.getUsername() + ")");

	    boolean result = lendingBorrowingService.borrowBook(userDetails.getUsername(), book.getIsbn());
	    String error = "";

	    if (result == true) {
	        log.info("更新成功");
	    } else {
	        log.info("更新失敗");
	        error = "同じ書籍を借りることはできません!";
	        model.addAttribute("error", error);

			return getBookList(userDetails, model);
	    }
	    //ホーム画面へ戻るよー
	    util.getHomePage(model, "貸出手続き", userDetails);

	    return "login/homeLayout";
	}
	//貸出確認キャンセル用メソッド
	@PostMapping(value = "borrowDecide", params = "cancel")
	public String cancelBorrow(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
		return getBookList(userDetails, model);
	}

	//書籍返却用のGETメソッド
	@PostMapping("/returnBook/{isbn}")
	public String returnBook(@AuthenticationPrincipal UserDetailsImpl userDetails,
			@ModelAttribute SignupForm form, Model model, @PathVariable("isbn") String isbn) {

		log.info("ISBN:" + isbn);
		log.info("NowLoginUser:" + userDetails.getUser().getMemberName() + "(" + userDetails.getUsername() + ")");

		boolean result = lendingBorrowingService.returnBook(userDetails.getUsername(), isbn);

		if (result == true) {
			log.info("更新成功");
		} else {
			log.info("更新失敗");
		}
		model.addAttribute("contents", "login/borrowingList :: borrowingList_contents");

		util.getHomePage(model, "書籍の返却", userDetails);

		return "login/homeLayout";
	}

	@ExceptionHandler(DataAccessException.class)
	public String dataAccessExceptionHandler(DataAccessException e, Model model) {
		return super.dataAccessExceptionHandler(e, model, "貸出機能");
	}
}