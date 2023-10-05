package com.maxrun.repairshop.carcare.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MsgSenderCtr {
	//how to 알림톡
	@PostMapping("/repairshop/carcare/message")
	public void sendNoticeByKakaotalk()throws Exception{
		
	}
}
