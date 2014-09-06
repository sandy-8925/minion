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

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Holds all Facebook account related methods and data
 */
public final class FacebookAccount {
    //TODO: Handle session loss, logout, denial of access etc.
    private static Session session;

    private FacebookAccount() {}

    static void setSession(Session newSession) {
        session = newSession;
    }

    /**
     * Checks if Facebook session is usable(logged in to user account and can be used for API calls)
     * @return True if Facebook session is usable, and false if not
     */
    public static boolean isSessionUsable() {
        if(session!=null && session.isOpened())
            return true;
        return false;
    }

    /**
     * Given an image file, attempts to upload it to Facebook user's account
     * @param imageFile Image file to be uploaded
     */
    public static void uploadImage(File imageFile) {
        try {
            com.facebook.Request.newUploadPhotoRequest(session, imageFile, new com.facebook.Request.Callback() {
                public void onCompleted(com.facebook.Response response) {
                    //TODO: Check response, and notify user if upload was successful or unsuccessful (notifications?)
                    android.util.Log.d("upload", "Media upload completed");
                }
            }).executeAsync();
        } catch (FileNotFoundException e) {
            //TODO: Notify user that photo/video could not be found
        }
    }
}
