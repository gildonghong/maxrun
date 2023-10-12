package com.maxrun.application.common.auth.service;

import java.security.Key;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.maxrun.application.common.crytography.DefaultCryptographyHelper;
import com.maxrun.application.common.utils.CommonUtils;
import com.maxrun.application.common.utils.CookieUtils;
import com.maxrun.application.common.utils.HttpServletUtils;
import com.maxrun.application.exception.BizExType;
import com.maxrun.application.exception.BizException;

import eu.bitwalker.useragentutils.BrowserType;
import eu.bitwalker.useragentutils.UserAgent;
import io.jsonwebtoken.ClaimJwtException;
//import com.ebs.gslp.common.auth.service.AuthProvider;
//import com.ebs.gslp.common.auth.token.service.ITokenManager;
//import com.ebs.gslp.common.crytography.ICryptographyHelper;
//import com.ebs.gslp.common.utils.CommonUtils;
//import com.ebs.gslp.common.utils.CookieUtils;
//import com.ebs.gslp.common.utils.HttpServletUtils;
//import com.ebs.gslp.exception.BizExType;
//import com.ebs.gslp.exception.BizException;
//import com.ebs.gslp.spring.util.RequestUtils;
//import com.ebs.gslp.spring.web.configuration.PropertyManager;
//import com.ebs.gslp.user.service.LoginMapper;
//import com.ebs.gslp.user.service.MemberService;
//import com.ebs.gslp.user.service.MyPageService;
//import com.ebs.gslp.user.service.impl.MyPageMapper;
//import com.google.common.collect.ImmutableMap;
//import eu.bitwalker.useragentutils.BrowserType;
//import eu.bitwalker.useragentutils.UserAgent;
//import io.jsonwebtoken.ClaimJwtException;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.JwtBuilder;
//import io.jsonwebtoken.JwtException;
//import io.jsonwebtoken.JwtParserBuilder;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.MalformedJwtException;
//import io.jsonwebtoken.RequiredTypeException;
//import io.jsonwebtoken.UnsupportedJwtException;
//import io.jsonwebtoken.security.Keys;
//import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.RequiredTypeException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;

@SuppressWarnings("deprecation")
@Service
public class JWTTokenManager implements ITokenManager {

	protected final Key KEY;
	private final long ONE_HOUR_SECONDS = 60 * 60; // 관리자는 1시간동안 리프레쉬 토큰을 유호하게 한다
	@Value("${Globals.jwt.access-token.guest}")
	private Long ACCESS_TOKEN_EXPIRATION_MIN_GUEST;
	@Value("${Globals.jwt.access-token.member}")
	private Long MEMBER_ACCESS_TOKEN_EXPIRATION_MIN;
	@Value("${Globals.jwt.refresh-token.free}")
	private Long REFRESH_TOKEN_EXPIRATION_MIN_FREE;
//	@Value("${Globals.jwt.refresh-token.paid}")
//	private Long REFRESH_TOKEN_EXPIRATION_SEC_PAID;
	@Value("${Globals.jwt.refresh-token.mobile-app}")
	private Long REFRESH_TOKEN_EXPIRATION_MIN_APP;

	/**
	 * 상품 구매 후 토큰 갱신을 위한 키
	 */
	public static final String NEW_SERVICE_END_DATE_ATTR_KEY = "newServiceEndDate";

	Logger logger = LogManager.getLogger(this.getClass());
	
	@Autowired
	protected JWTTokenMapper JWTTokenMapper;

	@Autowired
	private DefaultCryptographyHelper cryptographyHelper;
		
	JWTTokenManager(@Value("${Globals.jwt.key}") String myKey) {
		this.KEY = Keys.hmacShaKeyFor(myKey.getBytes());
	}

