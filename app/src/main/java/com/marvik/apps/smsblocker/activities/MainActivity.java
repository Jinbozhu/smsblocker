package com.marvik.apps.smsblocker.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.marvik.apis.core.activities.ActivityWrapper;
import com.marvik.apps.smsblocker.R;
import com.marvik.apps.smsblocker.constants.Constants;
import com.marvik.apps.smsblocker.fragments.blockedsms.BlockedSmsListFragment;
import com.marvik.apps.smsblocker.fragments.sendersms.SenderMessagesFragment;
import com.marvik.apps.smsblocker.fragments.smssenders.SmsSendersFragment;

public class MainActivity extends ActivityWrapper implements SmsSendersFragment.OnSmsSender, BlockedSmsListFragment.OnBlockedSms {

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();

        final String myPackageName = getPackageName();
        if (!Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {
            // App is not default.
            // Show the "not currently set as the default SMS app" interface
            View viewGroup = findViewById(R.id.not_default_app);
            viewGroup.setVisibility(View.VISIBLE);

            // Set up a button that allows the user to change the default SMS app
            Button button = (Button) findViewById(R.id.change_default_app);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent =
                            new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                    intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                            myPackageName);
                    startActivity(intent);
                }
            });
        } else {
            // App is the default.
            // Hide the "not currently set as the default SMS app" interface
            View viewGroup = findViewById(R.id.not_default_app);
            viewGroup.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {

    }

    @Override
    protected void onResumeActivity() {

    }

    @Override
    protected void onPauseActivity() {

    }

    @Override
    protected void onDestroyActivity() {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.Intents.INTENT_SELECT_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {
                String contactAddress = getUtils().getPersonContactPhoneNumber(data.getData());
                getUtils().getTransactionsManager().saveMessageSender(contactAddress, true, System.currentTimeMillis());
                attachFragment(new SmsSendersFragment(), true);
            }
        }

    }

    @Override
    public void onRequestPickContact() {
        startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI), Constants.Intents.INTENT_SELECT_CONTACT);
    }

    @Override
    public void setActivityTitle(String activityTitle) {
        setTitle(activityTitle);
    }

    @Override
    public void viewMessageSenders() {
        attachFragment(new SmsSendersFragment(), true);
    }

    @Override
    public void showSenderBlockedMessages(String senderAddress) {
        SenderMessagesFragment senderMessagesFragment = new SenderMessagesFragment();
        Bundle args = new Bundle();
        args.putString(Constants.Intents.EXTRA_MESSAGE_SENDER_ADDRESS, senderAddress);
        senderMessagesFragment.setArguments(args);
        attachFragment(senderMessagesFragment, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_blocked_messages, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_blocked_messages_view_message_senders) {

            if (getCurrentFragment() != new SmsSendersFragment()) {
                viewMessageSenders();
            }

        }
        return super.onOptionsItemSelected(item);
    }
}
