package com.example.demo.login.controller.bookInfo;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.login.controller.ControllerBasic;
import com.example.demo.login.domain.model.Book;
import com.example.demo.login.domain.model.SignupForm;
import com.example.demo.login.domain.model.User;
import com.example.demo.login.domain.service.BookService;
import com.example.demo.login.domain.service.LendingBorrowingService;
import com.example.demo.login.domain.service.UserService;
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
	public String getBookList(Model model) {
		model.addAttribute("contents", "login/borrowableList :: borrowableList_contents");

		List<Book> borrowableList = lendingBorrowingService.getBorrowableBook();
		model.addAttribute("borrowableList", borrowableList);
		//WebサイトにLoginUserを表示する
		model.addAttribute("loginUser", util.getNowLoginUserAndID(util.getLoginUser()));

		return "login/homeLayout";
	}

	//ログインユーザーが借りれる書籍リスト
	@GetMapping("/borrowableList")
	public String getBorrowableBook(Model model) {
		return getBookList(model);
	}

	//ログインユーザーが借りている書籍一覧
	@GetMapping("/borrowingList")
	public String getLendingList(Model model) {

		User user = (User) session.getAttribute("loginUser");
		model.addAttribute("contents", "login/borrowingList :: borrowingList_contents");

		UtilPageBean bean = lendingBorrowingService.getBorrowingBook(user.getMemberId());
		model.addAttribute("borrowingList", bean.getBookList());
		model.addAttribute("borrowingBookCount", bean.getCountBook());
		//WebサイトにLoginUserを表示する
		model.addAttribute("loginUser", util.getNowLoginUserAndID(util.getLoginUser()));

		return "login/homeLayout";
	}

	//書籍貸し出しの最終確認画面へ
	@GetMapping("/borrowBook/{id:.+}")
	public String borrowConfirm(@ModelAttribute SignupForm form, Model model, @PathVariable("id") String isbn) {

		log.info("ISBN:" + isbn);

		model.addAttribute("contents", "login/borrowConfirm :: borrowConfirm_contents");

		Book book = null;

		//ISBN番号のチェック
		if(isbn != null && isbn.length() > 0) {
			book = bookService.selectOne(isbn);
		}
		model.addAttribute("book", book);
		//WebサイトにLoginUserを表示する
		model.addAttribute("loginUser", util.getNowLoginUserAndID(util.getLoginUser()));

		return "login/homeLayout";
	}
	//書籍貸し出しメソッドPOST
	@PostMapping(value = "/borrowDecide", params = "borrow")
	public String borrowBook(@ModelAttribute Book book, Model model) {

	    log.info("ISBN:" + book.getIsbn());
	    User user = (User) session.getAttribute("loginUser");
	    log.info("NowLoginUser:" + user.getMemberName() + "(" + user.getMemberId() + ")");

	    boolean result = lendingBorrowingService.borrowBook(user.getMemberId(), book.getIsbn());
	    String error = "";

	    if (result == true) {
	        log.info("更新成功");
	    } else {
	        log.info("更新失敗");
	        error = "同じ書籍を借りることはできません!";
	        model.addAttribute("error", error);

			return getBookList(model);
	    }
	    //ホーム画面へ戻るよー
	    util.getHomePage(model, "貸出手続き");

	    return "login/homeLayout";
	}
	//貸出確認キャンセル用メソッド
	@PostMapping(value = "borrowDecide", params = "cancel")
	public String cancelBorrow(Model model) {
		return getBookList(model);
	}

	//書籍返却用のGETメソッド
	@GetMapping("/returnBook/{id:.+}")
	public String returnBook(@ModelAttribute SignupForm form, Model model, @PathVariable("id") String isbn) {

		log.info("ISBN:" + isbn);
		User user = (User) session.getAttribute("loginUser");
		log.info("NowLoginUser:" + user.getMemberName() + "(" + user.getMemberId() + ")");

		boolean result = lendingBorrowingService.returnBook(user.getMemberId(), isbn);

		if (result == true) {
			log.info("更新成功");
		} else {
			log.info("更新失敗");
		}
		model.addAttribute("contents", "login/borrowingList :: borrowingList_contents");

		util.getHomePage(model, "書籍の返却");

		return "login/homeLayout";
	}

	@ExceptionHandler(DataAccessException.class)
	public String dataAccessExceptionHandler(DataAccessException e, Model model) {
		return super.dataAccessExceptionHandler(e, model, "貸出機能");
	}
}