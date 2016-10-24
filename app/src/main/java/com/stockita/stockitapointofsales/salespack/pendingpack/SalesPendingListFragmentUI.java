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

package com.stockita.stockitapointofsales.salespack.pendingpack;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.stockita.stockitapointofsales.R;
import com.stockita.stockitapointofsales.activities.MainActivity;
import com.stockita.stockitapointofsales.customViews.AdapterViewSalesDetailList;
import com.stockita.stockitapointofsales.data.ContractData;
import com.stockita.stockitapointofsales.data.SalesDetailModel;
import com.stockita.stockitapointofsales.interfaces.SalesDetailPendingCallbacks;
import com.stockita.stockitapointofsales.itemmaster.ItemMasterListFragmentUI;
import com.stockita.stockitapointofsales.utilities.Constants;
import com.stockita.stockitapointofsales.utilities.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.R.attr.action;
import static com.google.android.gms.auth.api.credentials.PasswordSpecification.da;


/**
 * This fragment will display a sales transaction
 */
public class SalesPendingListFragmentUI extends Fragment implements Toolbar.OnMenuItemClickListener {


    // Constant
    private static final String TAG_LOG = SalesPendingListFragmentUI.class.getSimpleName();
    private static final String KEY_DATA_ENCODED_EMAIL = TAG_LOG + ".KEY_DATA_ENCODED_EMAIL";
    private static final String KEY_DATA_USER_NAME = TAG_LOG + ".KEY_DATA_USER_NAME";
    private static final String KEY_DATA_USER_UID = TAG_LOG + ".KEY_DATA_USER_UID";

    // Member variables
    private String mUserUid;
    public MyAdapter mMyAdapter;

    private DatabaseReference mCashCalc;
    private ValueEventListener mCashCalcListener;


    // Views
    @Bind(R.id.cash_toolbar)
    Toolbar mCashToolbar;
    @Bind(R.id.total_toolbar)
    TextView mTotalToolbar;
    @Bind(R.id.list_of_sales_detail)
    RecyclerView mListOfSalesDetail;


    /**
     * Empty constructor
     */
    public SalesPendingListFragmentUI() {

    }


    /**
     * This is when we instantiate the fragment and pass the data
     *
     * @param userEncodedEmail The user encoded email
     * @param userName         The user login name
     * @param userUid          The user login UID from the server
     * @return This fragment
     */
    public static SalesPendingListFragmentUI newInstance(String userEncodedEmail, String userName, String userUid) {

        // Instantiate this fragment
        SalesPendingListFragmentUI fragmentUI = new SalesPendingListFragmentUI();

        // Instantiate the bundle
        Bundle bundle = new Bundle();

        // put the data in the bundle
        bundle.putString(KEY_DATA_ENCODED_EMAIL, userEncodedEmail);
        bundle.putString(KEY_DATA_USER_NAME, userName);
        bundle.putString(KEY_DATA_USER_UID, userUid);

        // pass the bundle to the fragment
        fragmentUI.setArguments(bundle);

        // return the fragment
        return fragmentUI;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the user encoded email from the activity
        mUserUid = getArguments().getString(KEY_DATA_USER_UID);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        // Initialize the view
        final View view = inflater.inflate(R.layout.fragment_list_of_pending_sales, container, false);

        // Initialize the ButterKnife
        ButterKnife.bind(this, view);


        /**
         * The code below is for the sales detail recycler view
         */

        // Set more efficient
        mListOfSalesDetail.setHasFixedSize(true);


        // Initialize the cash toolbar
        initCashToolbar();

        // Server reference location sales detail pending ref
        DatabaseReference salesDetailPendingRef = FirebaseDatabase.getInstance().getReference()
                .child(mUserUid)
                .child(Constants.FIREBASE_SALES_DETAIL_PENDING_LOCATION);


        // Initialize the adapter
        mMyAdapter = new MyAdapter(SalesDetailModel.class,
                R.layout.adapter_each_card_in_sales_detail,
                ViewHolder.class, salesDetailPendingRef, mUserUid) {

            @Override
            protected void populateViewHolder(final ViewHolder viewHolder, final SalesDetailModel model, int position) {

                // Get the pushKey()
                DatabaseReference keyRef = getRef(viewHolder.getLayoutPosition());

                // Convert the keyRef to type String, then pass it to the UI
                final String pushKeyDetail = keyRef.getKey();


                // Get the field from the model
                //String itemNumber = model.getItemNumber();
                String itemDesc = model.getItemDesc();
                //String itemUnit = model.getItemUnit();
                String itemPrice = model.getItemPrice();
                String itemQuantity = model.getItemQuantity();
                //String itemDiscount = model.getItemDiscount();
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

                        /**
                         * If action mode then cancel before we can proceed with the click
                         * Here we use {@link com.stockita.stockitapointofsales.itemmaster.ItemMasterListFragmentUI.DeleteMultiItemMaster}
                         * to avoid redundancy.
                         * */
                        if (((ItemMasterListFragmentUI.DeleteMultiItemMaster) getActivity()).getActionModeItemMaster() != null) {
                            ((ItemMasterListFragmentUI.DeleteMultiItemMaster) getActivity()).getActionModeItemMaster().finish();
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
                                                        .child(Constants.FIREBASE_SALES_DETAIL_PENDING_LOCATION)
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
                                ((SalesDetailPendingCallbacks) getActivity()).onSalesEditDialogCallbacks(Constants.REQUEST_CODE_SALES_DIALOG_ONE, mAdapterUserUid, pushKeyDetail, null, model);

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
                 * when the user long click it will fire the ActionMenu to perform
                 * multi item delete.
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


                            // Change the color for the item clicked by the user
                            viewHolder.mAdapterView.getmRoot().setBackgroundColor(getResources().getColor(R.color.purple_100));
                            viewHolder.mAdapterView.getmItemPopupMenu().setBackgroundColor(getResources().getColor(R.color.purple_100));

                            // Pass the data to the MainActivity using callbacks
                            ((DeleteMultiSalesPending) getActivity()).sendDeleteMultiSalesPending(mListOfDeletePushKeys, mViewHolderMap);

                            // Now turn the ActionMode on using callbacks
                            ((DeleteMultiSalesPending) getActivity()).onTurnActionModeOnSalesPending();


                            return true;
                        }

                        return false;
                    }
                });


