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

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.facebook.Response;
import com.facebook.Session;

import org.sanpra.minion.R;
import org.sanpra.minion.utils.NotificationUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;

//TODO: Investigate if this class' functionality should be moved to an Android Service
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
     * @param context Application context
     */
    public static void uploadImage(File imageFile, final Context context) throws FileNotFoundException {
            com.facebook.Request.newUploadPhotoRequest(session, imageFile, new UploadPhotoRequestCallback(context)).executeAsync();
    }

    /**
     * Given a list of image files, attempts to upload them to user's Facebook account
     * @param imageFileList Collection of image files to be uploaded
     * @param applicationContext Application context
     */
    public static void uploadImageCollection(final Collection<File> imageFileList, final Context applicationContext) {
        for(File imageFile : imageFileList) {
            try {
                uploadImage(imageFile, applicationContext);
            } catch (FileNotFoundException exception) {
                //TODO: Handle these exceptions
            }
        }
    }

    /**
     * Contains callback methods for handling result of single photo upload
     */
    private final static class UploadPhotoRequestCallback implements com.facebook.Request.Callback {

        private Notification uploadErrorNotification;
        private Notification uploadSuccessNotification;
        private NotificationManager notificationManager;

        UploadPhotoRequestCallback(final Context applicationContext) {
            uploadErrorNotification = new NotificationCompat.Builder(applicationContext)
                                    .setContentTitle("Unable to upload photo")
                                    .setContentText("Facebook server returned error in response")
                                    .setSmallIcon(R.drawable.ic_launcher)
                                    .setAutoCancel(true)
                                    .setTicker("Unable to upload photo to Facebook")
                                    .setDefaults(Notification.DEFAULT_SOUND)
                                    .build();

            uploadSuccessNotification = new NotificationCompat.Builder(applicationContext)
                                        .setContentTitle("Photo uploaded successfully")
                                        .setContentText("")
                                        .setSmallIcon(R.drawable.ic_launcher)
                                        .setAutoCancel(true)
                                        .setDefaults(Notification.DEFAULT_SOUND)
                                        .build();

            notificationManager = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        @Override
        public void onCompleted(Response response) {
                if(response.getError() != null) {
                    notificationManager.notify(NotificationUtils.UPLOAD_ERROR_NOTIFICATION_ID, uploadErrorNotification);
                    android.util.Log.d("upload", "Media upload failed");
                }
                else {
                    notificationManager.notify(NotificationUtils.UPLOAD_SUCCESS_NOTIFICATION_ID, uploadSuccessNotification);
                    android.util.Log.d("upload", "Media upload completed successfully");
                }
        }
    }

}
