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

package com.stockita.stockitapointofsales.salespack.openpack;

import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stockita.stockitapointofsales.R;
import com.stockita.stockitapointofsales.data.ContractData;
import com.stockita.stockitapointofsales.data.ItemModel;
import com.stockita.stockitapointofsales.data.SalesDetailModel;
import com.stockita.stockitapointofsales.itemmaster.LookupItemMasterListDialogFragment;
import com.stockita.stockitapointofsales.itemmaster.MiniLookupItemMaster;
import com.stockita.stockitapointofsales.utilities.Constants;
import com.stockita.stockitapointofsales.zxing.IntentIntegrator;
import com.stockita.stockitapointofsales.zxing.IntentResult;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * When the user will add new sales detail into the already open bill
 * server node is /openSalesDetail/...
 */
public class OpenSalesAddFormDialogFragment extends DialogFragment {


    // Constant
    private static final String TAG_LOG = OpenSalesAddFormDialogFragment.class.getSimpleName();
    private static final String FRAGMENT_DIALOG_ITEM_MASTER_FORM = "fragment_dialog_item_master_form";
    private static final String KEY_ITEM_NUMBER = "KEY_ITEM_NUMBER";
    private static final String KEY_ITEM_MODEL = "KEY_ITEM_MODEL";
    private static final String KEY_DATA_ONE = TAG_LOG + ".KEY_DATA_ONE";
    private static final String KEY_DATA_TWO = TAG_LOG + ".KEY_DATA_TWO";
    private static final String KEY_DATA_THREE = TAG_LOG + ".KEY_DATA_THREE";
    private static final String KEY_DATA_FOUR = TAG_LOG + ".KEY_DATA_FOUR";


    /**
     * The user's login UID
     */
    private String mUserUid;

    /**
     * {@link ItemModel}
     */
    private ItemModel mItemModel;

    /**
     * Item Number is a field/state in {@link ItemModel#itemNumber}, this is useful
     * to store a barcode of any.
     */
    private String mItemNumber;

    /**
     * The SalesHeader push() key, will be used to store the data in the /openSalesDetail/salesHeaderPush()/...
     */
    private String mSalesHeaderKey;


    // Views
    @Bind(R.id.item_number_form)
    TextView mItemNumberForm;
    @Bind(R.id.item_desc_form)
    TextView mItemDescForm;
    @Bind(R.id.item_unit_form)
    TextView mItemUnitForm;
    @Bind(R.id.item_price_form)
    TextView mItemPriceForm;
    @Bind(R.id.item_qty_form)
    EditText mItemQtyForm;
    @Bind(R.id.item_discount_form)
    EditText mItemDiscountForm;


    /**
     * Empty constructor
     */
    public OpenSalesAddFormDialogFragment() {}


    /**
     * Statics method as constructor to get the data pass into here
     * @param userUid                   User's UID
     * @param itemMasterPushKey         Item master pushKey
     * @param salesHeaderKey            Sales header pushKey
     * @param model                     {@link ItemModel}
     * @return                          this fragment
     */
    public static OpenSalesAddFormDialogFragment newInstance(String userUid, String itemMasterPushKey, String salesHeaderKey, ItemModel model) {

        // Instantiate the model
        OpenSalesAddFormDialogFragment fragment = new OpenSalesAddFormDialogFragment();

        // put the variable in the bundle
        Bundle args = new Bundle();
        args.putString(KEY_DATA_ONE, userUid);
        args.putString(KEY_DATA_TWO, itemMasterPushKey);
        args.putString(KEY_DATA_THREE, salesHeaderKey);
        args.putParcelable(KEY_DATA_FOUR, model);

        // set the argument
        fragment.setArguments(args);

        // return the fragment
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Get the variable from the bundle, pass them to the field
        mUserUid = getArguments().getString(KEY_DATA_ONE);
        mSalesHeaderKey = getArguments().getString(KEY_DATA_THREE);
        mItemModel = getArguments().getParcelable(KEY_DATA_FOUR);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Initialize the view and the layout
        View view = inflater.inflate(R.layout.dialog_fragment_sales_detail_form, container);

        // initialize butterKnife
        ButterKnife.bind(this, view);


        // If data from lookup then update the UI
        if (savedInstanceState == null) {
            if (mItemModel != null) {

                // Update the UI
                if (mItemModel.getItemNumber() != null) {
                    mItemNumberForm.setText(String.valueOf(mItemModel.getItemNumber()));
                }

                if (mItemModel.getItemDesc() != null) {
                    mItemDescForm.setText(String.valueOf(mItemModel.getItemDesc()));
                }

                if (mItemModel.getUnitOfMeasure() != null) {
                    mItemUnitForm.setText(String.valueOf(mItemModel.getUnitOfMeasure()));
                }

                if (mItemModel.getItemPrice() != null) {
                    mItemPriceForm.setText(String.valueOf(mItemModel.getItemPrice()));
                }
            }
        }

        // Return the view
        return view;

    }


