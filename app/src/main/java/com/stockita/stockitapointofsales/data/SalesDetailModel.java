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
 * This is the model for sales detail, which contains the sales of each item
 * only one item
 */
public class SalesDetailModel implements Parcelable {

    private String itemNumber;
    private String itemDesc;
    private String itemUnit;
    private String itemPrice;
    private String itemQuantity;
    private String itemDiscount;
    private String itemDiscountAmout;
    private String itemAmount;


    /**
     * Empty constructor
     */
    public SalesDetailModel() {}


    /**
     * Constructor
     * @param itemNumber        item master number
     * @param itemDesc          item master desc
     * @param itemUnit          item master unit of measure
     * @param itemPrice         item master price
     * @param itemQuantity      sales quantity
     * @param itemDiscount      sales discount
     * @param itemAmount        sales amount for this item
     */
    public SalesDetailModel(String itemNumber,
                            String itemDesc,
                            String itemUnit,
                            String itemPrice,
                            String itemQuantity,
                            String itemDiscount,
                            String itemDiscountAmount,
                            String itemAmount) {

        this.itemNumber = itemNumber;
        this.itemDesc = itemDesc;
        this.itemUnit = itemUnit;
        this.itemPrice = itemPrice;
        this.itemQuantity = itemQuantity;
        this.itemDiscount = itemDiscount;
        this.itemDiscountAmout = itemDiscountAmount;
        this.itemAmount = itemAmount;

    }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public String getItemUnit() {
        return itemUnit;
    }

    public void setItemUnit(String itemUnit) {
        this.itemUnit = itemUnit;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(String itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getItemDiscount() {
        return itemDiscount;
    }

    public void setItemDiscount(String itemDiscount) {
        this.itemDiscount = itemDiscount;
    }

    public String getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(String itemAmount) {
        this.itemAmount = itemAmount;
    }

    public String getItemDiscountAmout() {
        return itemDiscountAmout;
    }

    public void setItemDiscountAmout(String itemDiscountAmout) {
        this.itemDiscountAmout = itemDiscountAmout;
    }

    protected SalesDetailModel(Parcel in) {
        itemNumber = in.readString();
        itemDesc = in.readString();
        itemUnit = in.readString();
        itemPrice = in.readString();
        itemQuantity = in.readString();
        itemDiscount = in.readString();
        itemDiscountAmout = in.readString();
        itemAmount = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemNumber);
        dest.writeString(itemDesc);
        dest.writeString(itemUnit);
        dest.writeString(itemPrice);
        dest.writeString(itemQuantity);
        dest.writeString(itemDiscount);
        dest.writeString(itemDiscountAmout);
        dest.writeString(itemAmount);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SalesDetailModel> CREATOR = new Parcelable.Creator<SalesDetailModel>() {
        @Override
        public SalesDetailModel createFromParcel(Parcel in) {
            return new SalesDetailModel(in);
        }

        @Override
        public SalesDetailModel[] newArray(int size) {
            return new SalesDetailModel[size];
        }
    };
}