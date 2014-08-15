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

package org.sanpra.minion;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

public final class FacebookAccountLoginActivity extends Activity
{
    //TODO: Need to handle screen rotation

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
        }
    };

    private UiLifecycleHelper uiLifecycleHelper;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        uiLifecycleHelper = new UiLifecycleHelper(this, callback);
        uiLifecycleHelper.onCreate(savedInstanceState);

        setContentView(R.layout.main);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiLifecycleHelper.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        uiLifecycleHelper.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiLifecycleHelper.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiLifecycleHelper.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiLifecycleHelper.onActivityResult(requestCode, resultCode, data);
    }
}
