package com.lukeinnovationlab.friendofblackberryhub.firebase.invite;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.lukeinnovationlab.friendofblackberryhub.R;

/**
 * Helper class to handle with Firebase.
 */

public final class InviteHelper {

    private static final String TAG = "InviteHelper";

    private static InviteHelper sHelper = null;

    private InviteHelper() {
    }

    public static final InviteHelper getInstance() {
        if (sHelper == null) {
            synchronized (InviteHelper.class) {
                if (sHelper == null) {
                    sHelper = new InviteHelper();
                }
            }
        }
        return sHelper;
    }

    public void shareByInvite(final Context context) {
        Log.i(TAG, "Shared by Invite");
        Toast.makeText(context, R.string.result_shared, Toast.LENGTH_SHORT).show();
    }
}
