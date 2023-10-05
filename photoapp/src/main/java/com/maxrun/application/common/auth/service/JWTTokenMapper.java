package com.maxrun.application.common.auth.service;

import java.security.Key;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface JWTTokenMapper {

	//refreshToken db저장
	void insertToken(Map<String, Object> map) throws SQLException;
	
	int selectTokenCount(Map<String, Object> map) throws SQLException;
	
	Map<String, Object> selectRefreshToken(Map<String, Object> param) throws SQLException;
	
	Map<String, Object> selectUserInfoByToken(Map<String, Object> param) throws SQLException;
	
	Map<String, Object> selectJwtMemberNo(Map<String, Object> param) throws SQLException;	
	
	void deleteToken(Map<String, Object> map) throws SQLException;
	
	int checkScpDbAgent(Map<String, Object> map) throws SQLException;
	
}
 