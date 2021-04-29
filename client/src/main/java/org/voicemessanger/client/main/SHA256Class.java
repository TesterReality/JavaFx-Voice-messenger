package org.voicemessanger.client.main;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256Class {

    public SHA256Class() {
    }

    public String getSHA256(String msg)
    {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");

            md.update(msg.getBytes());

            byte byteData[] = md.digest();
            StringBuffer sb = new StringBuffer();

            for(int i=0;i<byteData.length;i++)
            {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));// ??
            }

            String getHexValue = sb.toString();
            System.out.println("SHA256("+msg+") = "+getHexValue);
            return  getHexValue;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "";
    }
    public byte[] getByteSHA256(String msg)
    {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");

            md.update(msg.getBytes());

            byte byteData[] = md.digest();

            return  byteData;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }
}
