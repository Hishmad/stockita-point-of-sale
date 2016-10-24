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

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stockita.stockitapointofsales.R;
import com.stockita.stockitapointofsales.customViews.AdapterViewOpenSalesDetailList;
import com.stockita.stockitapointofsales.data.ContractData;
import com.stockita.stockitapointofsales.data.SalesDetailModel;
import com.stockita.stockitapointofsales.interfaces.OpenSalesHeaderListCallbacks;
import com.stockita.stockitapointofsales.salespack.pendingpack.SalesPendingCheckoutDialogFragment;
import com.stockita.stockitapointofsales.utilities.Constants;
import com.stockita.stockitapointofsales.utilities.Utility;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This class is a fragment to display the sales detail in the ../openSalesDetail/.. node
 */
public class OpenSalesDetailFragmentUI extends Fragment implements Toolbar.OnMenuItemClickListener {

    // Constants
    private static final String TAG_LOG = OpenSalesDetailFragmentUI.class.getSimpleName();
    private static final String KEY_ONE = TAG_LOG + ".KEY_ONE";
    private static final String KEY_TWO = TAG_LOG + ".KEY_TWO";

    // Member variable
    private String mUserUid;
    private String mSalesHeaderKey;
    public Adapter mAdapter;


    private DatabaseReference mCashCalc;
    private ValueEventListener mCashCalcListener;


    // Views
    @Bind(R.id.cash_toolbar)
    Toolbar mCashToolbar;
    @Bind(R.id.total_toolbar)
    TextView mTotalToolbar;
    @Bind(R.id.list_of_sales_detail_open)
    RecyclerView mList;

    /**
     * empty constructor
     */
    public OpenSalesDetailFragmentUI() {
    }

