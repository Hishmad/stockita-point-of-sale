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

/**
 * This class is where we store the constants
 */
public final class Constants {

    // Firebase locations
    public static final String FIREBASE_USER_LOCATION = "users";
    public static final String FIREBASE_USER_LOG_LOCATION = "usersLog";
    public static final String FIREBASE_ITEM_MASTER_LOCATION = "itemMaster";
    public static final String FIREBASE_ITEM_MASTER_IMAGE_LOCATION = "itemImage";
    public static final String FIREBASE_TRIGGER_LOCATION = "trigger";
    public static final String FIREBASE_SALES_DETAIL_PENDING_LOCATION = "salesDetailPending";
    public static final String FIREBASE_PAID_SALES_HEADER_LOCATION = "paidSalesHeader";
    public static final String FIREBASE_PAID_SALES_DETAIL_LOCATION = "paidSalesDetail";
    public static final String FIREBASE_PAYMENT_LOCATION = "payment";
    public static final String FIREBASE_OPEN_SALES_HEADER_LOCATION = "openSalesHeader";
    public static final String FIREBASE_OPEN_SALES_DETAIL_LOCATION = "openSalesDetail";
    public static final String FIREBASE_ARCHIVE_SALES_HEADER_LOCATION = "archiveSalesHeader";
    public static final String FIREBASE_ARCHIVE_SALES_DETAIL_LOCATION = "archiveSalesDetail";
    public static final String FIREBASE_ARCHIVE_PAYMENT_LOCATION = "archivePayment";
    public static final String FIREBASE_YEAR_MONTH_LOCATION = "yearMonth";


    // Firebase property
    public static final String FIREBASE_PROPERTY_TIMESTAMP = "timestamp";
    public static final String FIREBASE_PROPERTY_USERS_STATUS = "status";
    public static final String FIREBASE_PROPERTY_CONNECTION_STATUS = "connectionsStatus";
    public static final String FIREBASE_PROPERTY_LAST_ONLINE = "lastOnline";
    public static final String FIREBASE_PROPERTY_IMAGE_URL = "imageUrl";
    public static final String FIREBASE_PROPERTY_TOTAL_AMOUNT = "totalAmount";


    // Local storage SharedPreferences common data
    public static final String COMMON_DATA_SHARED_PREFERENCES = "COMMON_DATA_SHARED_PREFERENCES";

    // Key local storage SharedPreferences data
    public static final String KEY_USERS_EMAIL = "KEY_USERS_EMAIL";
    public static final String KEY_USERS_NAME = "KEY_USERS_NAME";
    public static final String KEY_USERS_UID = "KEY_USERS_UID";
    public static final String KEY_USER_PHOTO = "KEY_USER_PHOTO";


    // ItemMasterAddEditCallbacks Interface request code
    public static final int REQUEST_CODE_DIALOG_ONE = 1;
    public static final int REQUEST_CODE_DIALOG_TWO = 2;
    public static final int REQUEST_CODE_DIALOG_THREE = 3;


    // SalesDetailCallbacks
    public static final int REQUEST_CODE_SALES_DIALOG_ONE = 1;
    public static final int REQUEST_CODE_SALES_DIALOG_TWO = 2;

    // SalesPendingAddFormDialogFragment
    public static final int REQUEST_CODE_SALES_ADD_ITEM_LOOPUP = 1;

    // OpenSalesAddFormDialogFragment
    public static final int REQUEST_CODE_OPEN_SALES_ADD_ITEM_LOOKUP = 2;

}