                /**
                 * When it is already in Action Mode menu the single click will add or remove
                 * item from the list to be delete.
                 */
                viewHolder.mAdapterView.getmRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // Check if the ActionMode is on
                        if (isContextualMode) {

                            // initialize local variables
                            boolean found = false;
                            int record = 0;

                            // check if the pushList is not 0
                            if (mListOfDeletePushKeys.size() > 0) {

                                // Itarate for each element
                                for (int i = 0; i < mListOfDeletePushKeys.size(); i++) {

                                    // check if item master push() key is already exist
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
                                    ((DeleteMultiSalesPending) getActivity()).sendDeleteMultiSalesPending(mListOfDeletePushKeys, mViewHolderMap);

                                    /**
                                     * If the list is empty then invoke finish()
                                     * reuse {@link com.stockita.stockitapointofsales.itemmaster.ItemMasterListFragmentUI.DeleteMultiItemMaster}
                                     * to avoid redundancy.
                                     * */
                                    if (mListOfDeletePushKeys.size() == 0 && ((ItemMasterListFragmentUI.DeleteMultiItemMaster) getActivity()).getActionModeItemMaster() != null) {
                                        ((ItemMasterListFragmentUI.DeleteMultiItemMaster) getActivity()).getActionModeItemMaster().finish();
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
                                    ((DeleteMultiSalesPending) getActivity()).sendDeleteMultiSalesPending(mListOfDeletePushKeys, mViewHolderMap);

                                }

                            }

                            /**
                             * Display the number of item selected on the toolbar
                             * reuse {@link com.stockita.stockitapointofsales.itemmaster.ItemMasterListFragmentUI.DeleteMultiItemMaster
                             * to avoid redundency.
                             */
                            if (((ItemMasterListFragmentUI.DeleteMultiItemMaster) getActivity()).getActionModeItemMaster() != null) {
                                int size = mListOfDeletePushKeys.size();
                                ((ItemMasterListFragmentUI.DeleteMultiItemMaster) getActivity()).getActionModeItemMaster().setTitle(size + " Item(s) will be deleted");

                            }

                        }
                    }
                });

            }

            @Override
            public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
                super.onBindViewHolder(holder, position, payloads);
                // For animation
            }

            @Override
            public int getItemViewType(int position) {
                return R.layout.adapter_each_card_in_sales_detail;
            }

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


                // Initialize the view object
                View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

                // Return the view holder and pass the view object as argument
                return new ViewHolder(view);

            }
        };


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

        // Set the layout manager for the item master
        mListOfSalesDetail.setLayoutManager(gridLayoutManager);

        // Set the adapter for the item master
        mListOfSalesDetail.setAdapter(mMyAdapter);

        // Return view object
        return view;
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
                .child(mUserUid).child(Constants.FIREBASE_SALES_DETAIL_PENDING_LOCATION);

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
                Utility.setAnyString(getActivity(), "checkout_total", String.valueOf(calculate));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG_LOG, databaseError.getMessage());

            }
        };

        // Attach the listener
        mCashCalc.addValueEventListener(mCashCalcListener);

    }


    /**
     * The cash toolbar item click
     *
     * @param item Menu item
     * @return boolean
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_cash_checkout:

                // TODO add check before proceed

                // Go to the checkout dialog
                packTheCurrentSalesDetailPendingInToLocalDatabase();

                // Get the sales detail pending total from the toolbar
                String total = mTotalToolbar.getText().toString();

                //Pass the data to the activity then to the SalesPendingCheckoutDialogFragment
                ((SalesDetailPendingCallbacks) getActivity()).getSalesCheckoutDialog(mUserUid, total);


                return true;
        }

        return false;
    }


    /**
     * This method will insert the current Sales Detail Pending into a local database
     * so later can be query by {@link SalesPendingCheckoutDialogFragment}
     */
    private void packTheCurrentSalesDetailPendingInToLocalDatabase() {

        // Delete all data in the local database before we insert new data
        final ContentResolver contentResolver = getActivity().getContentResolver();
        contentResolver.delete(ContractData.SalesDetailPendingEntry.CONTENT_URI, null, null);

        // Get the reference to ../SalesDetailPending/...
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(mUserUid).child(Constants.FIREBASE_SALES_DETAIL_PENDING_LOCATION);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChildren()) {
                    return;
                }

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


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Remove listeners
        if (mMyAdapter != null) {
            mMyAdapter.cleanup();
        }

        // Remove listeners
        if (mCashCalc != null && mCashCalcListener != null) {
            mCashCalc.removeEventListener(mCashCalcListener);
        }

    }


    /**
     * This class is for the Sales detail list view holder
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.adapter_custom_view_for_sales_detail_list)
        AdapterViewSalesDetailList mAdapterView;

        /**
         * Constrictor
         *
         * @param itemView View object
         */
        public ViewHolder(View itemView) {
            super(itemView);

            // Initialize the butterKnife
            ButterKnife.bind(this, itemView);
        }


        /**
         * Getter method
         *
         * @return {@link AdapterViewSalesDetailList} instance
         */
        public AdapterViewSalesDetailList getAdapterView() {
            return mAdapterView;
        }
    }


    /**
     * This is an abstract class for the FirebaseUI recycler view adapter
     */
    public abstract class MyAdapter extends FirebaseRecyclerAdapter<SalesDetailModel, SalesPendingListFragmentUI.ViewHolder> {

        /**
         * The user UID
         */
        protected String mAdapterUserUid;

        /**
         * ArrayList type string as container to store sales detail's push() key
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
         * Constructor matching super
         *
         * @param modelClass      {@link SalesDetailModel} instance
         * @param modelLayout     The resource layout xml
         * @param viewHolderClass View holder
         * @param ref             Database node
         * @param userUid         The user UID
         */
        public MyAdapter(Class<SalesDetailModel> modelClass, int modelLayout,
                         Class<ViewHolder> viewHolderClass, Query ref, String userUid) {

            super(modelClass, modelLayout, viewHolderClass, ref);
            this.mAdapterUserUid = userUid;
        }
    }

    /**
     * This interface is callbacks to the {@link MainActivity}
     * in related with ActionMode to delete multi items from the RV
     */
    public interface DeleteMultiSalesPending {


        /**
         * This method will pass the delete item to the {@link MainActivity}
         *
         * @param pushKeyList       Container for sales detail push() key
         * @param viewHolderHashMap Container for view holder
         */
        void sendDeleteMultiSalesPending(ArrayList<String> pushKeyList, HashMap<String, ViewHolder> viewHolderHashMap);


        /**
         * This method will activate the {@link android.support.v7.view.ActionMode} in the {@link MainActivity}
         */
        void onTurnActionModeOnSalesPending();

    }


}