	@Override
	public Map<String, Object> generateToken(Map<String, Object> param) throws JwtException, BizException {
		Map<String, Object> ret = null;
		try {
			if(param.get("workerNo") == null)  throw new BizException(BizExType.PARAMETER_MISSING,"workerNo is required");

			long nowMillis = System.currentTimeMillis();
			long expirationMillis = nowMillis + (MEMBER_ACCESS_TOKEN_EXPIRATION_MIN  * 60 * 1000); //(분 ->초 -> 밀리세컨)한 값을 nowMillis에 더한다
			//long expirationMillis = nowMillis + 5000; //(분 ->초 -> 밀리세컨)한 값을 nowMillis에 더한다

			String jti = UUID.randomUUID().toString();
			
			//if(CommonUtils.isApp())	expirationMillis = nowMillis + (60*1000);	//1분짜리 access token
			String accessToken = createToken(jti, KEY, nowMillis, expirationMillis, param);
			logger.info("accessToken 유효기간: " + CommonUtils.getCalendarToFormatString(CommonUtils.getCalendarObject(expirationMillis), "yyyyMMdd HH:mm:ss"));
			ret = Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(accessToken).getBody();
			
			logger.info("accessToken: expicreateTokenrationMillis is " + expirationMillis);
			logger.info(CommonUtils.getCalendarToFormatString(CommonUtils.getCalendarObject(expirationMillis), "yyyyMMdd HH:mm:ss"));

//			if(param.containsKey("memberTypeCd") && param.get("memberTypeCd").equals("0000200002")) {	//미결제
//				expirationMillis =nowMillis + (REFRESH_TOKEN_EXPIRATION_MIN_FREE*1000);
//				
//				logger.info("membertype 0000200002 refreshtoken: expirationMillis is " + expirationMillis);
//				logger.info("membertype 0000200003  refreshtoken: expirationMillis is " + expirationMillis);
//				logger.info(CommonUtils.getCalendarToFormatString(CommonUtils.getCalendarObject(expirationMillis), "yyyyMMdd HH:mm:ss"));
//				//expirationMillis =nowMillis + 10000;
//			}else if(param.containsKey("memberTypeCd") && param.get("memberTypeCd").equals("0000200003")){	//유료
//				Map<String, Object> membershipInfo = memberService.getMemberInformation(param);
//				//존재하지 않는 사용자인 경우
//				if(membershipInfo==null) throw new BizException(BizExType.NO_MATCHING_USER_FOUND, "meberNo does not exists!!");
//
//				Timestamp ts = Timestamp.valueOf(String.valueOf(membershipInfo.get("serviceEndDate")));
//				//Timestamp getTime() : Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT represented by this Date object.
//				expirationMillis = ts.getTime();
//				
//				logger.info("membertype 0000200003  refreshtoken: expirationMillis is " + expirationMillis);
//				logger.info(CommonUtils.getCalendarToFormatString(CommonUtils.getCalendarObject(expirationMillis), "yyyyMMdd HH:mm:ss"));
//			}else {	//관리자
//				expirationMillis =nowMillis + ONE_HOUR_SECONDS * 1000;
//				
//				logger.info("manager refreshtoken : expirationMillis is " + expirationMillis);
//				logger.info("membertype 0000200003  refreshtoken: expirationMillis is " + expirationMillis);
//				logger.info(CommonUtils.getCalendarToFormatString(CommonUtils.getCalendarObject(expirationMillis), "yyyyMMdd HH:mm:ss"));
//			}
			//App에서 로그인하는 경우라면 1년 간 리프레쉬 토큰을 유효하게 한다
			if(CommonUtils.isApp()) {
				Calendar cal = Calendar.getInstance();

				cal.add(Calendar.MONTH, 6);
				//cal.add(Calendar.MINUTE, 5);	//5분간 유효한 리프레시 토큰

				expirationMillis = cal.getTimeInMillis();
			}
			String refreshToken = createToken(jti, KEY, nowMillis, expirationMillis, null);	// refresh token 발행시 claim 파람은 null을 전달한다
			logger.info("refreshToken 유효기간: " + CommonUtils.getCalendarToFormatString(CommonUtils.getCalendarObject(expirationMillis), "yyyyMMdd HH:mm:ss"));
			//생성된 REF 토큰을 DB에 저장, refresh token의 경우 암호화된 토큰값이 생성되므로 DB저장시는 복호화해서 저장한다
			Map<String, Object> refreshTokenClaims = Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(refreshToken).getBody();
			//refresh token을 DB에 저장하기 위해 필요 파라미터를 맵변수에 할당
			Map<String, Object> tokenInfo = new HashMap<String, Object>();
			tokenInfo.put("tokenNo",refreshTokenClaims.get("tokenNo"));
			tokenInfo.put("token", refreshToken);
			tokenInfo.put("workerNo", param.get("workerNo"));
			tokenInfo.put("tokenType", "R");
			tokenInfo.put("outTokenNo", null);
			
			JWTTokenMapper.regRefreshToken(tokenInfo);

			//App의 경우 쿠키를 expiryTime까지 영속시켜야 함
			//if(CommonUtils.isApp()) {
				int exp = Integer.parseInt(evaluateToken(accessToken).get("exp").toString());
				CookieUtils.addCookie("uAtoken", accessToken,getMaxAge((int)(nowMillis/1000), exp));
				exp = Integer.parseInt(refreshTokenClaims.get("exp").toString());
				CookieUtils.addCookie("uRtoken", refreshToken, getMaxAge((int)(nowMillis/1000), exp));
				
				logger.info("#########################################################################");
				logger.info("#########################################################################");
				logger.info("########################### non session cookie created ###############################");
				logger.info("#########################################################################");
				logger.info("#########################################################################");
			//}
//			else {
//				CookieUtils.addCookie(param.get("userId") == null?"uAtoken":"mAtoken", accessToken, -1);
//				CookieUtils.addCookie(param.get("userId") == null?"uRtoken":"mRtoken", refreshToken, -1);
//				
//				logger.info("#########################################################################");
//				logger.info("#########################################################################");
//				logger.info("########################### session cookie created###############################");
//				logger.info("#########################################################################");
//				logger.info("#########################################################################");
//			}

			ret.put("uAtoken", accessToken);
			ret.put("uRtoken", refreshToken);
			
		}catch (JwtException | SQLException e) {
			e.printStackTrace();
			throw new BizException(BizExType.TOKEN_GENERATION_FAIL, "token genration is failed check the application!!!");
		}

		return ret;
	}
	
