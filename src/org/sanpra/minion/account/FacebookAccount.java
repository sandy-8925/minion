/**
 Copyright 2014 Sandeep Raghuraman <sandy.8925@gmail.com>

 This file is part of Minion.

 Minion is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Minion is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Minion.  If not, see <http://www.gnu.org/licenses/>.

 */

package org.sanpra.minion.account;

import com.facebook.Session;


public final class FacebookAccount {
    //TODO: Handle session loss, logout, denial of access etc.
    private static Session session;

    private FacebookAccount() {}

    public static Session getSession() {
        return session;
    }

    static void setSession(Session newSession) {
        session = newSession;
    }

    /**
     * Checks if Facebook session is usable(logged in to user account and can be used for API calls)
     * @return True, if Facebook session is usable, and false if not
     */
    public static boolean isSessionUsable() {
        if(session!=null && session.isOpened())
            return true;
        return false;
    }
}
