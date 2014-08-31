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
import org.sanpra.minion.R;
import org.sanpra.minion.account.FacebookAccount;

import java.io.File;
import java.io.FileNotFoundException;

public final class MediaShareActivity extends FragmentActivity {

    private static final NoActiveSessionDialogFragment NO_ACTIVE_SESSION_DIALOG_FRAGMENT = new NoActiveSessionDialogFragment();
    private Uri mediaUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_share_activity_layout);

        //TODO: Validate content Uri stored in Intent
        setUpUserInterface();
        displayMediaList();
    }

    private void setUpUserInterface() {
        findViewById(R.id.continueButton).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    FacebookAccount.uploadImage(getFileForImageURI(mediaUri));
                } catch (FileNotFoundException e) {
                    //TODO: notify user that media file wasn't found
                }
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
        /*
            Imagine following: MediaShareActivity is started, user switches to another app, and then comes back
            Meanwhile, user may have logged out of Facebook account in login activity, or session may have expired
            Either way, we need to ensure that Facebook session is still valid when onStart is invoked
         */
        checkFacebookAccountStatus();
    }

    /**
     * Checks if Facebook Session is still valid - if not, displays dialog and finishes activity
     */
    private void checkFacebookAccountStatus() {
        if(!FacebookAccount.isSessionUsable()) {
            NO_ACTIVE_SESSION_DIALOG_FRAGMENT.show(getSupportFragmentManager(), "invalid_facebook_session_dialog");
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