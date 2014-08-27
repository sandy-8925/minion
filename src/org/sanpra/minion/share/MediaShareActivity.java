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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import org.sanpra.minion.R;
import org.sanpra.minion.account.FacebookAccount;

import java.io.File;
import java.io.FileNotFoundException;

public final class MediaShareActivity extends Activity {

    private Uri imageData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_share_activity_layout);

        displayMediaList();
    }

    private void displayMediaList() {
        Intent launchingIntent = getIntent();
        if(launchingIntent.getAction().equals("android.intent.action.SEND")) {
            imageData = launchingIntent.getParcelableExtra(Intent.EXTRA_STREAM);
            ((TextView) findViewById(R.id.mediaListText)).setText(imageData.toString());
        }
    }

    public void uploadMedia(View view) {
        try {
            File imageFile = getFileForImageURI(imageData);
            com.facebook.Request.newUploadPhotoRequest(FacebookAccount.getSession(), imageFile, new com.facebook.Request.Callback() {
                public void onCompleted(com.facebook.Response response) {
                    android.util.Log.d("upload", "Media upload completed");
                }
            }).executeAsync();
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Unable to get file associated with URI", Toast.LENGTH_LONG).show();
        }
        finish();
    }

    private File getFileForImageURI(Uri imageUri) throws FileNotFoundException {
        android.database.Cursor cursor = getContentResolver().query(imageUri, new String[] {android.provider.MediaStore.Images.Media.DATA}, null, null, null);
        if(cursor.moveToFirst()) {
            String filePath = cursor.getString(cursor.getColumnIndex(android.provider.MediaStore.Images.Media.DATA));
            return new File(filePath);
        }
        else
            throw new FileNotFoundException();
    }
}