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

package com.stockita.stockitapointofsales.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.stockita.stockitapointofsales.data.UserModel;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class for all static methods that can be reuse
 * as a single function
 */
public final class Utility {


    /**
     * This method is used for checking valid email id format.
     *
     * @param email The user input
     * @return boolean      True for valid false for invalid
     */
    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }


    /**
     * Encode user email to use it as a Firebase key (Firebase dows not allow) "." in the key name)
     * Encoded email is also used as "userEmail", list and item "owner" value
     */
    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }


    /**
     * Decode user email from firebase format to normal format.
     */
    public static String decodeEmail(String userEncodedEmail) {
        return userEncodedEmail.replace(",", ".");
    }


    /**
     * We can use this method to store any String into SharedPreferences.
     *
     * @param context Activity context
     * @param key     {@link SharedPreferences} Key
     * @param value   The value of String type that will be sore
     */
    public static void setAnyString(Context context, String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.COMMON_DATA_SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        prefs.edit().putString(key, value).apply();
    }


    /**
     * We can use this method to store any boolean into SharePreferences.
     *
     * @param context Activity
     * @param key     {@link SharedPreferences} key
     * @param value   The value which is true/false
     */
    public static void setAnyBoolean(Context context, String key, boolean value) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.COMMON_DATA_SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        prefs.edit().putBoolean(key, value).apply();
    }


    /**
     * We can use this method to store any int into SharePreferences.
     *
     * @param context The Activity context
     * @param key     Key to store
     * @param value   value which in type int
     */
    public static void setAnyInt(Context context, String key, int value) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.COMMON_DATA_SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        prefs.edit().putInt(key, value).apply();
    }


    /**
     * We can use this method to restore any String from SharedPReferences.
     *
     * @param context       Activity context
     * @param key           {@link SharedPreferences} Key
     * @param defaultString If no value then use this default value
     */
    public static String getAnyString(Context context, String key, String defaultString) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.COMMON_DATA_SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        return prefs.getString(key, defaultString);
    }


    /**
     * We can use this method to restore any Boolean from SharedPreferences.
     *
     * @param context        Activity context
     * @param key            {@link SharedPreferences} key
     * @param defaultBoolean False or True
     * @return The value stored
     */
    public static boolean getAnyBoolean(Context context, String key, boolean defaultBoolean) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.COMMON_DATA_SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        return prefs.getBoolean(key, defaultBoolean);
    }


    /**
     * We can use this method to restore any int from SharedPreferences.
     *
     * @param context    The Activity context
     * @param key        The key to restore data
     * @param defaultInt The default value
     * @return The value stored
     */
    public static int getAnyInt(Context context, String key, int defaultInt) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.COMMON_DATA_SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        return prefs.getInt(key, defaultInt);
    }


    /**
     * Create new user profile into Firebase location users
     *
     * @param userName  The user name
     * @param userUID   The user UID from Firebase Auth
     * @param userEmail The user encoded email
     */
    public static void createUser(Context context, String userName, String userUID, String userEmail, String userPhoto) {

        // Encoded the email that the user just signed in with
        String encodedUserEmail = Utility.encodeEmail(userEmail);

        // This is the Firebase server value time stamp HashMap
        HashMap<String, Object> timestampCreated = new HashMap<>();

        // Pack the ServerValue.TIMESTAMP into a HashMap
        timestampCreated.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

        /**
         * Pass those data into java object
         */
        UserModel userModel = new UserModel(userName, userUID, userEmail, userPhoto, timestampCreated);

        /**
         * Initialize the DatabaseReference
         */
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        /**
         * Get reference into the users location
         */
        DatabaseReference usersLocation = databaseReference.child(Constants.FIREBASE_USER_LOCATION);


        /**
         * Add the user object into the users location in Firebase database
         */
        usersLocation.child(userUID).setValue(userModel);

    }


    /**
     * Store the sign in data into SharedPreferences
     * @param context       Activity context
     * @param userName      The user's sign in name
     * @param userUID       The user's firebase UID
     * @param userEmail     The user's sign in email encoded
     */
    public static void registerTheCurrentLogin(Context context, String userName, String userUID, String userEmail, String photoUrl) {

        setAnyString(context, Constants.KEY_USERS_NAME, userName);
        setAnyString(context, Constants.KEY_USERS_UID, userUID);
        setAnyString(context, Constants.KEY_USERS_EMAIL, userEmail);
        setAnyString(context, Constants.KEY_USER_PHOTO, photoUrl);


    }


    /**
     * Return String[] for user's sign in data
     * @param context       Activity context
     * @return              String array of user's sign in data
     */
    public static String[] getTheCurrentUserLoginState(Context context) {
        String[] dataSignIn = new String[4];

        dataSignIn[0] = getAnyString(context, Constants.KEY_USERS_NAME, null);
        dataSignIn[1] = getAnyString(context, Constants.KEY_USERS_UID, null);
        dataSignIn[2] = getAnyString(context, Constants.KEY_USERS_EMAIL, null);
        dataSignIn[3] = getAnyString(context, Constants.KEY_USER_PHOTO, null);
        return dataSignIn;
    }


    /**
     * This method to will set the color of the statusbar to
     */
    public static void changeTheStatusbarColor(Activity activity, int colorResource) {
        /**
         * Change the color of the status bar
         */
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= 23) {
            window.setStatusBarColor(ContextCompat.getColor(activity, colorResource));
        } else {
            window.setStatusBarColor(activity.getResources().getColor(colorResource));
        }
    }


    /**
     * Helper method to convert from URI to Real file path, as String.
     * @param uri      File URI
     * @param activity Activity context
     */
    public static String convertMediaUriToPath(Uri uri, Context activity) throws Exception{
        String[] proj = {MediaStore.Images.Media.DATA};
        String path = null;
        Cursor cursor = activity.getContentResolver().query(uri, proj, null, null, null);
        int column_index = 0;
        if (cursor != null) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index);
            cursor.close();

        }

        return path;
    }
}
