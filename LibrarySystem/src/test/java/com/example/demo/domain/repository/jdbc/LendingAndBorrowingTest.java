package com.example.demo.domain.repository.jdbc;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.domain.service.LendingBorrowingService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LendingAndBorrowingTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	LendingBorrowingService service;

	@Test
	@WithMockUser
	public void 貸し出せる書籍リスト画面の書籍件数のテスト() throws Exception{
		when(service.count("RS999999")).thenReturn(10);

		mockMvc.perform(get("/borrowableList"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("合計:10件")));
	}
}
