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

package com.stockita.stockitapointofsales.login_ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.BuildConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stockita.stockitapointofsales.R;
import com.stockita.stockitapointofsales.activities.MainActivity;
import com.stockita.stockitapointofsales.utilities.Constants;
import com.stockita.stockitapointofsales.utilities.Utility;


/**
 * Using firebaseUI for login page
 */
public class SignInUI extends AppCompatActivity {

    /**
     * TAG LOG
     */
    private static final String TAG_LOG = SignInUI.class.getSimpleName();

    /**
     * Request code for FirebaseUI sign in result
     */
    private static final int RC_SIGN_IN = 9001;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check current user is already signed in or not
        FirebaseAuth auth = FirebaseAuth.getInstance();
        boolean isSignIn = auth.getCurrentUser() != null;


        if (!isSignIn) {

            // FirebaseUI line of code
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                            .setProviders(AuthUI.EMAIL_PROVIDER, AuthUI.GOOGLE_PROVIDER, AuthUI.FACEBOOK_PROVIDER)
                            .setTheme(R.style.AppTheme)
                            .build(), RC_SIGN_IN);


        }

        if (isSignIn) {
            // Go directly to the main activity.
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case RC_SIGN_IN:

                if (resultCode == RESULT_OK) {
                    // user is signed in!

                    /**
                     * Get a reference for the current signed in user, and get the
                     * email and the UID
                     */
                    FirebaseUser userProfile = FirebaseAuth.getInstance().getCurrentUser();

                    if (userProfile != null) {


                        // Get the photo Url
                        Uri photoUri = userProfile.getPhotoUrl();
                        final String photoUrl = photoUri != null ? photoUri.getPath() : null;

                        // Get the user name and store them in SharedPreferences for later use.
                        final String profileName = userProfile.getDisplayName();

                        // Get the user email and store them in SharedPreferences for later use.
                        final String profileEmail = userProfile.getEmail();

                        // Get the user UID and sore them in SharedPreferences for later use.
                        final String profileUid = userProfile.getUid();

                        // Encoded the email that the user just signed in with
                        String encodedUserEmail = Utility.encodeEmail(profileEmail);

                        // Register the current sign in data in SharedPreferences for later use
                        Utility.registerTheCurrentLogin(getBaseContext(), profileName, profileUid, encodedUserEmail, photoUrl);

                        // Initialize the Firebase reference to the /users/<userUid> location
                        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                                .child(Constants.FIREBASE_USER_LOCATION)
                                .child(profileUid);

                        // Listener for a single value event, only one time this listener will be triggered
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                // Check if hasChildren means already exists
                                if (!dataSnapshot.hasChildren()) {

                                    /**
                                     * Create this new user into /users/ node in Firebase
                                     */
                                    Utility.createUser(getBaseContext(), profileName, profileUid, profileEmail, photoUrl);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                // Get the message in to the logcat
                                Log.e("check if users exist", databaseError.getMessage());
                            }
                        });
                    }

                    // When signed in then start the main activity
                    startActivity(new Intent(this, MainActivity.class));
                    finish();

                } else {
                    // user is not signed in. Maybe just wait for the user to press
                    // "sign in" again, or show a message
                    Log.e(TAG_LOG, "not failed to sign in");
                }
        }
    }
}
