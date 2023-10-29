package com.maxrun.notice.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class NoticeService {
	@Autowired
	private NoticeMapper noticeMapper;
	
	public List<Map<String, Object>> getNoticeList(Map<String, Object> param)throws Exception {
		return noticeMapper.getNoticeList(param);
	}
	
	public Map<String, Object> regNotice(Map<String, Object> param)throws Exception{
		
		return noticeMapper.regNotice(param);
	}
	
	public Map<String, Object> getNoticeDetails(int noticeNo)throws Exception{
		return noticeMapper.getNoticeDetails(noticeNo);
	}
}
