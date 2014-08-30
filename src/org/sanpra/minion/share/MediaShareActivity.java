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

package org.sanpra.minion.share;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import com.facebook.Session;
import org.sanpra.minion.R;
import org.sanpra.minion.account.FacebookAccount;

import java.io.File;
import java.io.FileNotFoundException;

public final class MediaShareActivity extends FragmentActivity {

    private Uri mediaUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_share_activity_layout);

        setUpUserInterface();
        checkFacebookAccountStatus();
        displayMediaList();
    }

    private void setUpUserInterface() {
        findViewById(R.id.continueButton).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage(mediaUri);
                MediaShareActivity.this.finish();
            }
        });
    }

    /**
     * Display list of media (photos and videos) that will be uploaded
     */
    private void displayMediaList() {
        Intent launchingIntent = getIntent();
        if(launchingIntent.getAction().equals("android.intent.action.SEND")) {
            mediaUri = launchingIntent.getParcelableExtra(Intent.EXTRA_STREAM);
            ((TextView) findViewById(R.id.mediaListText)).setText(mediaUri.toString());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkFacebookAccountStatus();
    }

    private void checkFacebookAccountStatus() {
        //check if Facebook Session is still valid - if not, display dialog and finish activity
        Session fbSession = FacebookAccount.getSession();
        if(fbSession==null || !fbSession.isOpened()) {
            NoActiveSessionDialogFragment noActiveSessionDialogFragment = new NoActiveSessionDialogFragment();
            noActiveSessionDialogFragment.show(getSupportFragmentManager(), "invalid_facebook_session_dialog");
        }
    }

    //TODO: Move this method to class FacebookAccount
    private void uploadImage(Uri mediaUri) {
        try {
            File imageFile = getFileForImageURI(mediaUri);
            com.facebook.Request.newUploadPhotoRequest(FacebookAccount.getSession(), imageFile, new com.facebook.Request.Callback() {
                public void onCompleted(com.facebook.Response response) {
                    //TODO: Check response, and notify user if upload was successful or unsuccessful (notifications?)
                    android.util.Log.d("upload", "Media upload completed");
                }
            }).executeAsync();
        } catch (FileNotFoundException e) {
            //TODO: Notify user that photo/video could not be found
        }
    }

    /**
     * Attempts to locate corresponding file for a given content Uri by querying Android MediaStore
     * @param mediaUri Input Uri
     * @return A java.io.File object corresponding to mediaUri
     * @throws FileNotFoundException
     */
    private File getFileForImageURI(Uri mediaUri) throws FileNotFoundException {
        android.database.Cursor cursor = getContentResolver().query(mediaUri, new String[] {android.provider.MediaStore.Images.Media.DATA}, null, null, null);
        if(cursor.moveToFirst()) {
            String filePath = cursor.getString(cursor.getColumnIndex(android.provider.MediaStore.Images.Media.DATA));
            return new File(filePath);
        }
        else
            throw new FileNotFoundException();
    }
}