package com.maxrun.application.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.crypto.KeyGenerator;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import com.maxrun.application.exception.BizException;

/**
 * Created by IntelliJ IDEA.
 * User: ddochi
 * Date: 2021-08-20
 * Time: 오후 2:06
 * Luck is the residue of design. -Branch Rickey
 * <PRE>gslp-user|utils</PRE>
 */
public class CommonUtils {


    private Logger logger = LogManager.getLogger(this.getClass());

    public static JSONObject getJsonObject(String apiAddress) throws IOException, JSONException {
        if (apiAddress != null && !StringUtils.isEmpty(apiAddress)){
            URL url = new URL(apiAddress);
            //fixme time out 10seconds
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(1000);
            JSONObject jsonObject = new JSONObject(setBr(new InputStreamReader(connection.getInputStream())).readLine());
            return jsonObject;
        }
        return null;
    }

    public static BufferedReader setBr(InputStreamReader inputStreamReader) throws IOException {
        if (inputStreamReader == null)
            throw new IOException("InputStreamReader is Null");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        return bufferedReader;
    }

    public static String getClientIp(HttpServletRequest request) {
        // TODO: Set header in APACHE
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip.split(",")[0].trim();
    }

    /*public static String configViewResolvers(HttpServletRequest request){
        if (((String) request.getAttribute("country")).equalsIgnoreCase(Constants.NONE))
            return "en/";
        return request.getAttribute("country") + "/";
    }*/

    /** returns querystring ('?' not included) */
    public static String getQueryString(Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        for (String key : params.keySet()) {
            if (params.get(key) != null)
                sb.append(key).append("=").append(params.get(key)).append("&");
        }
        return sb.length() > 0 ? sb.toString() : "";
    }
    
   	//전체 국가목록 리턴, 회원가입시 국가 목록 제공해야 함
    public List<Map<String, String>> getISOCountries(){
    	String[] countries = Locale.getISOCountries();
    	Map<String, String> countryMap = new HashMap<String, String>();
    	List<Map<String, String>> countryList = new ArrayList<Map<String, String>>();
    	
    	for(String country:countries) {
    		Locale l = new Locale("en", country);
    		
    		countryMap.put("nationCd", country);
    		countryMap.put("nationName", l.getDisplayCountry(new Locale("en")));    
    		countryMap.put("currencyCode", Currency.getInstance(l).getDisplayName(l));
    		countryMap.put("currencySymbol", Currency.getInstance(l).getSymbol(l));
    		
    		countryList.add(countryMap);
    	}
    	
    	return countryList;
    }

    public static Integer getInteger(Object value) {
        try {
            if (value == null) return null;
            if (value instanceof Integer) return (Integer) value;
            if (value instanceof String) {
                return StringUtils.isEmpty(value) ? null : Integer.valueOf((String) value);
            }
        } catch (NumberFormatException ignored) {
            throw new NumberFormatException("Cannot convert " + value.getClass().getName() + "(value:" + value + ") into Integer.");
        }
        throw new NumberFormatException("Cannot convert " + value.getClass().getName() + "(value:" + value + ") into Integer.");
    }


    public static String getString(Object value) {
        try {
            if (value == null) return null;
            if (value instanceof Integer) return String.valueOf(value);
            if (value instanceof String) return (String) value;
        } catch (NumberFormatException ignored) {
            throw new NumberFormatException("Cannot convert " + value.getClass().getName() + "(value:" + value + ") into String.");
        }
        throw new NumberFormatException("Cannot convert " + value.getClass().getName() + "(value:" + value + ") into String.");
    }

    /**
     * 이메일 유효성 검사
     * @param email
     * @return
     */
    public static boolean isEmailForm(String email) {
        if(StringUtils.isEmpty(email))
            return false;
        return Pattern.matches("[\\w\\~\\-\\.]+@[\\w\\~\\-]+(\\.[\\w\\~\\-]+)+", email.trim());
    }
    
