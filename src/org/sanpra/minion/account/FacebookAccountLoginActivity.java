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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.UserSettingsFragment;
import org.sanpra.minion.R;

public final class FacebookAccountLoginActivity extends FragmentActivity
{

    private Session.StatusCallback fbSessionStatusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            if(session.isOpened()) {
                FacebookAccount.setSession(session);
            }
        }
    };

    private UserSettingsFragment fbLoginFragment;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fb_account_login_layout);

        fbLoginFragment = (UserSettingsFragment) getSupportFragmentManager().findFragmentById(R.id.fbLoginFragment);
        fbLoginFragment.setPublishPermissions("publish_actions");
        fbLoginFragment.setSessionStatusCallback(fbSessionStatusCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fbLoginFragment.onActivityResult(requestCode, resultCode, data);
    }
}
