package com.maxrun.repairshop.carcare.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MsgSenderService {
	@Autowired
	private MsgSenderMapper msgSenderMapper;
	
	public List<Map<String, Object>> getSendingMsgList(Map<String, Object> param)throws Exception{
		return msgSenderMapper.getSendingMsgList(param);
	}
	public Map<String, Object> saveSendingMsg(Map<String, Object> param)throws  Exception {
		return msgSenderMapper.saveSendingMsg(param);
	}
}
