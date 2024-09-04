package com.iecube.ota.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;

public class FileMd5Util {

    public static String getMD5(String path) {
        String md5Hex = null;
        try {
            md5Hex = DigestUtils.md5Hex(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return md5Hex;
    }
}


