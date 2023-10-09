package com.maxrun.application.common.auth.service;

import java.sql.SQLException;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface JWTTokenMapper {
	void regRefreshToken(Map<String, Object> map) throws SQLException;
	Map<String, Object> getRefreshToken(Map<String, Object> param) throws SQLException;
	void deleteRefreshToken(Map<String, Object> map) throws SQLException;	
}
 