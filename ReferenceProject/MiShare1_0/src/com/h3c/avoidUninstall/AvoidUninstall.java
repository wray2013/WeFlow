package com.h3c.avoidUninstall;

public class AvoidUninstall {
    private static final String libSoName = "avoiduninstall";
    private static AvoidUninstall instance;

    public static AvoidUninstall getInstance() {
        if (instance == null) {
            instance = new AvoidUninstall();
        }
        return instance;
    }

    public native void avoidUninstallApp(String packageName, String url);

    static {
//        System.loadLibrary(libSoName);
    }
}
