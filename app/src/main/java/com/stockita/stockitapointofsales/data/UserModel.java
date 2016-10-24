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

package com.stockita.stockitapointofsales.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;
import com.stockita.stockitapointofsales.utilities.Constants;

import java.util.HashMap;

/**
 * This is a POJO for the user model profile object
 */
public class UserModel implements Parcelable {

    /**
     * States
     */
    private String userName;
    private String uid;
    private String userEmail;
    private String photoUrl;

    private HashMap<String, Object> dateCreated;

    private boolean status;


    /**
     * Empty constructor
     */
    public UserModel(){

    }


    /**
     * Constructor
     * @param userName      Get the user name
     * @param uid           Get the user UID
     * @param userEmail     Get the user email address
     * @param photoUrl      Get the user photo
     */
    public UserModel(String userName, String uid, String userEmail, String photoUrl, HashMap<String, Object> dateCreated) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.uid = uid;
        this.photoUrl = photoUrl;
        this.dateCreated = dateCreated;

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public HashMap<String, Object> getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(HashMap<String, Object> dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Exclude
    public long getDateCreatedLong() {
        return (long)dateCreated.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }



    protected UserModel(Parcel in) {
        userName = in.readString();
        uid = in.readString();
        userEmail = in.readString();
        photoUrl = in.readString();
        dateCreated = (HashMap) in.readValue(HashMap.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(uid);
        dest.writeString(userEmail);
        dest.writeString(photoUrl);
        dest.writeValue(dateCreated);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UserModel> CREATOR = new Parcelable.Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };
}