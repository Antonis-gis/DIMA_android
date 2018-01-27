package com.example.elena.ourandroidapp.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.elena.ourandroidapp.model.Contact;
import com.example.elena.ourandroidapp.model.Poll;
import com.example.elena.ourandroidapp.services.DatabaseService;
import com.example.elena.ourandroidapp.services.GlobalContainer;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;
import java.util.HashMap;

/*
        <activity android:name=".LoginActivity"></activity>
        <activity android:name=".LaunchActivity"
            android:theme="@android:style/Theme.NoDisplay"></activity>
 */
public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Here", "Here2");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // already signed in
            String mPhoneNumber = auth.getCurrentUser().getPhoneNumber();
            DatabaseService mTokenService = DatabaseService.getInstance();
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            mTokenService.writeTokenData(mPhoneNumber,refreshedToken);
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {
            Log.i("There", "There2");
            // not signed in

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(
                                    Arrays.asList(
                                            new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()
                                    ))
                            .build(),
                    RC_SIGN_IN);

        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            // Successfully signed in
            if (resultCode == ResultCodes.OK) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String mPhoneNumber = auth.getCurrentUser().getPhoneNumber();
                DatabaseService mTokenService = DatabaseService.getInstance();
                String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                mTokenService.writeTokenData(mPhoneNumber,refreshedToken);
//since it is the first time the newly registered user comes into app we have to search for the phones in this contact list
// which are registered as our users
// we put these contacts into GlobalContainer contacts HashMap
                DatabaseService mCheckService = DatabaseService.getInstance();
                HashMap<String, Contact> contactsToCheck=new HashMap<>();
                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
                while (phones.moveToNext())
                {
                    String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Contact c = new Contact(name, phoneNumber);
                    Contact value = contactsToCheck.get(name);
                    if (value != null) {
                        System.err.println(value.getName());
                    }

                    contactsToCheck.put(name, c);

                }
                DatabaseReference rootRef = mCheckService.getReference("users");
                DatabaseService.ContactsCallback callback = new DatabaseService.ContactsCallback() {//we need somehow find right poll and update its view
                    @Override
                    public void onLoad() {

                        }

                    @Override
                    public void onFailure() {

                    }
                };
                mCheckService.getTheOnesInDatabase(contactsToCheck, new DatabaseService.DefaultContactsCallback());
                phones.close();

                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                finish();
                return;

                //startActivity(new Intent(LoginActivity.this,MainActivity.class));
                //finish();
                //return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Log.e("Login","Login canceled by User");
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Log.e("Login","No Internet Connection");
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Log.e("Login","Unknown Error");
                    return;
                }
            }
            Log.e("Login","Unknown sign in response");
        }
    }
}
