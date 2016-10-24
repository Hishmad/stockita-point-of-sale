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

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * This class is the content provider for the internal database
 */
public class ContentProviderPOS extends ContentProvider {

    // Constant
    private static final String ERROR_UNKNOWN_URI = "Unknown URI: ";
    private static final String ERROR_FILED_TO_INSERT = "Failed to insert row into URI: ";
    private static final int TRIGGER_ITEM_ID = 100;
    private static final int TRIGGER_ITEM_ENTRY = 101;
    private static final int ITEM_MASTER_ID = 200;
    private static final int ITEM_MASTER_ENTRY = 201;
    private static final int SALES_DETAIL_PENDING_ID = 300;
    private static final int SALES_DETAIL_PENDING_ENTRY = 301;

    private static final SQLiteQueryBuilder sQueryBuilder;


    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private DatabaseHelper dbHelper;

    static {

        sQueryBuilder = new SQLiteQueryBuilder();
        sQueryBuilder.setTables(ContractData.TriggerItemMasterEntry.TABLE_NAME);
        sQueryBuilder.setTables(ContractData.ItemMasterEntry.TABLE_NAME);
        sQueryBuilder.setTables(ContractData.SalesDetailPendingEntry.TABLE_NAME);
    }

    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ContractData.AUTHORITY;

        matcher.addURI(authority, ContractData.DIR_TRIGGER_ITEM_MASTER + "/#", TRIGGER_ITEM_ID);
        matcher.addURI(authority, ContractData.DIR_TRIGGER_ITEM_MASTER, TRIGGER_ITEM_ENTRY);

        matcher.addURI(authority, ContractData.DIR_ITEM_MASTER + "/#", ITEM_MASTER_ID);
        matcher.addURI(authority, ContractData.DIR_ITEM_MASTER, ITEM_MASTER_ENTRY);

