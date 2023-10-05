package com.maxrun.application.common.crytography;
/**
 * @author gildo
 * @date 2021. 6. 30
 * @desc 
 */

import java.util.Base64;

public class DefaultEncodingHelper implements IEncodingHelper {

    @Override
    public String encode(byte[] messageBytes) {
        return Base64.getEncoder().encodeToString(messageBytes);
    }

    @Override
    public byte[] decode(String encodedMessage) {
        return Base64.getDecoder().decode(encodedMessage);
    }
}
