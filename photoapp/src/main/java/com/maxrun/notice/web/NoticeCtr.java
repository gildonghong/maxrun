package com.maxrun.notice.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.maxrun.notice.service.NoticeService;

@Controller
public class NoticeCtr {
	@Autowired
	private NoticeService noticeService;
	
	@ResponseBody
	@GetMapping("/notice/list")
	public List<Map<String, Object>> getNoticeList(Map<String, Object> param) {
		return null;
	}
	
	@ResponseBody
	@GetMapping("/notice/list/{noticeNo}")
	public List<Map<String, Object>> getNoticeDetails(@PathVariable("noticeNo") int noticeNo) {
		return null;
	}
	
	@ResponseBody
	@PostMapping("/notice")
	public List<Map<String, Object>> regNotice(Map<String, Object> param) {
		List<Map<String, Object>> lst = null;
		try {
			noticeService.regNotice(param);
			return noticeService.getNoticeList(param);
		}catch(Exception ex) {
			ex.printStackTrace();
			lst = new ArrayList<Map<String, Object>>();
			Map<String, Object> mp = new HashMap<String, Object>();
			mp.put("errDesc", ex.getMessage());
			
			lst.add(mp);
		}
		return lst;
	}
}