	private long getMaxAge(long start, long end) {
		
		long expiry = (end - start)/1000;
	
		return expiry;
	}

	private int getMaxAge(int start, int end) {
	
		int expiry = (end - start);
		
		return expiry;
	}

	@Override
//	public Map<String, Object> evaluateToken() throws JwtException, BizException {	//현재 세션의 쿠키로 유지되고 있는 토큰의 유효성을 검증한다
//		Map<String, Object> ret = null;
//		String accessToken = null;
//		
//		if ((HttpServletUtils.getRequest().getRequestURI().contains("/mgr/") || HttpServletUtils.getRequest().getRequestURI().equals("/mgr")) && CookieUtils.getCookieValue("mAtoken") != null) {
//			accessToken = CookieUtils.getCookieValue("mAtoken");
//		}else {
//			if(HttpServletUtils.getRequest().getSession().getAttribute("managerInfo") != null && HttpServletUtils.getRequest().getSession().getAttribute("memberInfo") == null) {
//				accessToken = CookieUtils.getCookieValue("mAtoken");
//			}else if(HttpServletUtils.getRequest().getSession().getAttribute("memberInfo") != null && HttpServletUtils.getRequest().getSession().getAttribute("managerInfo") == null ){
//				accessToken = CookieUtils.getCookieValue("uAtoken");
//			}else {	// uAtoken, mAtoken 모두 존재시
//				if(HttpServletUtils.getRequstHeaderValue("referer").contains("/mgr") || HttpServletUtils.getRequest().getRequestURI().contains("/mgr")) {
//					accessToken = CookieUtils.getCookieValue("mAtoken");
//				}else {
//					accessToken = CookieUtils.getCookieValue("uAtoken");
//				}
//			}
//		}
//
//		try {
//
//			Claims claims = Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(accessToken).getBody();
//
//			if (!claims.containsKey("guestNo") && this.isStolenToken(accessToken)) { 				
//				// token 탈위여부 체크
//				deleteAllJWTToken();		  				
//				throw new BizException(BizExType.ACCESS_TOKEN_DIRTY,"access token may be counterfeited, so login retry is needed");
//			}else if(claims.containsKey("guestNo") && !StringUtils.isEmpty(CookieUtils.getCookieValue("uRtoken"))) {// 지속쿠키의 access token이 만료된 경우 
//				logger.info("############################################## access token is expired ################################");
//				return reGenerateToken(claims, CookieUtils.getCookieValue("uRtoken"));
//			}
//
//			return claims;
//
//		} catch (ExpiredJwtException e) {
//			if (e.getClaims().containsKey("guestNo")) {
//				return this.generateGuestToken(e.getClaims());
//			} else  return reGenerateToken(e.getClaims(), null);
//		} catch (RequiredTypeException e) {
//			e.printStackTrace();
//			deleteAllJWTToken();
//			throw e;
//		} catch (MalformedJwtException e) {
//			e.printStackTrace();
//			deleteAllJWTToken();
//			throw e;
//		} catch (SignatureException e) {
//			e.printStackTrace();
//			deleteAllJWTToken();
//			throw e;
//		} catch (UnsupportedJwtException e) {
//			e.printStackTrace();
//			deleteAllJWTToken();
//			throw e;
//		} catch (JwtException | SecurityException | IllegalArgumentException e) {
//			if(StringUtils.isEmpty(accessToken)) {	// 지속쿠키 access token 만료된 경우
//				return this.reGenerateToken(null, CookieUtils.getCookieValue("uRtoken"));
//			}
//			e.printStackTrace();
//			deleteAllJWTToken();
//			throw new BizException(BizExType.ACCESS_TOKEN_DIRTY, "access token is not valid, so login retry is needed");
//		} catch(BizException e) {
//			e.printStackTrace();
//			deleteAllJWTToken();
//			throw e;
//		}
//	}

