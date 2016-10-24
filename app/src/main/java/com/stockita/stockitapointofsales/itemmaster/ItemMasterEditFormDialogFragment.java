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
 * This class is to display a dialog fragment to edit the item master.
 */
public class ItemMasterEditFormDialogFragment extends DialogFragment {


    // Constants
    private static final String TAG_LOG = ItemMasterEditFormDialogFragment.class.getSimpleName();
    private static final String KEY_ONE = TAG_LOG + ".KEY_ONE";
    private static final String KEY_TWO = TAG_LOG + ".KEY_TWO";
    private static final String KEY_THREE = TAG_LOG + ".KEY_THREE";

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

    // Member variables
    private String mUserUid;
    private String mPushKey;
    private ItemModel mModel;

    /**
     * Empty constructor
     */
    public ItemMasterEditFormDialogFragment() {}


    /**
     * Statics method as constructor to get the data pass into here
     * @param userUid           The user's UID
     * @return                  This fragment
     */
    public static ItemMasterEditFormDialogFragment newInstance(String userUid, String pushKey, ItemModel model) {

        ItemMasterEditFormDialogFragment fragment = new ItemMasterEditFormDialogFragment();
        Bundle args = new Bundle();
        args.putString(KEY_ONE, userUid);
        args.putString(KEY_TWO, pushKey);
        args.putParcelable(KEY_THREE, model);
        fragment.setArguments(args);

        return fragment;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserUid = getArguments().getString(KEY_ONE);
        mPushKey = getArguments().getString(KEY_TWO);
        mModel = getArguments().getParcelable(KEY_THREE);

    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Initialize the view and the layout
        View view = inflater.inflate(R.layout.dialog_fragment_item_master_form, container);

        // initialize butterKnife
        ButterKnife.bind(this, view);


        // Populate the UI from the server
        mItemNumberForm.setText(mModel.getItemNumber());
        mItemDescForm.setText(mModel.getItemDesc());
        mItemUnitForm.setText(mModel.getUnitOfMeasure());
        mItemPriceForm.setText(mModel.getItemPrice());

        // Save the changes..
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Send the data to the server
                DatabaseReference locationItem = FirebaseDatabase.getInstance().getReference();

                // Get data from the UI
                String itemNumber = mItemNumberForm.getText().toString();
                String itemDesc = mItemDescForm.getText().toString();
                String itemUnit = mItemUnitForm.getText().toString();
                String itemPrice = mItemPriceForm.getText().toString();

                // Instantiate model then pass the data as argument
                ItemModel model = new ItemModel(itemNumber, itemDesc, itemUnit, itemPrice);

                // Get to the location /encodedEmail/itemMaster + mPushKey + then pass the model object
                locationItem.child(mUserUid).child(Constants.FIREBASE_ITEM_MASTER_LOCATION)
                        .child(mPushKey).setValue(model);

                // Get to the location /encodedEmail/trigger/itemMaster/lastOnline + then add SERVER_VALUE.timestamp
                locationItem.child(mUserUid).child(Constants.FIREBASE_TRIGGER_LOCATION)
                        .child(Constants.FIREBASE_ITEM_MASTER_LOCATION).child(Constants.FIREBASE_PROPERTY_LAST_ONLINE)
                        .setValue(ServerValue.TIMESTAMP);


                // Call back to send the result message to the activity
                ((ItemMasterAddEditCallbacks) getActivity()).getTheResultFromTheDialog(Constants.REQUEST_CODE_DIALOG_ONE, "Done editing", null, null);

                // dismiss the dialog from the screen
                getDialog().dismiss();


            }
        });


        // cancel button..
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // dismiss the dialog from the screen
                getDialog().dismiss();
            }
        });


        return view;

    }

    /**
     * Get the barcode scanner
     */
    @OnClick(R.id.barcode_scanner)
    void barcodeScanner() {

        // Get the barcode scanner
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.initiateScan();
    }


    /**
     * Get the result from the barcode scanner
     * @param requestCode       request code
     * @param resultCode        result code
     * @param data              intent data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Retrieve barcode scanner result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
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
