package com.maxrun.repairshop.carcare.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MsgSenderMapper {
	public List<Map<String, Object>> getSendingMsgList(Map<String, Object> param)throws SQLException;
	public Map<String, Object> saveSendingMsg(Map<String, Object> param)throws SQLException;
}
