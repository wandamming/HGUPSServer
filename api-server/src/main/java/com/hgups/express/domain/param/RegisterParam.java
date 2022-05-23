package com.hgups.express.domain.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterParam {

	public String username;
	public String password;
	public String phone;
	public String company;
	public String email;
	public String phonePrefix;
	public List<Integer> roleIds;
}
