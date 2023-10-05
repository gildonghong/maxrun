package com.maxrun.application.common.crytography;

import java.security.NoSuchAlgorithmException;

/**
 * @author gildo
 * @date 2021. 6. 30
 * @desc 
 */
public interface ICryptographyHelper {
	
	/*
	 * public byte[] encrypt(byte[] plainMessage);
	 * 
	 * public byte[] decrypt(byte[] encryptedMessage);
	 */
	
//    public String encrypt(String plainString) throws ScpDbAgentException;
//    
//    public String decrypt(String plainString) throws ScpDbAgentException;
    
    public String encAES(String painStr) ;//throws NoSuchAlgorithmException, GeneralSecurityException, UnsupportedEncodingException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException;
    
    public String decAES(String encStr) ;//throws NoSuchAlgorithmException, GeneralSecurityException, UnsupportedEncodingException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException;

    public String sha256(String str) throws NoSuchAlgorithmException;
}
