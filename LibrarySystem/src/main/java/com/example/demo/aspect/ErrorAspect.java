package com.example.demo.aspect;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ErrorAspect {

	Log log = LogFactory.getLog(ErrorAspect.class);

	@AfterThrowing(value="execution(* *..*..*(..))" + " && (bean(* Controller) || bean(* Service) || bean(* Repository))", throwing="ex")
	public void throwingNull(DataAccessException ex) {
		//ログ出力
		log.info("DataAccessException" + ex);
		System.out.println("DAtaAccessException" + ex);

	}


}
