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

package com.stockita.stockitapointofsales.itemmaster;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.stockita.stockitapointofsales.R;
import com.stockita.stockitapointofsales.data.ItemModel;
import com.stockita.stockitapointofsales.interfaces.ItemMasterAddEditCallbacks;
import com.stockita.stockitapointofsales.utilities.Constants;
import com.stockita.stockitapointofsales.zxing.IntentIntegrator;
import com.stockita.stockitapointofsales.zxing.IntentResult;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This class will display a Dialog Fragment on the screen for the user to entry a new item master
 * and store the data in the server.
 * It has barcode scanner feature.
 */
public class ItemMasterAddFormDialogFragment extends DialogFragment {

    // Constants
    private static final String TAG_LOG = ItemMasterAddFormDialogFragment.class.getSimpleName();
    private static final String KEY_ONE = TAG_LOG + ".KEY_ONE";

    // Views
    @Bind(R.id.item_number_form)
    EditText mItemNumberForm;
    @Bind(R.id.barcode_scanner)
    ImageButton mBarcodeScanner;
    @Bind(R.id.item_desc_form)
    EditText mItemDescForm;
    @Bind(R.id.item_unit_form)
    EditText mItemUnitForm;
    @Bind(R.id.item_price_form)
    EditText mItemPriceForm;
    @Bind(R.id.save_button)
    TextView mSaveButton;
    @Bind(R.id.cancel_button)
    TextView mCancelButton;

    /**
     * The user's UID
     */
    private String mUserUid;


    /**
     * Empty constructor
     */
    public ItemMasterAddFormDialogFragment() {}


    /**
     * Static method as constructor to get the data pass into here
     * @param userUid           The user's UID
     * @return                  This fragment
     */
    public static ItemMasterAddFormDialogFragment newInstance(String userUid) {

        ItemMasterAddFormDialogFragment fragment = new ItemMasterAddFormDialogFragment();
        Bundle args = new Bundle();
        args.putString(KEY_ONE, userUid);
        fragment.setArguments(args);

        return fragment;

    }


    /**
     * Android's callback
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the users encoded email from the argument
        mUserUid = getArguments().getString(KEY_ONE);
    }


    /**
     * Android's callback
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Initialize the view and the layout
        View view = inflater.inflate(R.layout.dialog_fragment_item_master_form, container);

        // initialize butterKnife
        ButterKnife.bind(this, view);

        return view;

    }


    /**
     * When the user click on the save button
     */
    @OnClick(R.id.save_button)
    void clickSaveButton() {


        /**
         * Get the itemNumber from the UI, it's optional
         */
        String itemNumber;
        itemNumber = mItemNumberForm.getText().toString();

        /**
         * Get the itemDesc from the UI, this can't be empty
         * if empty notify the user
         */
        String itemDesc;
        itemDesc = mItemDescForm.getText().toString();
        if (itemDesc.length() == 0) {
            mItemDescForm.setError(getString(R.string.ERROR_dialog_fragment_item_master_form));
        }

        /**
         * Get the itemUnit from the UI, this can't be empty
         * if empty notify the user
         */
        String itemUnit;
        itemUnit = mItemUnitForm.getText().toString();
        if (itemUnit.length() == 0) {
            mItemUnitForm.setError(getString(R.string.ERROR_dialog_fragment_item_master_form));
        }


        /**
         * Get the itemPrice from the UI, this can't be empty
         * so 0 will replace the empty
         */
        String itemPrice;
        itemPrice = mItemPriceForm.getText().toString();
        if (itemPrice.length() == 0) {
            mItemPriceForm.setText("0");
        }

        // Check before save
        if (itemDesc.length() > 0 && itemUnit.length() > 0) {

            // Instantiate model then pass the data as argument
            ItemModel model = new ItemModel(itemNumber, itemDesc, itemUnit, itemPrice);

            // Get the reference to Send data to the server
            DatabaseReference locationItem = FirebaseDatabase.getInstance().getReference();

            // Get to the location /userUid/itemMaster + push() + then pass the model object
            locationItem.child(mUserUid).child(Constants.FIREBASE_ITEM_MASTER_LOCATION)
                    .push().setValue(model);

            // TODO: For future development
            // Get to the location /userUid/trigger/itemMaster/lastOnline + then add SERVER_VALUE.timestamp
            locationItem.child(mUserUid).child(Constants.FIREBASE_TRIGGER_LOCATION)
                    .child(Constants.FIREBASE_ITEM_MASTER_LOCATION).child(Constants.FIREBASE_PROPERTY_LAST_ONLINE)
                    .setValue(ServerValue.TIMESTAMP);

            // Call back to send the result message to the activity
            ((ItemMasterAddEditCallbacks) getActivity()).getTheResultFromTheDialog(Constants.REQUEST_CODE_DIALOG_ONE, "Item added", null, null);
            getDialog().dismiss();

        } else {

            // Notify the user using
            Toast.makeText(getActivity(), "One or more fields can't be empty", Toast.LENGTH_SHORT).show();

        }

    }


    /**
     * When the user click on the red cancel button
     */
    @OnClick(R.id.cancel_button)
    void clickCancelButton() {

        // dismiss this dialog
        getDialog().dismiss();

    }

    /**
     * Barcode scanner {@link IntentIntegrator}
     */
    @OnClick(R.id.barcode_scanner)
    void barcodeScanner() {

        // Get the barcode scanner
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.initiateScan();
    }


    /**
     * Android's callbacks
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Retrieve barcode scanner result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (scanningResult != null) {

            // Get the scan content which is the item number
            String scanContent = scanningResult.getContents();

            // Get the scan format
            String scanFormat = scanningResult.getFormatName();

            // Pass the scan content to the UI
            mItemNumberForm.setText(scanContent);

        } else {
            Toast toast = Toast.makeText(getActivity(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
