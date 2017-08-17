package com.gy.hsi.ds.common.utils;

import org.apache.log4j.Logger;

/**
 * Created by knightliao on 15/1/7.
 */
public class CodeUtils2 {

    protected static final Logger LOG = Logger.getLogger(CodeUtils2.class);

    /**
     * utf-8 转换成 unicode
     *
     * @param inStr
     *
     * @return
     *
     * @author fanhui
     * 2007-3-15
     */
    public static String utf8ToUnicode(String inStr) {

        char[] myBuffer = inStr.toCharArray();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < inStr.length(); i++) {
            Character.UnicodeBlock ub = Character.UnicodeBlock.of(myBuffer[i]);
            if (ub == Character.UnicodeBlock.BASIC_LATIN) {
                sb.append(myBuffer[i]);
            } else if (ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                int j = (int) myBuffer[i] - 65248;
                sb.append((char) j);
            } else {
                int chr1 = (char) myBuffer[i];
                String hexS = Integer.toHexString(chr1);
                String unicode = "\\u" + hexS;
                sb.append(unicode.toLowerCase());
            }
        }
        return sb.toString();

    }

    /**
     * unicode 转换成 utf-8
     *
     * @param theString
     *
     * @return
     *
     * @author fanhui
     * 2007-3-15
     */
    public static String unicodeToUtf8(String theString) {
        char aChar;
        char pChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
            	pChar = aChar;
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    boolean isNotUnicode = false;
                    String str = "";
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                //throw new IllegalArgumentException("Malformed   \\uxxxx   encoding." + value);
                                // filter this bad case
                                value = 0;
                                isNotUnicode = true;
                                str += String.valueOf(aChar);
                                break;
                        }
                    }
                    if(isNotUnicode) {
                    	outBuffer.append(str);
                    } else {
                    	outBuffer.append((char) value);
                    }
                } else {
                    if (aChar == 't') {
                        aChar = '\t';
                        outBuffer.append(aChar);
                    } else if (aChar == 'r') {
                        aChar = '\r';
                        outBuffer.append(aChar);
                    } else if (aChar == 'n') {
                        aChar = '\n';
                        outBuffer.append(aChar);
                    } else if (aChar == 'f') {
                        aChar = '\f';
                        outBuffer.append(aChar);
                    } else {
                    	outBuffer.append(pChar).append(aChar);
                    }
                }
            } else {
                outBuffer.append(aChar);
            }
        }
        return outBuffer.toString();
    }
}