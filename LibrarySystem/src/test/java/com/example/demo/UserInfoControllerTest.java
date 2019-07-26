package com.example.demo;

import static org.hamcrest.CoreMatchers.*;
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

import com.example.demo.login.domain.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserInfoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	//モックの戻り値設定
	@MockBean
	private UserService userService;

	//ログインした後の画面
	@Test
	@WithMockUser
	public void UserListCount() throws Exception{
		when(userService.count()).thenReturn(5);

		mockMvc.perform(get("/userList"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("合計 : 5件")));
	}
}
