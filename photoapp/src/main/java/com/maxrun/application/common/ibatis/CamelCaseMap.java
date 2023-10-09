package com.maxrun.application.common.ibatis;

import java.util.HashMap;
import org.springframework.jdbc.support.JdbcUtils;

@SuppressWarnings("serial")
public class CamelCaseMap extends HashMap<String, Object> {

	public Object put(String key, Object value) {
		
		if(key.equalsIgnoreCase("BEFORE_OPTYPE") || key.equalsIgnoreCase("AFTER_OPTYPE") || key.equalsIgnoreCase("OP_TYPE"))  
			return super.put(key, value);
		else return super.put(JdbcUtils.convertUnderscoreNameToPropertyName(key), value);
	}
}
