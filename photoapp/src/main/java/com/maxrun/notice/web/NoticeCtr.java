package com.maxrun.notice.web;

import java.util.HashMap;
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

import com.maxrun.application.common.auth.service.JWTTokenManager;
import com.maxrun.application.common.utils.HttpServletUtils;
import com.maxrun.application.exception.BizExType;
import com.maxrun.application.exception.BizException;
import com.maxrun.notice.service.NoticeService;

@Controller
public class NoticeCtr {
	@Autowired
	private NoticeService noticeService;
	@Autowired
	JWTTokenManager jwt;
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
	public Map<String, Object> regNotice(@RequestParam Map<String, Object> param) throws Exception{
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		
		if(!claims.get("repairShopNo").equals(-1)) {
			throw new BizException(BizExType.NOT_AUTHORIZED, "맥스런 사용자가 아닌경우 공지사항 등록은 불가합니다");
		}

		
		if (param.containsKey("delYn") && param.get("delYn").equals("Y")) {
			Map<String, Object> ret = new HashMap<String, Object>();
			int noticeNo = Integer.parseInt(String.valueOf(param.get("noticeNo")));
			int delCnt = noticeService.delNotice(noticeNo);
			
			ret.put("delCnt", delCnt);
			return ret;
		}else {
			param.put("regUserId",claims.get("workerNo"));
			param.put("outNoticeNo", 0);
			return noticeService.regNotice(param);
		}
		

//		param.replace("noticeNo", param.get("outNoticeNo"));
//		return param;
	}
}
