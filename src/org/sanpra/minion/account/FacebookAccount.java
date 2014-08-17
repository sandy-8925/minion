package org.sanpra.minion.account;

import com.facebook.Session;


public final class FacebookAccount {
    private static Session session;

    private FacebookAccount() {}

    public static Session getSession() {
        return session;
    }

    static void setSession(Session newSession) {
        session = newSession;
    }
}
