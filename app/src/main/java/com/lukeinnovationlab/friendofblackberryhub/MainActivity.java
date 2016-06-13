package com.lukeinnovationlab.friendofblackberryhub;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.lukeinnovationlab.friendofblackberryhub.firebase.invite.InviteHelper;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onShare(View view) {
        Log.i(TAG, "onShare");

        if (view.getId() == R.id.action_share) {
            InviteHelper.getInstance().shareByInvite(this);
        }
    }
}