	//매개변수로 전달된 토큰 문자열을 파싱하여 calims를 반환한다
	public Map<String, Object> evaluateToken(String token) throws BizException {

		try {
			logger.info("###### token ===>" + token);
			/*유효한 토큰이 없는 경우,다시 로그인 해야 함*/
			if (token==null && StringUtils.isEmpty(CookieUtils.getCookieValue("uRtoken"))) return null;
			
			Claims claims = Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token).getBody();
			
			logger.info("claims==>" + claims);
			
			return claims;
		} catch (ExpiredJwtException e) {
			try {
				logger.info("############################# access token expired ###############################");
				return this.reGenerateToken(e.getClaims(), null);
			}catch(JwtException e1) {
				e1.printStackTrace();
				deleteAllJWTToken();
				throw new BizException(BizExType.TOKEN_GENERATION_FAIL, "while trying re-generation access token with refresh token, exception occurs, check application logic!");
			}
		} catch (SecurityException | JwtException | IllegalArgumentException e) {
			if(StringUtils.isEmpty(token) ) {// 지속쿠키 access token 만료된 경우				
				return this.reGenerateToken(null, CookieUtils.getCookieValue("uRtoken"));
			}
			e.printStackTrace();
			deleteAllJWTToken();
			throw new BizException(BizExType.UNKNOWN, "while evaluating access token, exception occurs, check the application logic!!");
		} 
	}

	private String createToken(String jti, Key secretKey, long now, long expiration, Map<String, Object> claims)
			throws JwtException {
		String createdToken = null;
		String beforeEnc=null;
		// 로그인 정보 셋팅
		Claims param = Jwts.claims();
		
		param.putAll(claims);
		JwtBuilder builder = Jwts.builder().setClaims(param).signWith(secretKey).setId(jti) /* UUID */
				.setIssuer("www.maxrun.com").setExpiration(new Date(expiration))
				.setIssuedAt(new Date(now));
		
		//ipAddress, userAgent, 모바일 여부 등의 값을 설정한다
		HttpServletRequest req = HttpServletUtils.getRequest();
		UserAgent ua= UserAgent.parseUserAgentString(req.getHeader("userAgent"));
		
		builder = builder.claim("ipAddress", CommonUtils.getClientIp(req));											//client ipAddress											
		builder = builder.claim("userAgent", req.getHeader("User-Agent"));											//UserAgent String
		builder = builder.claim("mobileYn", ua.getBrowser().getBrowserType()==BrowserType.MOBILE_BROWSER?"Y":"N");	//모바일 기기에서 접속했는지 여부	
		builder = builder.claim("loginDate", now);

		if (claims != null) {	//accsstoken 또는 onetime url token 의 경우
			for (String strKey : claims.keySet()) {
				String[] reservedClaim = { "iss", "sub", "aud", "exp", "nbf", "iat" };

				if (!Arrays.asList(reservedClaim).contains(strKey)) {
					builder = builder.claim(strKey, claims.get(strKey));
				}

				/*
				 * if (strKey.equals("memberNo")) { //회원의 경우 builder =
				 * builder.setSubject("users/" + String.valueOf(claims.get(strKey))); }else
				 * if(strKey.equals("userId") && !claims.keySet().contains("memberNo")){ //관리자의
				 * 경우 builder = builder.setSubject("users/" +
				 * String.valueOf(claims.get(strKey))); }else { String[] reservedClaim = {"iss",
				 * "sub", "aud", "exp", "nbf", "iat"};
				 * 
				 * if(!Arrays.asList(reservedClaim).contains(strKey)) { builder =
				 * builder.claim(strKey, claims.get(strKey)); } }
				 */
			}
			createdToken = builder.compact();
			beforeEnc=createdToken;
		} else {//claims 파라미터가 null 이면 refresh token 생성 요청임
			createdToken = builder.compact();
			beforeEnc=createdToken;
			String beforEncStr = createdToken;
			//createdToken = cryptographyHelper.encAES(createdToken);
		}
		
		logger.info("createdToken -->" + Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(beforeEnc).getBody());
		return createdToken;
	}

	// expired로 인해 토큰 재발행시
	private Map<String, Object> reGenerateToken(Claims claims, String refreshToken) throws JwtException, BizException {
		String accessToken = null;
		JwtParserBuilder builder = Jwts.parserBuilder().setSigningKey(KEY); // Clock Skew를 1분으로 설정
		
		logger.info("reguest uri ==>" + HttpServletUtils.getRequest().getRequestURI());
		String refToken = refreshToken==null?CookieUtils.getCookieValue("uRtoken"):refreshToken;

		
		//refToken이 만료되면 브라우저에서 토큰을 삭제하기 때문에 쿠키에 refToken이 null일수 있음
		if (refToken==null || StringUtils.isEmpty(refToken)) {
			throw new BizException(BizExType.REF_TOKEN_EXPIRED, "refresh token is expired");
		}

		Claims refreshClaims = null;
		Map<String, Object> mp = null;
		try {
			// 리프레시 토큰 복호화
			//refToken = cryptographyHelper.decAES(refToken);
			mp = new HashMap<String, Object>();
			mp.put("token", refToken);
			// 클라이언트에서 보낸 리프레시 토큰 값이 DB에 있다면 ===> result != null
			Map<String, Object> refreshTokenFromDB = JWTTokenMapper.getRefreshToken(mp);
			// 리프레시 토큰 값이 DB에 없는 경우
			if (refreshTokenFromDB == null) {	
				throw new BizException(BizExType.REF_TOKEN_DIRTY, "refresh token may be counterfeited because the refresh token does not exists");
			}
			try {
				// 토큰 유효성 검사
				refreshClaims = Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(refToken).getBody();
				// 리프레시 토큰 탈취여부 체크(DB에 있는 토큰 값과 일치하지 않는 경우)
				if (!refToken.equals(refreshTokenFromDB.get("token"))) {
					throw new BizException(BizExType.REF_TOKEN_DIRTY, "refresh token may be counterfeited");
				};
			}catch (ExpiredJwtException e) { 
				throw new BizException(BizExType.REF_TOKEN_EXPIRED, "refresh token is expired");		
			}catch (JwtException | IllegalArgumentException e) { // 토큰없거나 오류
				this.deleteAllJWTToken();
				throw new BizException(BizExType.TOKEN_GENERATION_FAIL, "while re-generating token, exception occurs. check the application logic");		
			}

			// 유효한 리프레시 토큰 존재하므로 엑세스토큰 재발행
			String jti = null;
			long nowMillis = System.currentTimeMillis();
			long expirationMillis = nowMillis + (MEMBER_ACCESS_TOKEN_EXPIRATION_MIN * 60 * 1000);
			jti = (String) refreshClaims.get("jti"); // refreshToken으로 재 생성

			//if(CommonUtils.isApp())	expirationMillis =nowMillis + (60*1000); //app에서 접속한 경우 테스트를 위해서 1분짜리 access token을 발행해본다, 운영 적용전에는 코드 삭제 필요
			
			if((int)expirationMillis/1000 > Integer.parseInt(refreshClaims.get("exp").toString())) {//accesstoken exp 값이 refshtoken exp 값보다 큰 경우
				expirationMillis=Integer.parseInt(refreshClaims.get("exp").toString())*1000;
			}
			
//			if(claims==null) {	// access token이 만료되서 null로 넘어온 경우 
//				logger.info("####################################### access token is null, so user info get by reftoekn #######################################");
//				mp = new HashMap<String, Object>();
//				mp.put("token", refToken);
//				
//				Map<String, Object> userInfo= JWTTokenMapper.getToken(mp);
//				userInfo.put("isManager", false);
//				accessToken = createToken(jti, KEY, nowMillis, expirationMillis, userInfo);
//			}else {
//				accessToken = createToken(jti, KEY, nowMillis, expirationMillis, (Map<String, Object>) claims);
//			}
			
			//re-generating 된 access token을 쿠키에 저장한다
			CookieUtils.addCookie("uAtoken", accessToken, (int)this.getMaxAge(nowMillis, expirationMillis));
//			if(CommonUtils.isApp()) {//app에서 접속한 경우 쿠키 유지기한을 expirationMillis 와 동기화시킨다
//				CookieUtils.addCookie("uAtoken", accessToken, (int)this.getMaxAge(nowMillis, expirationMillis));
//			}else {
//				CookieUtils.addCookie(CookieUtils.getCookieValue("uRtoken")!=null?"mAtokedn":"uAtoken", accessToken, -1);	//app에서 접속한 것이 아니므로 세션 쿠키로 저장한다
//			}
		}catch (JwtException | SQLException | NullPointerException e) {
			e.printStackTrace();
			logger.error("reGenerateToken Exception : {}", e.getMessage());
			this.deleteAllJWTToken();
		}catch (BizException e) {
			this.deleteAllJWTToken();
			
			e.printStackTrace();
			
			if(e.getType()==BizExType.REF_TOKEN_EXPIRED) {
				throw e;
			}else if(e.getType()==BizExType.ACCESS_TOKEN_EXPIRED){
				throw e;
			}
			
		}
		
		return builder.build().parseClaimsJws(accessToken).getBody();
	}

