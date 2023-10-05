package com.maxrun.application.common.crytography;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class DefaultCryptographyHelper implements ICryptographyHelper {

    Logger logger = LogManager.getLogger(this.getClass());

    final static String key = "j9g+7CAoTyK6T8MwP+w46gj9TBA6ac7N";
    private static String iv= key.substring(0,16);

	@Override
	public String sha256(String str) throws NoSuchAlgorithmException {
		if (StringUtils.isEmpty(str)) return null;

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(str.getBytes());
        byte byteData[] = md.digest();
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<byteData.length; i++) {
            sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
        }
        String retVal = sb.toString();
        return retVal;
	}

	@Override
	public String encAES(String plainStr) /*throws NoSuchAlgorithmException, GeneralSecurityException, UnsupportedEncodingException*/ {

		byte[] keyData = key.getBytes();
		String encStr = null;
		try {
			SecretKey secureKey = new SecretKeySpec(keyData, "AES");
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.ENCRYPT_MODE, secureKey, new IvParameterSpec(iv.getBytes("UTF-8")));
			
			byte[] encrypted = c.doFinal(plainStr.getBytes("UTF-8"));
			
			encStr =  new String(Base64.getEncoder().encode(encrypted));
		}catch(Exception e) {
			encStr="";
			e.printStackTrace();
		}
		
		return encStr;
	}

	@Override
	public String decAES(String encStr) /*throws NoSuchAlgorithmException, GeneralSecurityException, UnsupportedEncodingException*/ {
		byte[] keyData = key.getBytes();
		try {
			SecretKey secureKey = new SecretKeySpec(keyData, "AES");
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.DECRYPT_MODE, secureKey, new IvParameterSpec(iv.getBytes("UTF-8")));
			
			byte[] decryted= Base64.getDecoder().decode(encStr.getBytes());
			return new String(c.doFinal(decryted), "UTF-8");
		}catch(BadPaddingException e) {
			return "";
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return "";
	}
}