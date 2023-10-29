package com.maxrun.notice.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeMapper {
	public List<Map<String, Object>> getNoticeList(Map<String, Object> param) throws SQLException;
	public Map<String, Object> regNotice(Map<String, Object> param)throws SQLException;
	public Map<String, Object> getNoticeDetails(int noticeNo)throws SQLException;
}