    /**
     * If the user long click on the item number UI it will display a dialog fragment to lookup
     * into item master list.
     */
    @OnLongClick(R.id.item_number_form)
    boolean setLongClickOnItemNumber() {

        // Instantiate the dialog fragment object and pass the encoded email as an argument
        LookupItemMasterListDialogFragment lookupItemMasterListDialogFragment = LookupItemMasterListDialogFragment.newInstance(Constants.REQUEST_CODE_OPEN_SALES_ADD_ITEM_LOOKUP, mUserUid, null, mSalesHeaderKey);

        // Show the Dialog Fragment on the screen
        lookupItemMasterListDialogFragment.show(getFragmentManager(), FRAGMENT_DIALOG_ITEM_MASTER_FORM);

        // Dismiss this dialog
        getDialog().dismiss();

        return true;
    }


    /**
     * If the user clicked the ref cancel button.
     */
    @OnClick(R.id.cancel_button)
    void setCancelButton() {
        getDialog().dismiss();
    }


    /**
     * If the user clicked the save button
     */
    @OnClick(R.id.save_button)
    void setSavebutton() {

        /**
         * Check if the itemModel {@link ItemModel} is not null, this is very important to understand
         * that it can not be saved unless the the user choose to get the item master from the barcode
         * {@link #setmBarcodeScanner()} or from the lookup {@link #setLongClickOnItemNumber()}
         */
        if (mItemModel != null) {

            // Send the data to the server
            DatabaseReference locationSalesDetailPending = FirebaseDatabase.getInstance().getReference();

            // Get the user edit data from the UI
            String itemNumber = mItemNumberForm.getText().toString();
            String itemDesc = mItemDescForm.getText().toString();
            String itemUnit = mItemUnitForm.getText().toString();
            String itemPrics = mItemPriceForm.getText().toString();
            String itemQty = mItemQtyForm.getText().toString();
            String itemDiscount = mItemDiscountForm.getText().toString();

            /**
             * Lets calculate the discount, and the itemAmount
             */

            // Get the qty from the UI
            double qty;
            try {
                qty = Double.parseDouble(itemQty);
            } catch (Exception e) {
                qty = 1;
            }

            // Get the price from the UI
            double price;
            try {
                price = Double.parseDouble(itemPrics);
            } catch (Exception e) {
                price = 0;
            }

            // Calculate the price . qty
            double subTotalOne = qty * price;

            // get the Discount %
            double discount;
            try {
                discount = Double.parseDouble(itemDiscount);
            } catch (Exception e) {
                discount = 0;
            }

            double discountAmount;
            try {
                discountAmount = (subTotalOne * discount) / 100;
            } catch (Exception e) {
                discountAmount = 0;
            }

            // itemAmount
            double amount = subTotalOne - discountAmount;


            // now pack the following to the object
            String itemDiscountAmount = String.valueOf(discountAmount);
            String itemAmount = String.valueOf(amount);

            // Pack to the object, using constructor
            SalesDetailModel salesDetailModel = new SalesDetailModel(itemNumber, itemDesc, itemUnit, itemPrics, String.valueOf(qty), String.valueOf(discount), itemDiscountAmount, itemAmount);

            // Get to the location in the server use the same push() key detail
            locationSalesDetailPending
                    .child(mUserUid)
                    .child(Constants.FIREBASE_OPEN_SALES_DETAIL_LOCATION)
                    .child(mSalesHeaderKey)
                    .push()
                    .setValue(salesDetailModel);


            // Dismiss the dialog
            getDialog().dismiss();
        }

    }


    /**
     * Barcode scanner
     */
    @OnClick(R.id.barcode_scanner)
    void setmBarcodeScanner() {

        // Get the barcode scanner and do the work, later pass the result to onActivityResult()
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.initiateScan();

    }


