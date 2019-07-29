package com.example.demo.login.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "member")
@Getter
@Setter
public class UserJPA {

	@Id
	@Column(name = "member_id")
	private String memberId;

	private String password;

	@Column(name = "member_name")
	private String memberName;

	@Enumerated(EnumType.STRING)
	@Column(name = "admin_flg")
	private RoleName roleName;

}
