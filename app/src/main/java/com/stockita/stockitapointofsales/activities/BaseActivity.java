/**
 * MIT License
 *
 * Copyright (c) 2016 Hishmad Abubakar Al-Amudi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.stockita.stockitapointofsales.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.stockita.stockitapointofsales.login_ui.SignInUI;
import com.stockita.stockitapointofsales.utilities.Constants;
import com.stockita.stockitapointofsales.utilities.Utility;


/**
 * This is the base activity, it is the parent to all activities in this app
 */
public class BaseActivity extends AppCompatActivity  {

    /**
     * Tag
     */
    private static final String TAG_LOG = BaseActivity.class.getSimpleName();

    /**
     * Firebase user presences {@link DatabaseReference}
     * to check the connection status
     */
    protected DatabaseReference mUserPresenceRef;

    /**
     * Firebase listener related to user presences
     */
    protected ValueEventListener mUserPresenceListener;

    /**
     * User encoded email, means the users login
     * email that encoded to meet Firebase requirement
     * if to be used as key.
     */
    protected String mUserEncodedEmail;

    /**
     * Users login name
     */
    protected String mUserName;

    /**
     * Users UID obtained from OAuth Firebase
     */
    protected String mUserUid;

    /**
     * Users photo url
     */
    protected String mPhotoUrl;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Get the user data from sharedPreferences, pass them to the member variables for later use
        getUserData(this);

    }


    @Override
    protected void onStart() {
        super.onStart();

        // If not null then check the user presence
        if (mUserUid != null) {
            // Check & listen for the Users presence
            presenceFunction();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

        // Remove the listeners
        if (mUserPresenceRef != null && mUserPresenceListener != null) {
            mUserPresenceRef.removeEventListener(mUserPresenceListener);
        }
    }


    /**
     * This method to sign out, from both the email sign or google sign
     */
    protected void signOut() {

        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now sign out

                        // Get back to the sign in page
                        startActivity(new Intent(getBaseContext(), SignInUI.class));
                        finish();
                    }
                });
    }


    /**
     * This helper method is to check and listen for the user connection, and also
     * log time stamp when the user disconnect.
     */
    private void presenceFunction() {

        // Initialize the .info/connected
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mUserPresenceRef = database.getReference(".info/connected");

        // Get an instance reference to the location of the user's /usersLog/<uid>/lastOnline
        final DatabaseReference lastOnlineRef = database.getReference()
                .child(Constants.FIREBASE_USER_LOG_LOCATION)
                .child(mUserUid)
                .child(Constants.FIREBASE_PROPERTY_LAST_ONLINE);


        // Initialize the listeners
        mUserPresenceListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Get the boolean value of the connection status
                boolean connected = dataSnapshot.getValue(Boolean.class);

                if (connected) {

                    // when I disconnect, update the last time I was seen online
                    lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Nothing
            }
        };

        // Add the listeners instance to the database database instance
        mUserPresenceRef.addValueEventListener(mUserPresenceListener);

    }


    /**
     * Retrieve users data from sharedPreferences, store them in variables
     * for later use
     */
    private void getUserData(Context context) {

        // Get the users data in String[] from the SharedPreferences
        String[] usersData = Utility.getTheCurrentUserLoginState(context);

        if (usersData != null) {

            // Load the data into the member variables for later use.
            mUserName = usersData[0];
            mUserUid = usersData[1];
            mUserEncodedEmail = usersData[2];
            mPhotoUrl = usersData[3];
        }
    }


    /**
     * Check the version
     *
     * @return true if > Lollipop
     */
    protected boolean checkVersionMarsh() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1;
    }

}
