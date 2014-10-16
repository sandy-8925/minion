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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import org.sanpra.minion.R;
import org.sanpra.minion.account.FacebookAccount;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;

public final class MediaShareActivity extends FragmentActivity {

    private static final DialogFragment NO_ACTIVE_SESSION_DIALOG_FRAGMENT = new NoActiveSessionDialogFragment();
    private Collection<Uri> mediaUriList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_share_activity_layout);

        //TODO: Validate content Uri stored in Intent (can specify restrictions in intent filter in manifest)
        mediaUriList = extractUriListFromIntent(getIntent());
        displayMediaList();
    }

    /**
     * Display list of media (photos and videos) that will be uploaded
     */
    private void displayMediaList() {
        ((TextView) findViewById(R.id.mediaListText)).setText(buildUriDisplayString(mediaUriList));
    }

    /**
     * Checks intent action and extracts data from the intent
     * @param intent Intent from which data is to be extracted
     * @return Returns a collection of URIs extracted from the intent. If none are found, an empty list is returned.
     */
    private static Collection<Uri> extractUriListFromIntent(Intent intent) {
        Collection<Uri> uriList = new ArrayList<Uri>();

        if(intent.getAction().equals("android.intent.action.SEND")) {
            Uri mediaUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            uriList.add(mediaUri);
        }
        else if(intent.getAction().equals("android.intent.action.SEND_MULTIPLE")) {
            uriList = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        }

        return uriList;
    }

    /**
     * Accepts a collection of Uri objects, and builds a displayable string, with string representation of each Uri on it's own line
     * @param uriList Input collection of Uri objects
     * @return User displayable string, with string representation of each Uri on it's own line
     */
    private static String buildUriDisplayString(final Collection<Uri> uriList) {
        final StringBuilder uriListString = new StringBuilder();

        for(Uri uri : uriList) {
            uriListString.append(uri.toString()).append('\n');
        }

        return uriListString.toString();
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
            NO_ACTIVE_SESSION_DIALOG_FRAGMENT.show(getSupportFragmentManager(), "unusable_facebook_session_dialog");
        }
    }

    public void uploadMedia(View view) {
        final Context applicationContext = getApplicationContext();
        Collection<File> mediaFileList = getFilesForImageUriList(mediaUriList);
        for(File mediaFile : mediaFileList) {
            try {
                FacebookAccount.uploadImage(mediaFile, applicationContext);
            } catch (FileNotFoundException e) {
                //TODO: notify user that media file wasn't found
            }
        }
        finish();
    }

    /**
     * Attempts to determine corresponding files for a given list of content URIs by querying Android MediaStore
     * @param imageUriList Input list of URIs
     * @return A list of java.io.File objects corresponding to the URIs passed in imageUriList
     */
    private Collection<File> getFilesForImageUriList(Collection<Uri> imageUriList) {
        Collection<File> fileList = new ArrayList<File>();

        /*
            This method of determining file path for a given content URI, only works for images from the Android gallery app.
            Must figure out a generic way to determine file paths.
                        (OR)
            Accept only file URIs, and get the user to pick photos from  Android gallery, to upload photos from gallery
        */
        String idList = constructIdListString(imageUriList);
        android.database.Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{android.provider.MediaStore.Images.Media.DATA}, "_ID in " + idList, null, null);
        cursor.moveToFirst();
        do {
            String filePath = cursor.getString(cursor.getColumnIndex(android.provider.MediaStore.Images.Media.DATA));
            fileList.add(new File(filePath));
        } while(cursor.moveToNext());

        cursor.close();

        return  fileList;
    }

    /**
     * The purpose of this method is to create a string containing a list of the IDs of external media
     * Content URIs. This can be used to fetch information regarding these URIs from the Android OS.
     * This method accepts a list of URIs, and creates a String containing the last path segments
     * of the URIs.
     * The order of the segments in the String is not guaranteed to be the same as the order of URIs
     * in the list.
     * @param imageUriList Input list of URIs
     *
     * @return  A string containing a bracketed list of the last path segments of the input URIs
     * in the following format:
     *      ( URIOneLastPathSegment, URITwoLastPathSegment, URIThreeLastPathSegment, ..... )
     *
     *      Returns empty string when imageUriList is null or empty
     */
    private static String constructIdListString(final Collection<Uri> imageUriList) {
        if(imageUriList == null || imageUriList.isEmpty())
            return "";

        final StringBuilder idListBuilder = new StringBuilder();

        //add opening bracket
        idListBuilder.append("( ");
        for(Uri imageUri : imageUriList) {
            idListBuilder.append(imageUri.getLastPathSegment()).append(",");
        }
        //remove last , and then add closing bracket
        idListBuilder.deleteCharAt(idListBuilder.length()-1).append(" )");

        return idListBuilder.toString();
    }
}