/*
 * Copyright (c) 2012-2016 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package cn.fast.jni;

import android.os.Build;

import java.security.InvalidKeyException;

public class JNIUtils {
    public static native byte[] rsasign(byte[] input, int pkey, boolean pkcs1padding) throws InvalidKeyException;

    public static native String[] getIfconfig() throws IllegalArgumentException;

    public static native void jniclose(int fdint);

    public static String initVPNServer() {
        if (isRoboUnitTest())
            return "ROBO";
        else
            return init();
    }

    private static native String init();

    static {
        if (!isRoboUnitTest()) {
            System.loadLibrary("ovpnutil");
//            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN)
//                System.loadLibrary("jbcrypto");
        }
    }

    public static boolean isRoboUnitTest() {
        return "robolectric".equals(Build.FINGERPRINT);
    }

}