        matcher.addURI(authority, ContractData.DIR_SALES_DETAIL_PENDING + "/#", SALES_DETAIL_PENDING_ID);
        matcher.addURI(authority, ContractData.DIR_SALES_DETAIL_PENDING, SALES_DETAIL_PENDING_ENTRY);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }


    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor cursor;
        switch (sUriMatcher.match(uri)) {

            case TRIGGER_ITEM_ENTRY:
                cursor = dbHelper.getReadableDatabase().query(
                        ContractData.TriggerItemMasterEntry.TABLE_NAME,
                        projection,
                        selection,
                        selection == null ? null : selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case ITEM_MASTER_ENTRY:
                cursor = dbHelper.getReadableDatabase().query(
                        ContractData.ItemMasterEntry.TABLE_NAME,
                        projection,
                        selection,
                        selection == null ? null : selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case SALES_DETAIL_PENDING_ENTRY:
                cursor = dbHelper.getReadableDatabase().query(
                        ContractData.SalesDetailPendingEntry.TABLE_NAME,
                        projection,
                        selection,
                        selection == null ? null : selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case TRIGGER_ITEM_ID:
                cursor = dbHelper.getReadableDatabase().query(
                        ContractData.TriggerItemMasterEntry.TABLE_NAME,
                        projection,
                        ContractData.TriggerItemMasterEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case ITEM_MASTER_ID:
                cursor = dbHelper.getReadableDatabase().query(
                        ContractData.ItemMasterEntry.TABLE_NAME,
                        projection,
                        ContractData.ItemMasterEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case SALES_DETAIL_PENDING_ID:
                cursor = dbHelper.getReadableDatabase().query(
                        ContractData.SalesDetailPendingEntry.TABLE_NAME,
                        projection,
                        ContractData.ItemMasterEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            default:
                throw new UnsupportedOperationException(ERROR_UNKNOWN_URI + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }


    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {

            case TRIGGER_ITEM_ID:
                return ContractData.TriggerItemMasterEntry.CONTENT_ITEM_TYPE;

            case ITEM_MASTER_ID:
                return ContractData.ItemMasterEntry.CONTENT_ITEM_TYPE;

            case SALES_DETAIL_PENDING_ID:
                return ContractData.SalesDetailPendingEntry.CONTENT_ITEM_TYPE;

            case TRIGGER_ITEM_ENTRY:
                return ContractData.TriggerItemMasterEntry.CONTENT_TYPE;

            case ITEM_MASTER_ENTRY:
                return ContractData.ItemMasterEntry.CONTENT_TYPE;

            case SALES_DETAIL_PENDING_ENTRY:
                return ContractData.SalesDetailPendingEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException(ERROR_UNKNOWN_URI + uri);
        }
    }


    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {

            case TRIGGER_ITEM_ENTRY: {
                long _id = db.insert(ContractData.TriggerItemMasterEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = ContractData.TriggerItemMasterEntry.buildTriggerUri(_id);
                } else {
                    throw new android.database.SQLException(ERROR_FILED_TO_INSERT + uri);
                }
                break;
            }

            case ITEM_MASTER_ENTRY: {
                long _id = db.insert(ContractData.ItemMasterEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = ContractData.ItemMasterEntry.buildItemUri(_id);
                } else {
                    throw  new android.database.SQLException(ERROR_FILED_TO_INSERT + uri);
                }
                break;
            }

            case SALES_DETAIL_PENDING_ENTRY: {
                long _id = db.insert(ContractData.SalesDetailPendingEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = ContractData.SalesDetailPendingEntry.buildSalesDetailPendingUri(_id);
                } else {
                    throw  new android.database.SQLException(ERROR_FILED_TO_INSERT + uri);
                }
                break;
            }

            default:
                throw new UnsupportedOperationException(ERROR_UNKNOWN_URI + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {


        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowDeleted;

        switch (match) {

            case TRIGGER_ITEM_ENTRY:
                rowDeleted = db.delete(
                        ContractData.TriggerItemMasterEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case ITEM_MASTER_ENTRY:
                rowDeleted = db.delete(
                        ContractData.ItemMasterEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case SALES_DETAIL_PENDING_ENTRY:
                rowDeleted = db.delete(
                        ContractData.SalesDetailPendingEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException(ERROR_UNKNOWN_URI + uri);
        } // end switch

        // null deletes all rows
        if (selection == null || rowDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowDeleted;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {

            case TRIGGER_ITEM_ENTRY:
                rowsUpdated = db.update(ContractData.TriggerItemMasterEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            case ITEM_MASTER_ENTRY:
                rowsUpdated = db.update(ContractData.ItemMasterEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            case SALES_DETAIL_PENDING_ENTRY:
                rowsUpdated = db.update(ContractData.SalesDetailPendingEntry.TABLE_NAME,  values, selection,  selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException(ERROR_UNKNOWN_URI + uri);
        }

        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }


    /**
     * Database Helper class
     */
    private class DatabaseHelper extends SQLiteOpenHelper {


        // Constant
        private static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "stockitapointofsales.db";

        /**
         * Constructor
         * @param context
         */
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }



        @Override
        public void onCreate(SQLiteDatabase db) {

            final String CREATE_TRIGGER_ITEM_MASTER_TABLE = "CREATE TABLE " +
                    ContractData.TriggerItemMasterEntry.TABLE_NAME + " (" +
                    ContractData.TriggerItemMasterEntry._ID + " INTEGER PRIMARY KEY, " +
                    ContractData.TriggerItemMasterEntry.COLUMN_TIME_STAMP + " TEXT)";

            final String CREATE_ITEM_MASTER_TABLE = "CREATE TABLE " +
                    ContractData.ItemMasterEntry.TABLE_NAME + "(" +
                    ContractData.ItemMasterEntry._ID + " INTEGER PRIMARY KEY, " +
                    ContractData.ItemMasterEntry.COLUMN_PUSH_KEY + " TEXT, " +
                    ContractData.ItemMasterEntry.COLUMN_ITEM_NUMBER + " TEXT, " +
                    ContractData.ItemMasterEntry.COLUMN_ITEM_DESC + " TEXT, " +
                    ContractData.ItemMasterEntry.COLUMN_ITEM_UNIT + " TEXT, " +
                    ContractData.ItemMasterEntry.COLUMN_ITEM_PRICE + " TEXT)";

            final String CREATE_SALES_DETAIL_PENDING_TABLE = "CREATE TABLE " +
                    ContractData.SalesDetailPendingEntry.TABLE_NAME + "(" +
                    ContractData.SalesDetailPendingEntry._ID + " INTEGER PRIMARY KEY, " +
                    ContractData.SalesDetailPendingEntry.COLUMN_PUSH_KEY + " TEXT, " +
                    ContractData.SalesDetailPendingEntry.COLUMN_ITEM_NUMBER + " TEXT, " +
                    ContractData.SalesDetailPendingEntry.COLUMN_ITEM_DESC + " TEXT, " +
                    ContractData.SalesDetailPendingEntry.COLUMN_ITEM_UNIT + " TEXT, " +
                    ContractData.SalesDetailPendingEntry.COLUMN_ITEM_PRICE + " TEXT, " +
                    ContractData.SalesDetailPendingEntry.COLUMN_ITEM_QUANTITY + " TEXT, " +
                    ContractData.SalesDetailPendingEntry.COLUMN_ITEM_DISCOUNT + " TEXT, " +
                    ContractData.SalesDetailPendingEntry.COLUMN_ITEM_DISCOUNT_AMOUNT + " TEXT, " +
                    ContractData.SalesDetailPendingEntry.COLUMN_ITEM_AMOUNT + " TEXT)";


            // Create
            db.execSQL(CREATE_TRIGGER_ITEM_MASTER_TABLE);
            db.execSQL(CREATE_ITEM_MASTER_TABLE);
            db.execSQL(CREATE_SALES_DETAIL_PENDING_TABLE);


        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {

            db.execSQL("DROP TABLE IF EXISTS " + ContractData.TriggerItemMasterEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + ContractData.ItemMasterEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + ContractData.SalesDetailPendingEntry.TABLE_NAME);

        }
    }


}
