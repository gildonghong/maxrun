package com.maxrun.application.login.service;

import java.sql.SQLException;
import java.util.Map;

public interface LoginMapper {
	public Map<String, Object> login(Map<String, Object> param) throws SQLException;
	public Map<String, Object> logout(Map<String, Object> param) throws SQLException;
}
