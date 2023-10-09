package com.maxrun.notice.web;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.maxrun.notice.service.NoticeService;

@Controller
public class NoticeCtr {
	@Autowired
	private NoticeService noticeService;
	
	@ResponseBody
	@GetMapping("/notice/list")
	public List<Map<String, Object>> getNoticeList(@RequestParam Map<String, Object> param)throws Exception{
		List<Map<String, Object>> lst = noticeService.getNoticeList(param);
		return lst;
	}
	
	@ResponseBody
	@GetMapping("/notice/list/{noticeNo}")
	public Map<String, Object> getNoticeDetails(@PathVariable("noticeNo") int noticeNo)throws Exception{
		return noticeService.getNoticeDetails(noticeNo);
	}
	
	@ResponseBody
	@PostMapping("/notice")
	public List<Map<String, Object>> regNotice(Map<String, Object> param) throws Exception{
		return noticeService.regNotice(param);
	}
}