	/* SHA-256 암호화 */
//	public static String sha256Enc(String str) throws NoSuchAlgorithmException {
//	    if (StringUtils.isEmpty(str)) return null;
//
//        MessageDigest md = MessageDigest.getInstance("SHA-256");
//        md.update(str.getBytes());
//        byte byteData[] = md.digest();
//        StringBuffer sb = new StringBuffer();
//        for(int i=0; i<byteData.length; i++) {
//            sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
//        }
//        String retVal = sb.toString();
//        return retVal;
//	}

//	/** get timezone id from countryCode */
//    public static List<String> getAvailableTimeZones(String countryCode, boolean excludeAbbreviations) {
//        List<String> availableTimezones = new ArrayList<>();
//        for (String id : com.ibm.icu.util.TimeZone.getAvailableIDs(countryCode)) {
//            if (!excludeAbbreviations || id.length() > 3)
//            	/*US/Pacific-New 타임존은 미래에 사용가능성을 염두에 두고 만들어둔 예약 타밈존임, 사용하면 안됨*/
//                if(!id.equals("US/Pacific-New")) availableTimezones.add(id);
//        }
//        
//        return availableTimezones;
//    }
//    public static Map<String, List<String>> getAllAvailableTimeZones() {
//        Map<String, List<String>> map = new HashMap<>();
//
//        String[] isoCountries = Locale.getISOCountries();
//        for (String countryCode : isoCountries) {
//            map.put(countryCode, getAvailableTimeZones(countryCode, true));
//        }
//
//        return map;
//    }

    /** 타임존을 inject하여 리턴한다. */
//    public static ZonedDateTime injectTimezone(Timestamp timestamp, String timezone) {
//        Instant instant = timestamp.toInstant();
//        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.of(timezone));
//        return zonedDateTime;
//    }

    /** 특정 시간대의 시각이 현재 기준으로 과거인지 판단한다. */
    public static boolean isPastDateTime(Timestamp timestamp, String timezone) {
        // timestamp에 timezone을 적용
        ZonedDateTime zonedDateTime = ZonedDateTime.of(timestamp.toLocalDateTime(), ZoneId.of(timezone));
        // unix-time 으로 현재시각과 비교하여 리턴
        return zonedDateTime.toEpochSecond() < Instant.now().getEpochSecond();
    }

    /**
     * Api Rest 통신 - requestBody
     * messageBody -> 특정 문구 치환
     * 예) ###MSG### -> MSG만 골라온다.
     * @param responseEntity
     * @param messageBody
     * @return
     */
    public static String replaceRegularExMsgBody(ResponseEntity<?> responseEntity, String messageBody) {
        return responseEntity.getBody().toString().replaceAll("(###)(.*.?)(###)", messageBody);
    }


    /**
     * string 왼쪽으로 length 만큼 padChar 를 채운다.
     * */
    public static String padLeft(String string, int length, char padChar) {
        if (string.length() >= length) {
            return string;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - string.length()) {
            sb.append(padChar);
        }
        sb.append(string);
        return sb.toString();
    }

    public static String getRandomString(int length) {
        SecureRandom rand = new SecureRandom();
        if (length < 1) return "";
        StringBuilder sb = new StringBuilder();
        String pool = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (int i = 0; i < length; i++) {
            int index = rand.nextInt(pool.length());
            sb.append(pool.charAt(index));
        }
        return sb.toString();
    }

    public static String removeHtmlTags (String html) {
        return html.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
    }

    /**
     * speaker language list getting method
     * @// FIXME: 2021-10-29 해당 메소드는 한국어 미사용으로 사용 하지 않음.
     * @param target
     * @param lang
     * @return
     */
    @Deprecated
    public static List<Map<String, Object>> setLanguageCollection(List<Map<String, Object>> target, String lang) {
        return target.stream().filter("EN".equalsIgnoreCase(lang) ? s -> "0002600001".equals(s.get("languageCd")) : s -> "0002600002".equals(s.get("languageCd"))).collect(Collectors.toList());
    }

