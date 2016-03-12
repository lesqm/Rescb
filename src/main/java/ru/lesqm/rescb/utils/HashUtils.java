package ru.lesqm.rescb.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HashUtils {

    private static final Logger log = LogManager.getLogger(HashUtils.class);

    public static String sha256String(String input) {

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update(input.getBytes("UTF-8"));

            return sha256ToString(md.digest());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            log.catching(ex);
        }
        return "";
    }

    public static String sha256String(File file) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            FileInputStream fis = new FileInputStream(file);

            byte[] dataBytes = new byte[1024];

            int nread = 0;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }

            return sha256ToString(md.digest());
        } catch (NoSuchAlgorithmException | IOException ex) {
            log.catching(ex);
        }
        return "";
    }

    private static String sha256ToString(byte[] digest) {
        return String.format("%064x", new java.math.BigInteger(1, digest));
    }
}
