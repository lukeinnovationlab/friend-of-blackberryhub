package com.lukeinnovationlab.friendofblackberryhub.firebase.invite;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.lukeinnovationlab.friendofblackberryhub.R;

/**
 * Helper class to handle with Firebase.
 */

public final class InviteHelper {

    private static final String TAG = "InviteHelper";

    public static final int REQUEST_INVITE_BLACKBERRY_HUB = 1;
    public static final int REQUEST_SHARE_BLACKBERRY_HUB_AD_FREE = 2;

    private static InviteHelper sHelper = null;

    public interface InviteCallback extends GoogleApiClient.OnConnectionFailedListener {
        void onPromotionCodeReceived(final String promotionCode);
    }

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

    public void invite(final Context context) throws RuntimeException {
        Log.i(TAG, "To invite");

        Intent intent = new AppInviteInvitation.IntentBuilder(context.getString(R.string
                .invitation_title))
                .setMessage(context.getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(context.getString(R.string
                        .invitation_deep_link_blackberry_hub)))
                .setEmailHtmlContent(context.getString(R.string.invitation_email_html_content))
                .setEmailSubject(context.getString(R.string.invitation_email_subject))
                // .setAccount(new Account("", GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE)) // Must be a
                // Google account
                .build();

        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, REQUEST_INVITE_BLACKBERRY_HUB);
        } else {
            throw new RuntimeException("Failed to invite: invalid activity.");
        }
    }

    public void sharePromotion(final Context context) {
        String promotionCode = generatePromotionCode();

        Log.i(TAG, "To share promotion code: " + promotionCode);

        Intent intent = new AppInviteInvitation.IntentBuilder(context.getString(R.string
                .promotion_title))
                .setMessage(context.getString(R.string.promotion_message))
                .setDeepLink(Uri.parse(context.getString(R.string
                        .promotion_deep_link_promotion_code) + promotionCode))
                .setEmailHtmlContent(context.getString(R.string.promotion_email_html_content))
                .setEmailSubject(context.getString(R.string.promotion_email_subject))
                // .setAccount(new Account("", GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE)) // Must be a
                // Google account
                .build();

        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent,
                    REQUEST_SHARE_BLACKBERRY_HUB_AD_FREE);
        } else {
            throw new RuntimeException("Failed to share promotion: invalid activity.");
        }
    }

    private String generatePromotionCode() {
        StringBuilder sb = new StringBuilder();
        sb.append("AD_FREE_");

        String time = String.valueOf(System.currentTimeMillis());
        sb.append(time.substring(time.length() - 5));
        return sb.toString();
    }

    public void checkInvitation(final Context context, final InviteCallback inviteCallback, boolean
            autoLaunchDeepLink) {
        // Create an auto-managed GoogleApiClient with access to App Invites.
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(AppInvite.API)
                .enableAutoManage((FragmentActivity) context, inviteCallback)
                .build();

        // Check for App Invite invitations and launch deep-link activity if possible.
        // Requires that an Activity is registered in AndroidManifest.xml to handle
        // deep-link URLs.
        AppInvite.AppInviteApi.getInvitation(googleApiClient, (Activity) context,
                autoLaunchDeepLink)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(@NonNull AppInviteInvitationResult result) {
                                if (!result.getStatus().isSuccess()) {
                                    Log.w(TAG, "getInvitation:onResult:" + result.getStatus());
                                    return;
                                }

                                // Extract information from the intent
                                Intent intent = result.getInvitationIntent();
                                String deepLink = AppInviteReferral.getDeepLink(intent);
                                String invitationId = AppInviteReferral.getInvitationId(intent);

                                Log.i(TAG, "getInvitation:onResult:" + result.getStatus() + ", "
                                        + "intent: " + intent + ", deepLink: " + deepLink + ", " +
                                        "invitationId: " + invitationId);

                                // Because autoLaunchDeepLink = true we don't have to do
                                // anything
                                // here, but we could set that to false and manually choose
                                // an Activity to launch to handle the deep link here.
                                // ...

                                // Only when opening from the invitation email
                                if (Intent.ACTION_MAIN.equals(intent.getAction()) && deepLink !=
                                        null) {
                                    Uri deepLinkUri = Uri.parse(deepLink);
                                    if (deepLink.startsWith(context.getString(R.string
                                            .promotion_deep_link_promotion_code))) {
                                        String promotionCode = deepLinkUri.getLastPathSegment();
                                        inviteCallback.onPromotionCodeReceived(promotionCode);

                                        Log.i(TAG, "getInvitation:promotionCode: " + promotionCode);

                                    } else {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                                deepLinkUri);
                                        context.startActivity(browserIntent);

                                        Log.i(TAG, "getInvitation:deepLink started");
                                    }
                                }
                            }
                        });
    }
}
