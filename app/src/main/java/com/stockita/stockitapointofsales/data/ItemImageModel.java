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

/**
 * This is the model class for item images
 */
public class ItemImageModel implements Parcelable {

    //State
    private String imageUrl;
    private String itemPushKey;


    /**
     * Empty constructor
     */
    public ItemImageModel() {

    }

    /**
     * Constructor
     * @param imageUrl      The url for this image or the file name
     * @param itemPushKey   The push key of an item master created by Server
     */
    public ItemImageModel(String imageUrl, String itemPushKey){

        this.imageUrl = imageUrl;
        this.itemPushKey = itemPushKey;

    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getItemPushKey() {
        return itemPushKey;
    }

    public void setItemPushKey(String itemPushKey) {
        this.itemPushKey = itemPushKey;
    }

    protected ItemImageModel(Parcel in) {
        imageUrl = in.readString();
        itemPushKey = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageUrl);
        dest.writeString(itemPushKey);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ItemImageModel> CREATOR = new Parcelable.Creator<ItemImageModel>() {
        @Override
        public ItemImageModel createFromParcel(Parcel in) {
            return new ItemImageModel(in);
        }

        @Override
        public ItemImageModel[] newArray(int size) {
            return new ItemImageModel[size];
        }
    };
}