    public static boolean isLocal(){
    	String localIp;
		try {
			localIp = getLocalIPAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    	return (localIp.startsWith("0:0:0:0:0:0:0") || localIp.startsWith("127.0.0.1")|| localIp.startsWith("172.20.90"));
        //return ("local".equals(System.getProperty("spring.profiles.active")));
    }
    
    public static boolean isApp() {
    	String userAgent = HttpServletUtils.getRequstHeaderValue("User-Agent");
    	if(HttpServletUtils.getRequest().getRequestURI().contains("/mgr") || HttpServletUtils.getRequest().getRequestURI().contains("mgr")) return false;

    	boolean mobileApp = null != userAgent && -1 < userAgent.indexOf( "MobileApp" );
    	//mobileApp = true;	//테스트 때문에 하드코딩
    	return mobileApp;
    }
    
    public static String getLocalIPAddress() throws UnknownHostException {
    	InetAddress addr = InetAddress.getLocalHost();
    	System.out.println("####################################" + addr.getHostAddress());
    	return addr.getHostAddress();
    }
    //개발환경에서 접속했는지 여부 
    public static boolean isDeveloper() {
    	String ip = getClientIp(HttpServletUtils.getRequest());
    	
    	if(ip.startsWith("0:0:0:0:0:0:0") || ip.startsWith("127.0.0.1")|| ip.startsWith("172.20.90") || ip.equals("211.60.149.96")) { //로컬호스트, 사설명, 오피스 인터넷망 게이트웨이 주소
    		return true;
    	}
    	return false;
    }

    public static Calendar getCalendarObject(String dateStr) throws BizException{
    	
    	dateStr = dateStr.replaceAll(":", "");
    	dateStr = dateStr.replaceAll("-", "");
    	dateStr = dateStr.replaceAll(" ", "");
    	dateStr = dateStr.trim();
    	
    	if(dateStr.length()!=14) throw new BizException("yyyyMMddHHmmss 형식 문자열이 아닙니다");
    	
    	SimpleDateFormat sfmt = new SimpleDateFormat("yyyyMMddHHmmss");
    	try {
    		Date date = sfmt.parse(dateStr);
    		Calendar cal = Calendar.getInstance();
        	cal.setTime(date);      
        	
        	return cal;
    	}catch(ParseException e) {
    		e.printStackTrace();
    		throw new BizException("yyyyMMddHHmmss 형식 문자열이 아닙니다");
    	}	
    }
    
    public static Calendar getCalendarObject(long milliSeconds) {
    	Calendar cal = Calendar.getInstance(); 
    	cal.setTimeInMillis(milliSeconds);
        return cal;
    }
    
    public static Calendar getCalendarObject(Date date){
    	Calendar cal = Calendar.getInstance();
    	
    	cal.setTime(date); 
    	
    	return cal;
    }
    
    //Calendar 객체를 formatStr 형식의 문자열로 변환하여 반환
    public static String getCalendarToFormatString(Calendar cal, String formatStr) {
    	SimpleDateFormat dateFormat = null;
    	
    	try {
    		dateFormat = new SimpleDateFormat(formatStr);
    	}catch(NullPointerException e) {
    		e.printStackTrace();
    		throw e;
    	}catch(IllegalArgumentException e) {
    		e.printStackTrace();
    		throw e;
    	}
    	
    	return dateFormat.format(cal.getTime());
    }

    public static String getAppContxt() {
    	
    	if (isApp()) return "user";
    	
    	HttpServletRequest req = HttpServletUtils.getRequest();
    	
    	if (req.getRequestURI().indexOf("/mgr") > -1 || req.getRequestURI().indexOf("/mgr/") > -1) {
    		return "mgr";
    	}else if(req.getHeader("referer") != null && req.getHeader("referer").contains("/mgr")) {
    		return "mgr";
    	}
    	
    	return "user";
    }
    
    public static String decodeJWTToken(String encodeToken) {
    	String decodedToken = null;
    	
    	String[] parts = encodeToken.split("\\.");
    	
    	Base64.Decoder decoder = Base64.getUrlDecoder();
    	
    	String header = new String(decoder.decode(parts[0]));
    	String payload = new String(decoder.decode(parts[1]));
    	String signature = new String(decoder.decode(parts[2]));
    	
    	decodedToken = header + "." + payload;
    	
    	return decodedToken;
    }
    
    public static Key getKeyForAES256() throws NoSuchAlgorithmException{
    	KeyGenerator keyGen = KeyGenerator.getInstance("AES");
    	keyGen.init(256);
    	
    	return keyGen.generateKey();
    	
    }
    //문자열이 base64 인코딩인지 아닌지 확인
    public static boolean isBase64Encoded(String encodedString) {
    	return org.apache.commons.codec.binary.Base64.isArrayByteBase64(encodedString.getBytes());
    }
    
    
//    public static Calendar toEpoch(String timestamp) {
//    	long epochTime = Instant
//    	
//    	return null;
//    }
//    
//    public static Calendar toEpoch(Timestamp timestamp) {
//    	long epochTime = Instant.
//    	
//    	return null;
//    }
    
}
