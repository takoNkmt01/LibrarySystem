package com.example.demo.login.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Controller
public class ControllerBasic {

	@ExceptionHandler(DataAccessException.class)
	public String dataAccessExceptionHandler(DataAccessException e, Model model, String className) {

		//例外クラスのメッセージをModelに登録
		model.addAttribute("error", "内部サーバーエラー(DB): ExceptionHandler");

		//例がクラスのメッセージをModelに登録
		model.addAttribute("message", className + "でDataAccessExceptionが発生しました。");

		//HTTPエラーコード（５００）をModelに登録
		model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR);

		return "error";
	}
}