    /**
     * Pass data from the activity into here
     * @param userUid      The user' UID
     * @param headerKey    The {@link com.stockita.stockitapointofsales.data.SalesHeaderModel} key
     * @return This fragment
     */
    public static OpenSalesDetailFragmentUI newInstance(String userUid, String headerKey) {

        OpenSalesDetailFragmentUI fragment = new OpenSalesDetailFragmentUI();
        Bundle args = new Bundle();

        args.putString(KEY_ONE, userUid);
        args.putString(KEY_TWO, headerKey);

        fragment.setArguments(args);

        return fragment;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the user encoded email from the activity
        mUserUid = getArguments().getString(KEY_ONE);

        // Get the sales header key from the activity
        mSalesHeaderKey = getArguments().getString(KEY_TWO);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Initialize the view
        View view = inflater.inflate(R.layout.fragment_list_of_open_sales_detail, container, false);

        // Initialize the ButterKnife
        ButterKnife.bind(this, view);

        // Set more efficient
        mList.setHasFixedSize(true);

        // Initialize the adapter
        mAdapter = new Adapter(getActivity(), mUserUid, mSalesHeaderKey);

        // set the adapter
        mList.setAdapter(mAdapter);


        // Initialize the toolbar to display the invoice total
        initCashToolbar();


        /**
         * Code below is for the number of span depend device (Phone / tablet)
         * and orientation (portrait / landscape)
         */
        int columnCount;
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        boolean isLandscape = getResources().getBoolean(R.bool.isLandscape);

        if (isTablet && isLandscape) {
            columnCount = 3;
        } else if (!isTablet && isLandscape) {
            columnCount = 2;
        } else if (isTablet && !isLandscape) {
            columnCount = 2;
        } else {
            columnCount = 1;
        }

        // Initialize the LayoutManager for item master
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), columnCount, GridLayoutManager.VERTICAL, false);

        // Set the layout manager
        mList.setLayoutManager(gridLayoutManager);

        // Return the view
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Remove listeners
        if (mCashCalc != null && mCashCalcListener != null) {
            mCashCalc.removeEventListener(mCashCalcListener);
        }

        // Remove listeners in the adapter
        if (mAdapter != null) {
            mAdapter.removeListeners();
        }
    }


    /**
     * Helper method to initialize the cash toolbar
     * which is a second toolbar that displays the total amount and a checkout menu item
     */
    private void initCashToolbar() {

        // Inflate the menu into the cash_toolbar
        mCashToolbar.inflateMenu(R.menu.menu_cash_toolbar);

        // Set the background color
        mCashToolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.burgundy));

        // Set the menu item click listeners
        mCashToolbar.setOnMenuItemClickListener(this);

        // Display the sum of amount

        /**
         * Code below is to update the cash amount on the red cash toolbar
         */
        mCashCalc = FirebaseDatabase.getInstance().getReference()
                .child(mUserUid)
                .child(Constants.FIREBASE_OPEN_SALES_DETAIL_LOCATION)
                .child(mSalesHeaderKey);

        // Setup the listener
        mCashCalcListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                // Initialize the calculate
                double calculate = 0;

                // Iterate
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    // Get the instance of salesDetailModel from the dataSnapshot
                    SalesDetailModel salesDetailModel = data.getValue(SalesDetailModel.class);


                    // Do the calculation
                    try {
                        double calc = Double.parseDouble(salesDetailModel.getItemAmount());
                        calculate += calc;
                    } catch (Exception e) {
                        Log.e(TAG_LOG, e.getMessage());
                    }

                }

                // Display the cash amount
                mTotalToolbar.setText(String.valueOf(calculate));

                // Store the total in the SharedPreference for later use
                Utility.setAnyString(getActivity(), "checkout_total_open", String.valueOf(calculate));


                /**
                 * Update the totalAmount in the /openSalesHeader mode
                 */
                DatabaseReference openSalesHeader = FirebaseDatabase.getInstance().getReference()
                        .child(mUserUid)
                        .child(Constants.FIREBASE_OPEN_SALES_HEADER_LOCATION)
                        .child(mSalesHeaderKey);

                // If somehow the /openSalesDetail/headerKey has no children, then remove the /openSalesHeader/headerKey
                if (!dataSnapshot.hasChildren()) {
                    openSalesHeader.setValue(null);
                } else {
                    // Update the totalAmount node
                    openSalesHeader.child(Constants.FIREBASE_PROPERTY_TOTAL_AMOUNT).setValue(String.valueOf(calculate));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG_LOG, databaseError.getMessage());

            }
        };

        // Attach the listener
        mCashCalc.addValueEventListener(mCashCalcListener);

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_cash_checkout:

                // TODO add check before proceed

                // Go to the checkout dialog
                packTheCurrentSalesDetailOpenInToLocalDatabase();

                // Get the sales detail open total from the toolbar
                String total = mTotalToolbar.getText().toString();

                // Pass the data to the activity then to the SalesPendingCheckoutDialogFragment
                ((OpenSalesHeaderListCallbacks) getActivity()).getSalesCheckoutDialog(mUserUid, total, mSalesHeaderKey);

                return true;
        }

        return false;
    }


    /**
     * This method will insert the current Sales Detail Open into a local database
     * so later can be query by {@link OpenSalesCheckoutDialogFragment}
     */
    private void packTheCurrentSalesDetailOpenInToLocalDatabase() {

        // Delete all data in the local database before we insert new data
        final ContentResolver contentResolver = getActivity().getContentResolver();
        contentResolver.delete(ContractData.SalesDetailPendingEntry.CONTENT_URI, null, null);

        // Get the reference to ../openSalesDetail/...
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(mUserUid)
                .child(Constants.FIREBASE_OPEN_SALES_DETAIL_LOCATION)
                .child(mSalesHeaderKey);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                // Iterate
                for (DataSnapshot snap : dataSnapshot.getChildren()) {

                    // Initialize the content values
                    ContentValues values = new ContentValues();

                    // Initialize the model
                    SalesDetailModel model = snap.getValue(SalesDetailModel.class);

                    // Get the state
                    String key = snap.getKey();
                    String itemNumber = model.getItemNumber();
                    String itemDesc = model.getItemDesc();
                    String itemUnit = model.getItemUnit();
                    String itemPrice = model.getItemPrice();
                    String itemQty = model.getItemQuantity();
                    String itemDiscount = model.getItemDiscount();
                    String itemDiscountAmount = model.getItemDiscountAmout();
                    String itemAmount = model.getItemAmount();

                    // Pack into ContentValues object
                    values.put(ContractData.SalesDetailPendingEntry.COLUMN_PUSH_KEY, key);
                    values.put(ContractData.SalesDetailPendingEntry.COLUMN_ITEM_NUMBER, itemNumber);
                    values.put(ContractData.SalesDetailPendingEntry.COLUMN_ITEM_DESC, itemDesc);
                    values.put(ContractData.SalesDetailPendingEntry.COLUMN_ITEM_UNIT, itemUnit);
                    values.put(ContractData.SalesDetailPendingEntry.COLUMN_ITEM_PRICE, itemPrice);
                    values.put(ContractData.SalesDetailPendingEntry.COLUMN_ITEM_QUANTITY, itemQty);
                    values.put(ContractData.SalesDetailPendingEntry.COLUMN_ITEM_DISCOUNT, itemDiscount);
                    values.put(ContractData.SalesDetailPendingEntry.COLUMN_ITEM_DISCOUNT_AMOUNT, itemDiscountAmount);
                    values.put(ContractData.SalesDetailPendingEntry.COLUMN_ITEM_AMOUNT, itemAmount);


                    // Insert into local database
                    try {
                        contentResolver.insert(ContractData.SalesDetailPendingEntry.CONTENT_URI, values);
                    } catch (Exception e) {
                        Log.e(TAG_LOG, e.getMessage());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG_LOG, databaseError.getMessage());
            }
        });
    }


    /**
     * Class adapter
     */
    public class Adapter extends RecyclerView.Adapter<ViewHolder> {

        /**
         * Activity context
         */
        private Context mAdapterContext;

        /**
         * Container for {@link SalesDetailModel} object
         */
        private ArrayList<SalesDetailModel> mSalesDetailList;

        /**
         * Container for push() key /openSalesDetail/ node
         */
        private ArrayList<String> mSalesDetailKeyList;

        /**
         * The user's UID
         */
        private String mAdapterUserUid;

        /**
         * The push() key for /openSalesHeader/
         */
        private String mAdapterHeaderKey;

        /**
         * Refeference to /openSalesDetail/headerKey/... node
         */
        private DatabaseReference detailRef;

        /**
         * Listener to the node above
         */
        private ChildEventListener detailListener;

        /**
         * ArrayList type string as container to store item master's push() key
         * for all the element that the user select it to be deleted using the ActionMode
         */
        protected ArrayList<String> mListOfDeletePushKeys;

        /**
         * Container for the view holder that about to be delete
         */
        protected HashMap<String, ViewHolder> mViewHolderMap;

        /**
         * Boolean as flag if ActionMode is on or off.
         */
        public boolean isContextualMode = false;




        /**
         * Constructor
         * @param context       Activity context
         * @param userUid       The user's UID
         * @param headerKey     The header key from openSalesHeader
         */
        public Adapter(Context context, String userUid, String headerKey) {

            // Pass the context
            mAdapterContext = context;

            // Pass the user UID
            mAdapterUserUid = userUid;

            // Pass the openSalesHeader key
            mAdapterHeaderKey = headerKey;

            // Initialize the containers
            mSalesDetailKeyList = new ArrayList<>();
            mSalesDetailList = new ArrayList<>();

            // Get the reference /openSalesDetail/openSalesHeaderKey/...
            detailRef = FirebaseDatabase.getInstance().getReference()
                    .child(mAdapterUserUid)
                    .child(Constants.FIREBASE_OPEN_SALES_DETAIL_LOCATION)
                    .child(mAdapterHeaderKey);

            //Get the data feed from Firebase database
            getDataFromFirebase();

            //Notify change
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return R.layout.adapter_each_card_in_sales_detail_open;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            // Initialize the view object
            View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

            // Return the view holder and pass the view object as argument
            return new ViewHolder(view);

        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, int position) {

            if (mSalesDetailList != null) {

                // Get the key for the current detail
                final String pushKeyDetail = mSalesDetailKeyList.get(position);

                // Get the model instance from the list
                final SalesDetailModel model = mSalesDetailList.get(position);

                // Get the field from the model
                String itemNumber = model.getItemNumber();
                String itemDesc = model.getItemDesc();
                String itemUnit = model.getItemUnit();
                String itemPrice = model.getItemPrice();
                String itemQuantity = model.getItemQuantity();
                String itemDiscount = model.getItemDiscount();
                String itemAmount = model.getItemAmount();

                // update the UI
                viewHolder.mAdapterView.getmItemDesc().setText(itemDesc);
                viewHolder.mAdapterView.getmItemPrice().setText(itemPrice);
                viewHolder.mAdapterView.getmItemQuantity().setText(itemQuantity);
                viewHolder.mAdapterView.getmItemAmount().setText(itemAmount);


                /**
                 * The below for the popup menu in each element in the list
                 */
                viewHolder.mAdapterView.getmPopupMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        // If action mode then cancel before we can proceed with the click
                        if (((DeleteMultiOpenSalesDetail) getActivity()).getActionModeOpenSalesDetail() != null) {
                            ((DeleteMultiOpenSalesDetail) getActivity()).getActionModeOpenSalesDetail().finish();
                        }

                        // Get the menu item id
                        int id = menuItem.getItemId();

                        switch (id) {

                            // Delete
                            case R.id.menu_delete:

                                // Alert the user before delete.
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Warning")
                                        .setMessage("You are about to delete this detail item sales?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                /**
                                                 * Do the delete here...
                                                 */

                                                // Get the reference
                                                DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference()
                                                        .child(mAdapterUserUid)
                                                        .child(Constants.FIREBASE_OPEN_SALES_DETAIL_LOCATION)
                                                        .child(mAdapterHeaderKey)
                                                        .child(pushKeyDetail);

                                                // Pass null to delete
                                                itemRef.setValue(null);

                                            }
                                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // do nothing
                                    }
                                }).show();
                                return true;

                            // Edit
                            case R.id.menu_edit:

                                // Call the SalesPendingEditFormDialogFragment via MainActivity
                                ((OpenSalesHeaderListCallbacks) getActivity())
                                        .onSalesEditDialogCallbacks(Constants.REQUEST_CODE_SALES_DIALOG_ONE,
                                                mAdapterUserUid, pushKeyDetail, mAdapterHeaderKey, model);

                                return true;
                        }
                        return false;
                    }
                });


                /**
                 * Check if in contextual mode, so the following line of codes is to prevent other view holders
                 * from changing colors if they are not the one chosen for multi delete. It is kind of workaround.
                 * Because RV will recycle the view holder.
                 */
                if (isContextualMode) {
                    if (mViewHolderMap.get(pushKeyDetail) == null) {
                        viewHolder.mAdapterView.getmRoot().setBackgroundColor(getResources().getColor(R.color.white));
                        viewHolder.mAdapterView.getmItemPopupMenu().setBackgroundColor(getResources().getColor(R.color.white));
                    } else {
                        viewHolder.mAdapterView.getmRoot().setBackgroundColor(getResources().getColor(R.color.purple_100));
                        viewHolder.mAdapterView.getmItemPopupMenu().setBackgroundColor(getResources().getColor(R.color.purple_100));

                    }
                }


                /**
                 * When the user long click on an item it will trigger the ActionMode for multi
                 * item delete
                 */
                viewHolder.mAdapterView.getmRoot().setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        // Check if the ActionMode is not yet on.
                        if (!isContextualMode) {

                            // Now set this to true to mark that the ActionMode is on
                            isContextualMode = true;

                            // Initialize the array list as a container for item master push() key
                            mListOfDeletePushKeys = new ArrayList<>();

                            // Initialize the HashMap as a container for view holder objects
                            mViewHolderMap = new HashMap<>();

                            // Pass the pushKey for this current position
                            mListOfDeletePushKeys.add(pushKeyDetail);

                            // Pass the view holder instance as a value, and pass the pushKeyDetail as the key
                            mViewHolderMap.put(pushKeyDetail, viewHolder);

                            // turn the color fot he item clicked by the user
                            viewHolder.mAdapterView.getmRoot().setBackgroundColor(getResources().getColor(R.color.purple_100));
                            viewHolder.mAdapterView.getmItemPopupMenu().setBackgroundColor(getResources().getColor(R.color.purple_100));


                            // Pass the data to the MainActivity using callbacks
                            ((DeleteMultiOpenSalesDetail) getActivity()).sendDeleteMultiOpenSalesDetail(mListOfDeletePushKeys, mViewHolderMap);

                            // Now turn the ActionMode on using callbacks
                            ((DeleteMultiOpenSalesDetail) getActivity()).onTurnActionModeOnOpenSalesDetail(mAdapterHeaderKey);

                            return true;
                        }

                        return false;
                    }
                });


                /**
                 * When the user sing click on an item
                 */
                viewHolder.mAdapterView.getmRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // Check if the ActionMode is on then user can click multi items in action mode
                        if (isContextualMode) {

                            // initialize local variables
                            boolean found = false;
                            int record = 0;

                            // check if the pushList is not 0
                            if (mListOfDeletePushKeys.size() > 0) {

                                // Iterate for each element
                                for (int i = 0; i < mListOfDeletePushKeys.size(); i++) {

                                    // check if push() key is already exist
                                    if (pushKeyDetail.equals(mListOfDeletePushKeys.get(i))) {

                                        // flag to true
                                        found = true;

                                        // capture the position in the list
                                        record = i;

                                        // break the iteration
                                        break;
                                    } // end if

                                    else {

                                        // flag to false of not found
                                        found = false;
                                    } // end else

                                }// end for loop


                                // If the item master push key is already exist in the list then
                                if (found) {

                                    // Remove the push key and the view holder object
                                    String pushKeyTobeRemoved = mListOfDeletePushKeys.get(record);
                                    mListOfDeletePushKeys.remove(record);

                                    // Change the color back to normal
                                    if (mViewHolderMap.get(pushKeyTobeRemoved) != null) {
                                        mViewHolderMap.get(pushKeyTobeRemoved).mAdapterView.getmRoot().setBackgroundColor(getResources().getColor(R.color.white));
                                        mViewHolderMap.get(pushKeyTobeRemoved).mAdapterView.getmItemPopupMenu().setBackgroundColor(getResources().getColor(R.color.white));
                                        // Remove the push key and the view holder object from the HashMap
                                        mViewHolderMap.remove(pushKeyTobeRemoved);

                                    }

                                    // Pass the data to MainActivity
                                    ((DeleteMultiOpenSalesDetail) getActivity()).sendDeleteMultiOpenSalesDetail(mListOfDeletePushKeys, mViewHolderMap);

                                    /// If the list is empty then invoke finish()
                                    if (mListOfDeletePushKeys.size() == 0 && ((DeleteMultiOpenSalesDetail) getActivity()).getActionModeOpenSalesDetail() != null) {
                                        ((DeleteMultiOpenSalesDetail) getActivity()).getActionModeOpenSalesDetail().finish();
                                    }

                                } else {

                                    // If not found then add what the user just clicked into the list
                                    mListOfDeletePushKeys.add(pushKeyDetail);

                                    // If not found then add what the user just clicked into the hashMap
                                    mViewHolderMap.put(pushKeyDetail, viewHolder);

                                    // Change the color to
                                    if (mViewHolderMap.get(pushKeyDetail) != null) {
                                        mViewHolderMap.get(pushKeyDetail).mAdapterView.getmRoot().setBackgroundColor(getResources().getColor(R.color.purple_100));
                                        mViewHolderMap.get(pushKeyDetail).mAdapterView.getmItemPopupMenu().setBackgroundColor(getResources().getColor(R.color.purple_100));
                                    }

                                    // Pass the data to MainActivity
                                    ((DeleteMultiOpenSalesDetail) getActivity()).sendDeleteMultiOpenSalesDetail(mListOfDeletePushKeys, mViewHolderMap);

                                }
                            }

                            // Display the number of item selected on the toolbar
                            if (((DeleteMultiOpenSalesDetail) getActivity()).getActionModeOpenSalesDetail() != null) {
                                int size = mListOfDeletePushKeys.size();
                                ((DeleteMultiOpenSalesDetail) getActivity()).getActionModeOpenSalesDetail().setTitle(size + " Item(s) will be deleted");
                            }
                        }
                    }
                });
            }
        }


        @Override
        public int getItemCount() {
            return mSalesDetailList != null ? mSalesDetailList.size() : 0;
        }


        /**
         * This helper method will get and listen for the data from Firebase node
         */
        private void getDataFromFirebase() {

            /* Check if it is not null */
            if (mSalesDetailList != null) {

                /**
                 * This listener will get data from Firebase, then pass the data to an ArrayList
                 */
                detailListener = detailRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {

                        /* Get the model from the dataSnapshot */
                        SalesDetailModel model = dataSnapshot.getValue(SalesDetailModel.class);

                        if (model != null) {

                            /*  Pack the POJO into an ArrayList */
                            mSalesDetailList.add(model);

                            /* Pack the keys into an ArrayList */
                            mSalesDetailKeyList.add(dataSnapshot.getKey());

                            /* Notify this adapter for any data change */
                            notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {


                        /* Get the model from the dataSnapshot */
                        SalesDetailModel model = dataSnapshot.getValue(SalesDetailModel.class);

                        if (model != null) {

                            /* Get the key for the value that changed */
                            String keyChanged = dataSnapshot.getKey();

                            /* Find the index of that key */
                            int theIndexOfKey = mSalesDetailKeyList.indexOf(keyChanged);

                            /* Revise/Set the value for that index */
                            mSalesDetailList.set(theIndexOfKey, model);

                            /* Notify this adapter for any data changes */
                            notifyItemChanged(theIndexOfKey);
                        }


                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {


                        /* Check if not null */

                            /* Get the key */
                        String keyRemoved = dataSnapshot.getKey();

                            /* Find the index of that key */
                        int theIndexOfKey = mSalesDetailKeyList.indexOf(keyRemoved);

                            /* Remove the value from the list */
                        mSalesDetailList.remove(theIndexOfKey);

                            /* Remove the key from the list */
                        mSalesDetailKeyList.remove(theIndexOfKey);

                            /* Notify this adapter for any data changes */
                        notifyItemRemoved(theIndexOfKey);

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }


                });
            }
        }

        /**
         * Helper method to remove all listeners, this must be public
         * because it will be invoked later by
         */
        public void removeListeners() {
            if (detailRef != null && detailListener != null) {
                detailRef.removeEventListener(detailListener);
            }
        }
    }


    /**
     * View holder class
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.adapter_custom_view_for_open_sales_detail_list)
        AdapterViewOpenSalesDetailList mAdapterView;


        /**
         * Constructor
         *
         * @param itemView View object
         */
        public ViewHolder(View itemView) {
            super(itemView);

            // initialize the butter knife
            ButterKnife.bind(this, itemView);

        }
    }


    /**
     * This interface is callbacks to the {@link OpenSalesDetailActivity}
     * in related with ActionMode to delete multi items from the RV
     */
    public interface DeleteMultiOpenSalesDetail {


        /**
         * This method will pass the delete item to the {@link OpenSalesDetailActivity}
         * @param pushKeyList       The open sales detail push()
         * @param viewHolderList    The view holder object
         */
        void sendDeleteMultiOpenSalesDetail(ArrayList<String> pushKeyList, HashMap<String, ViewHolder> viewHolderList);

        /**
         * This method will activate the {@link ActionMode}
         * in the {@link OpenSalesDetailActivity}
         */
        void onTurnActionModeOnOpenSalesDetail(String headerKey);

        /**
         * This well get a reference to the {@link ActionMode} instance
         * in the {@link OpenSalesDetailActivity}
         * @return          {@link ActionMode} instance
         */
        android.view.ActionMode getActionModeOpenSalesDetail();

    }

}
