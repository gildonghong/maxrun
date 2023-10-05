package com.maxrun.application.common.crytography;

/**
 * @author gildo
 * @date 2021. 6. 30
 * @desc 
 */
public interface IEncodingHelper {

    String encode(byte[] messageBytes);

    byte[] decode(String encodedMessage);

}