package com.neo.tech.whatsappPoc.common.audit;


public final class UserContext {

    private static final ThreadLocal<String> MOBILE = new ThreadLocal<>();

    private UserContext() {}

    public static void setMobile(String mobile) {
        MOBILE.set(mobile);
    }

    public static String getMobile() {
        return MOBILE.get();
    }

    public static void clear() {
        MOBILE.remove();
    }
}