    /**
     * Android's callback
     * @param requestCode       It is the int number that define when invokeing method {@link android.app.Activity#startActivityForResult(Intent, int, Bundle)}
     * @param resultCode        It is the int number passed back {@link android.app.Activity#setResult}
     * @param data              The {@link Intent} instance
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Retrieve barcode scanner result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        /**
         * Check if null then no result
         */
        if (scanningResult != null) {

            // Get the result from the scanner
            String scanContent = scanningResult.getContents();
            //String scanFormat = scanningResult.getFormatName();

            // Pass the scan content to the UI
            mItemNumber = scanContent;
            mItemNumberForm.setText(scanContent);


            /**
             * The code below...
             * We will retrieve the item master based on the barcode scanner which is the item number
             */
            if (mItemNumber != null) {

                // Query the server and retrieve the item master matching scanned item number
                DatabaseReference itemNumberRef = FirebaseDatabase.getInstance().getReference().child(mUserUid).child(Constants.FIREBASE_ITEM_MASTER_LOCATION);
                itemNumberRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        // instantiate the Item master model
                        mItemModel = new ItemModel();

                        // Initialize the modeList to pack the item master model
                        ArrayList<ItemModel> modelList = new ArrayList<>();

                        // Initialize the keyList to pack the push() keys
                        //ArrayList<String> keyList = new ArrayList<>();

                        // Initialize the content resolver
                        ContentResolver contentResolver = null;
                        try {
                            contentResolver = getActivity().getContentResolver();
                        } catch (Exception e) {
                            Log.e(TAG_LOG, e.getMessage());
                        }

                        // Delete all before insert new data, since we are going to store
                        // some data in local database
                        try {
                            if (contentResolver != null) {
                                contentResolver.delete(ContractData.ItemMasterEntry.CONTENT_URI, null, null);
                            }
                        } catch (Exception e) {
                            Log.e(TAG_LOG, e.getMessage());
                        }

                        // Initialize the content values
                        ContentValues contentValues = new ContentValues();

                        // Iterate
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {

                            // Instantiate the item model from the server
                            ItemModel itemModel = snap.getValue(ItemModel.class);

                            // Get the specific data that matches the itemNumber
                            if (itemModel.getItemNumber().matches(mItemNumber)) {

                                // Pack the model and the key to the list
                                modelList.add(itemModel);
                                //keyList.add(snap.getKey());

                                // Pack the data into the context values;
                                contentValues.put(ContractData.ItemMasterEntry.COLUMN_PUSH_KEY, snap.getKey());
                                contentValues.put(ContractData.ItemMasterEntry.COLUMN_ITEM_NUMBER, itemModel.getItemNumber());
                                contentValues.put(ContractData.ItemMasterEntry.COLUMN_ITEM_DESC, itemModel.getItemDesc());
                                contentValues.put(ContractData.ItemMasterEntry.COLUMN_ITEM_UNIT, itemModel.getUnitOfMeasure());
                                contentValues.put(ContractData.ItemMasterEntry.COLUMN_ITEM_PRICE, itemModel.getItemPrice());

                                // Insert them to local database, intentionally using try black rather than if block
                                // because we want to log the error if any.
                                try {
                                    if (contentResolver != null) {
                                        contentResolver.insert(ContractData.ItemMasterEntry.CONTENT_URI, contentValues);
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG_LOG, e.getMessage());
                                }

                            }
                        }

                        /**
                         * If the barcode scanner has found more than one item with the
                         * same item number then this code will work...
                         */
                        if (modelList.size() > 1) {

                            // Pass the model list
                            // Instantiate the dialog fragment object and pass the encoded email as an argument
                            MiniLookupItemMaster miniLookupItemMaster = MiniLookupItemMaster.newInstance(Constants.REQUEST_CODE_OPEN_SALES_ADD_ITEM_LOOKUP, mUserUid, mSalesHeaderKey, null, null);

                            // Show the Dialog Fragment on the screen
                            miniLookupItemMaster.show(getFragmentManager(), FRAGMENT_DIALOG_ITEM_MASTER_FORM);

                            // Dismiss this dialog
                            getDialog().dismiss();
                        }

                        /**
                         * Else if the barcode just found one item number that match only one
                         * then this code will work...
                         */
                        else if (modelList.size() == 1) {

                            // Update the UI
                            mItemModel.setItemNumber(mItemNumber);
                            mItemDescForm.setText(modelList.get(0).getItemDesc());
                            mItemModel.setItemDesc(mItemDescForm.getText().toString());
                            mItemUnitForm.setText(modelList.get(0).getUnitOfMeasure());
                            mItemModel.setUnitOfMeasure(mItemUnitForm.getText().toString());
                            mItemPriceForm.setText(modelList.get(0).getItemPrice());
                            mItemModel.setItemPrice(mItemPriceForm.getText().toString());

                        }

                        /**
                         * Else if the barcode just found no match then this code will work...
                         */
                        else if (modelList.size() == 0) {

                            // This code will prevent to save an empty/not available item
                            // once we set the mItemModel to null it will not be able to save.
                            mItemModel = null;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG_LOG, databaseError.getMessage());
                    }
                });
            }

        } else {
            Toast toast = Toast.makeText(getActivity(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    /**
     * Save instance state
     *
     * @param outState      {@link Bundle} instance
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mItemNumber = mItemNumberForm.getText().toString();
        outState.putString(KEY_ITEM_NUMBER, mItemNumber);
        outState.putParcelable(KEY_ITEM_MODEL, mItemModel);

    }


    /**
     * Restore state after configuraton changes
     * @param savedInstanceState        {@link Bundle} object
     */
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        // screen rotation or config changes
        if (savedInstanceState != null) {

            mItemModel = savedInstanceState.getParcelable(KEY_ITEM_MODEL);
            mItemNumber = savedInstanceState.getString(KEY_ITEM_NUMBER);

            if (mItemNumber != null) {
                mItemNumberForm.setText(mItemNumber);
            }

            if (mItemModel != null && mItemModel.getItemDesc() != null) {
                mItemDescForm.setText(mItemModel.getItemDesc());
            }

            if (mItemModel != null && mItemModel.getUnitOfMeasure() != null) {
                mItemUnitForm.setText(mItemModel.getUnitOfMeasure());
            }

            if (mItemModel != null && mItemModel.getItemPrice() != null) {
                mItemPriceForm.setText(mItemModel.getItemPrice());
            }
        }
    }
}
