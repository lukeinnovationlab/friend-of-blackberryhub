package com.lukeinnovationlab.friendofblackberryhub;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.common.ConnectionResult;
import com.lukeinnovationlab.friendofblackberryhub.firebase.invite.InviteHelper;

public class MainActivity extends AppCompatActivity implements InviteHelper.InviteCallback {

    private static final String TAG = "MainActivity";

    private TextView mViewPromotionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPromotionCode = (TextView) findViewById(R.id.text_promotion_code);

        InviteHelper.getInstance().checkInvitation(this, this, true);
    }

    public void onInvite(View view) {
        Log.i(TAG, "onInvite");

        if (view.getId() == R.id.action_invite) {
            InviteHelper.getInstance().invite(this);
        }
    }

    public void onSharePromotion(View view) {
        Log.i(TAG, "onSharePromotion");

        if (view.getId() == R.id.action_share_promotion) {
            InviteHelper.getInstance().sharePromotion(this);
        }
    }

    /**
     * This is called after the InviteHelper.invite() or InviteHelper.sharePromotion() activity
     * completes.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == InviteHelper.REQUEST_INVITE_BLACKBERRY_HUB) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.i(TAG, "onActivityResult: sent invitation " + id);
                }

                Log.i(TAG, "Invited");
                Toast.makeText(this, R.string.result_invited_app, Toast.LENGTH_SHORT).show();
            } else {
                // Sending failed or it was canceled, show failure message to the user
                Log.w(TAG, "Failed to invite: " + resultCode);
                Toast.makeText(this, R.string.result_failed_to_invite_app, Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == InviteHelper.REQUEST_SHARE_BLACKBERRY_HUB_AD_FREE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.i(TAG, "onActivityResult: sent promotion " + id);
                }

                Log.i(TAG, "Sent promotion");
                Toast.makeText(this, R.string.result_send_promotion, Toast.LENGTH_SHORT).show();
            } else {
                // Sending failed or it was canceled, show failure message to the user
                Log.w(TAG, "Failed to send promotion: " + resultCode);
                Toast.makeText(this, R.string.result_failed_to_send_promotion, Toast
                        .LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "Failed to invite: " + connectionResult);
        Toast.makeText(this, R.string.result_failed_to_invite_app, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPromotionCodeReceived(String promotionCode) {
        if (mViewPromotionCode != null) {
            mViewPromotionCode.setText(getString(R.string.text_promotion_code_received) + promotionCode);
        }
    }
}