//	@Override
//	public String createToken(Map<String, Object> param) throws Exception {
//
//		long nowMillis = System.currentTimeMillis();
//		long expirationMillis;
//		try {
//			expirationMillis = nowMillis
//					+ (Integer.parseInt(PropertyManager.get("Globals.jwt.token.lifeTime")) * 1000 * 60);
//		} catch (NumberFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} // token
//																										// lifeTime동안만
//																										// 유효하도록 설정
//		String jti = UUID.randomUUID().toString();
//
//		String accessToken = createToken(jti, KEY, nowMillis, expirationMillis, param);
//
//		return accessToken;
//	}
//
//	@Override
//	public String getToken() throws Exception {
//
//		String token = null;
//		Cookie[] cookies = RequestUtils.getRequest().getCookies();
//
//		if (cookies == null) {
//			token = RequestUtils.getRequest().getParameter("accessToken");
//		} else {
//			token = CookieUtils.getCookieValue("refreshToken");
//		}
//
//		return token;
//	}

	/**
	 * 토큰을 생성한다. ** 원타임 URL 생성 등에 사용되는 토큰 생성 메소드. 로그인 인증에서 사용하면 안됨
	 * 
	 * @param claims    토큰에 포함할 내용
	 * @param expMillis 유효기간 (milliseconds)
	 * @return (String) token
	 * @throws JwtException
	 */
	public String generateOneTimeToken(Map<String, Object> claims, long expMillis) throws JwtException {

		long nowMillis = System.currentTimeMillis();
		long expirationMillis = nowMillis + expMillis;
		String jti = UUID.randomUUID().toString();

		return createToken(jti, KEY, nowMillis, expirationMillis, claims);
	}
	/* 토큰 탈취여부 검증 */
	private boolean isStolenToken(String token) throws JwtException {
		Map<String, Object> map = null;
		try {
			// token 탈취여부 확인
			map = Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token).getBody();
		} catch (ExpiredJwtException e) {
			map = e.getClaims();
		} catch (ClaimJwtException e) {
			e.printStackTrace();
			throw e;
		} catch (MalformedJwtException e) {
			e.printStackTrace();
			throw e;
		} catch (RequiredTypeException e) {
			e.printStackTrace();
			throw e;
		} catch (SignatureException e) {
			e.printStackTrace();
			throw e;
		} catch (UnsupportedJwtException e) {
			e.printStackTrace();
			throw e;
		} catch (JwtException e) {
			e.printStackTrace();
			throw e;
		}

		String ipAddress = String.valueOf(map.get("ipAddress"));//ipAddress
		logger.debug("[Token ipAddress :::::::::::::]" + ipAddress);

		String userAgent = String.valueOf(map.get("userAgent"));
		logger.debug("[Token userAgent ::::::::::]" + userAgent);

		if (!CommonUtils.getClientIp(HttpServletUtils.getRequest()).equals(ipAddress)) {
			// token의 ip 와 현재 request ip가 다른 경우
			logger.debug("[현재 ipAddress ::::::::::: ]" + CommonUtils.getClientIp(HttpServletUtils.getRequest()));
			return true;
		} else if (!userAgent.equals(HttpServletUtils.getRequstHeaderValue("User-Agent"))) {
			// token의 userAgent와 현재 request User-Agent가 다른 경우
			logger.debug("[현재 userAgent ::::::::::::::::] " + HttpServletUtils.getRequstHeaderValue("User-Agent"));
			return true;
		}
		return false;
	}

	@Override
	public Map<String, Object> generateGuestToken(Map<String, Object> param) throws JwtException {
		// guest 토큰을 발급해서 쿠키에 저장한다
		long nowMillis = System.currentTimeMillis();
		long expirationMillis = nowMillis + ACCESS_TOKEN_EXPIRATION_MIN_GUEST * 60 * 1000;

		String jti=param==null? UUID.randomUUID().toString():(String)param.get("guestNo");
		
		param = new HashMap<String, Object>();
		param.put("guestNo", jti);
		param.put("isManager", false);

		String accessToken = createToken(jti, KEY, nowMillis, expirationMillis, param);
		
		CookieUtils.deleteCookie("uAtoken");
		CookieUtils.addCookie("uAtoken", accessToken, -1);

		return Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(accessToken).getBody();
	}

	@Override
	public Map<String, Object> getAuthenticatedUserInformation() throws BizException {
		String accessToken = null;

		if (HttpServletUtils.getRequest().getRequestURI().contains("/mgr/") || HttpServletUtils.getRequest().getRequestURI().equals("/mgr") && CookieUtils.getCookieValue("mAtoken") != null) {
			accessToken = CookieUtils.getCookieValue("mAtoken");
		}else {
			if(HttpServletUtils.getRequest().getSession().getAttribute("managerInfo") != null) {
				accessToken = CookieUtils.getCookieValue("mAtoken");
			}else if(HttpServletUtils.getRequest().getSession().getAttribute("memberInfo") != null){
				accessToken = CookieUtils.getCookieValue("uAtoken");
			}
		}
		
		if (accessToken==null) throw new BizException(BizExType.ACCESS_TOKEN_MISSING, "token is null");
		
		return Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(accessToken).getBody();
	}
	
	//uAtoken, mAtoken, uRtoken, mRtoken delete
	private void deleteAllJWTToken() {		
		CookieUtils.deleteCookie("mAtoken"); CookieUtils.deleteCookie("mRtoken");				  
		CookieUtils.deleteCookie("uAtoken"); CookieUtils.deleteCookie("uRtoken");
	}

	@Override
	public Map<String, Object> validateToken(String accessToken, String refreshToken) throws JwtException, BizException {
		
		//if(0==0) throw new BizException(BizExType.REF_TOKEN_EXPIRED);// 임시코드 , 배포전 반드시 삭제
		
		Map<String, Object> memberInfo = null;
		
		if (StringUtils.isEmpty(accessToken)) throw new BizException(BizExType.ACCESS_TOKEN_MISSING, "access token is null");

		try {
			
			memberInfo = Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(accessToken).getBody();

			if(CommonUtils.isApp()) {
				String refToken = cryptographyHelper.decAES(refreshToken);
				Map<String, Object> claims = this.evaluateToken(refToken);
				
				CookieUtils.addCookie("uAtoken", accessToken, (int)this.getMaxAge(System.currentTimeMillis()/1000, (long)claims.get("exp")));
				CookieUtils.addCookie("uRtoken", refreshToken, (int)this.getMaxAge(System.currentTimeMillis()/1000, (long)claims.get("exp")));
			}else {
				CookieUtils.addCookie("uAtoken", accessToken, -1);
				CookieUtils.addCookie("uRtoken", refreshToken, -1);
			}
			
		} catch (ExpiredJwtException e) {
			try {
				e.printStackTrace();
				return this.reGenerateToken(e.getClaims(), refreshToken);
			}catch(JwtException e1) {
				e1.printStackTrace();
				//deleteAllJWTToken();
				throw new BizException(BizExType.TOKEN_GENERATION_FAIL, "while trying re-generation access token with refresh token, exception occurs, check application logic!");
			}
		} catch (SecurityException | JwtException | IllegalArgumentException e) {
			e.printStackTrace();
			//deleteAllJWTToken();
			throw new BizException(BizExType.UNKNOWN, "while evaluating access token, exception occurs, check the application logic!!");
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return memberInfo;
	}

	@Override
	public String createToken(Map<String, Object> param) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getToken() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
 
}
