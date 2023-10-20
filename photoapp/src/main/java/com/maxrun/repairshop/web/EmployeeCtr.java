package com.maxrun.repairshop.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.maxrun.application.common.auth.service.JWTTokenManager;
import com.maxrun.application.common.utils.CookieUtils;
import com.maxrun.application.common.utils.HttpServletUtils;
import com.maxrun.application.exception.BizExType;
import com.maxrun.application.exception.BizException;
import com.maxrun.repairshop.service.EmployeeService;

@Controller
public class EmployeeCtr {
	
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private JWTTokenManager jwt;
	
	@ResponseBody
	@GetMapping("/repairshop/employee/list")
	public List<Map<String, Object>> getWorkerList(@RequestParam Map<String, Object> param) throws Exception{
		return employeeService.getWorkerList(param);
	}
	
	@ResponseBody
	@PostMapping("/repairshop/employee")
	public Map<String, Object> regWorker(@RequestParam Map<String, Object> param)throws Exception{ 
		Map<String, Object> claims = jwt.evaluateToken(String.valueOf(HttpServletUtils.getRequest().getSession().getAttribute("uAtoken")));
		
		/*작업자 신규 등록시만 파라미터 유효성 체크 진행*/
		if(param.get("workerNo")==null && param.get("workerNo").equals(0)) {
			if(param.get("departmentNo")==null || param.get("departmentNo").equals(0)) {
				throw new BizException(BizExType.PARAMETER_MISSING, "부서는 필수값입니다");
			}
			
			if(param.get("loginId")==null || param.get("loginId").equals("")) {
				throw new BizException(BizExType.PARAMETER_MISSING, "로그인ID는 필수값입니다");
			}
			
			if(param.get("pwd")==null || param.get("pwd").equals("")) {
				throw new BizException(BizExType.PARAMETER_MISSING, "초기비밀번호는 필수값입니다");
			}
			
			if(param.get("workerName")==null || param.get("pwd").equals("")) {
				throw new BizException(BizExType.PARAMETER_MISSING, "셩명은 필수값입니다");
			}
		}

		param.put("regUserId", claims.get("workerNo"));
		param.put("outWorkerNo", null);
		return employeeService.regWorker(param);
	}
}
