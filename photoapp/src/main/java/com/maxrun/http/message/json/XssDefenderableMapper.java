package com.maxrun.http.message.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxrun.http.message.json.escape.HTMLCharacterEscapes;

public class XssDefenderableMapper extends ObjectMapper {
	private static final long serialVersionUID = 1L;

	public XssDefenderableMapper() {
		super();
		
		this.getFactory().setCharacterEscapes(new HTMLCharacterEscapes());
	}
}
