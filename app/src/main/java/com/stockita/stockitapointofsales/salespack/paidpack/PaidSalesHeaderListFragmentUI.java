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

package com.stockita.stockitapointofsales.salespack.paidpack;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.stockita.stockitapointofsales.R;
import com.stockita.stockitapointofsales.customViews.AdapterViewSalesHeaderList;
import com.stockita.stockitapointofsales.data.SalesHeaderModel;
import com.stockita.stockitapointofsales.utilities.Constants;
import com.stockita.stockitapointofsales.utilities.ManageDateTime;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This class fragment will display the /paidSalesHeader/.. in to RV
 */
public class PaidSalesHeaderListFragmentUI extends Fragment {

    // Constant
    private static final String TAG_LOG = PaidSalesHeaderListFragmentUI.class.getSimpleName();
    private static final String KEY_ONE = TAG_LOG + ".KEY_ONE";

    /**
     * The user's UID
     */
    private String mUserUid;


    /**
     * The adapter that will populate data into the list
     */
    private MyAdapter mMyAdapter;

    /**
     * RV
     */
    @Bind(R.id.list_of_sales_header)
    RecyclerView mListOfSalesHeader;


    /**
     * Empty constructor
     */
    public PaidSalesHeaderListFragmentUI() {}

    /**
     * Get the data from the activity into here
     * @param userUid               The user's UID
     * @return                      This fragment object
     */
    public static PaidSalesHeaderListFragmentUI newInstance(String userUid) {

        PaidSalesHeaderListFragmentUI fragment = new PaidSalesHeaderListFragmentUI();
        Bundle args = new Bundle();
        args.putString(KEY_ONE, userUid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the user encoded email from the activity
        mUserUid = getArguments().getString(KEY_ONE);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Initialize the view
        final View view = inflater.inflate(R.layout.fragment_list_of_paid_sales_header, container, false);

        // Initialize the ButterKnife
        ButterKnife.bind(this, view);

        // Set more efficient
        mListOfSalesHeader.setHasFixedSize(true);

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
        mListOfSalesHeader.setLayoutManager(gridLayoutManager);

        // Server reference location sales detail pending ref
        DatabaseReference paidSalesHeaderRef = FirebaseDatabase.getInstance().getReference()
                .child(mUserUid)
                .child(Constants.FIREBASE_PAID_SALES_HEADER_LOCATION);

        mMyAdapter = new MyAdapter(SalesHeaderModel.class,
                R.layout.adapter_each_card_in_sales_header,
                ViewHolder.class, paidSalesHeaderRef, mUserUid) {


            @Override
            protected void populateViewHolder(final PaidSalesHeaderListFragmentUI.ViewHolder viewHolder, SalesHeaderModel model, int position) {

                // Get the pushKey()
                final DatabaseReference keyRef = getRef(viewHolder.getLayoutPosition());

                // Convert the keyRef to type String, then pass it to the UI
                final String pushKeyHeader = keyRef.getKey();

                // Get the field from the model
                String customerName = model.getCustomerName();
                String total = model.getGrandTotal();

                // Get the timeStamp
                HashMap<String, Object> timeStamp = model.getServerDate();

                long dateTimeLong;

                try {
                    dateTimeLong = (long) timeStamp.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
                } catch (Exception e) {
                    dateTimeLong = 0;
                }

                // Convert the long to date time format
                String displayDate = new ManageDateTime(dateTimeLong).getGivenTimeMillisInDateFormat();

                // update the UI
                viewHolder.mAdapterView.getmHeaderCustomerName().setText(customerName);
                viewHolder.mAdapterView.getmHeaderAmount().setText(total);
                viewHolder.mAdapterView.getmHeaderDate().setText(displayDate);

                // Change the menu title from "Edit Invoice" to "Sales Detail"
                viewHolder.mAdapterView.getmPopupMenu().getMenu().findItem(R.id.menu_edit).setTitle("Sales Detail");

                // Show menu item Payment Detail to visible
                viewHolder.mAdapterView.getmPopupMenu().getMenu().findItem(R.id.menu_payment_detail).setVisible(true);

                // Set the menu item click listener
                viewHolder.mAdapterView.getmPopupMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        // Get the menu item id
                        int id = menuItem.getItemId();

                        switch (id) {

                            // Delete all the header and the detail as well
                            case R.id.menu_delete:
                                // Alert the user before delete.
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Warning")
                                        .setMessage("You are about to delete this Sales and all its details?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                /**
                                                 * Do the delete here... all that have the same .../pushKeyHeader/... node
                                                 */

                                                // First we must delete the archive, so we need to get the yearMonth from the
                                                // paidSalesHeader timestamp
                                                DatabaseReference paidSalesHeaderRef = FirebaseDatabase.getInstance().getReference()
                                                        .child(mAdapterUserUid)
                                                        .child(Constants.FIREBASE_PAID_SALES_HEADER_LOCATION)
                                                        .child(pushKeyHeader);
                                                paidSalesHeaderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                        // Get the server date so we can know the node to the /yearMonth/ for each archive
                                                        SalesHeaderModel salesHeaderModel = dataSnapshot.getValue(SalesHeaderModel.class);
                                                        HashMap<String, Object> timeStamp = salesHeaderModel.getServerDate();
                                                        long serverDate = (long) timeStamp.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);

                                                        // Convert the server date from long millis to yearMonth format
                                                        String yearMonth = new ManageDateTime(serverDate).getGivenTimeMillisInYearMonth();

                                                        // Get to all archive, there are 3 /archivePayment/... & /archiveSalesDetail/... & /archiveSalesHeader/...
                                                        DatabaseReference archive = FirebaseDatabase.getInstance().getReference()
                                                                .child(mAdapterUserUid);

                                                        // Delete that match the pushKeyHeader
                                                        HashMap<String, Object> deleteArchive = new HashMap<>();
                                                        deleteArchive.put("/" + Constants.FIREBASE_ARCHIVE_SALES_HEADER_LOCATION + "/" + yearMonth + "/" + pushKeyHeader, null);
                                                        deleteArchive.put("/" + Constants.FIREBASE_ARCHIVE_SALES_DETAIL_LOCATION + "/" + yearMonth + "/" + pushKeyHeader, null);
                                                        deleteArchive.put("/" + Constants.FIREBASE_ARCHIVE_PAYMENT_LOCATION + "/" + yearMonth + "/" + pushKeyHeader, null);
                                                        archive.updateChildren(deleteArchive);

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });


                                                // Once we already deleted all the archive related to this invoice, now we can delete the paid... node
                                                DatabaseReference deleteAll = FirebaseDatabase.getInstance().getReference()
                                                        .child(mAdapterUserUid);

                                                HashMap<String, Object> allInOne = new HashMap<>();
                                                allInOne.put("/" + Constants.FIREBASE_PAID_SALES_DETAIL_LOCATION + "/" + pushKeyHeader, null);
                                                allInOne.put("/" + Constants.FIREBASE_PAID_SALES_HEADER_LOCATION + "/" + pushKeyHeader, null);
                                                allInOne.put("/" + Constants.FIREBASE_PAYMENT_LOCATION + "/" + pushKeyHeader, null);

                                                // Delete them all
                                                deleteAll.updateChildren(allInOne);


                                            }
                                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // do nothing
                                    }
                                }).show();
                                return true;

                            case R.id.menu_edit:

                                // This will call the PaidSalesDetailActivity
                                ((PaidSalesHeaderListInterface) getActivity()).callPaidSalesDetailActivity(mAdapterUserUid, pushKeyHeader);
                                return true;

                            case R.id.menu_payment_detail:

                                // This will call the PaymentActivity
                                ((PaidSalesHeaderListInterface) getActivity()).callPaymentActivity(mAdapterUserUid, pushKeyHeader);
                                return true;
                        }

                        return false;
                    }
                });
            }


            @Override
            public void onBindViewHolder(PaidSalesHeaderListFragmentUI.ViewHolder viewHolder, int position) {
                super.onBindViewHolder(viewHolder, position);
            }


            @Override
            public int getItemViewType(int position) {
                return R.layout.adapter_each_card_in_sales_header;
            }


            @Override
            public PaidSalesHeaderListFragmentUI.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                // Initialize the view object
                View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

                // Return the view holder and pass the view object as argument
                return new ViewHolder(view);
            }
        };

        // Set the adapter for the item master
        mListOfSalesHeader.setAdapter(mMyAdapter);

        // Return the view
        return view;

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Remove listeners
        if (mMyAdapter != null) {
            mMyAdapter.cleanup();
        }

    }

    /**
     * This class is for the Sales Header list view holder
     */
    public class ViewHolder extends RecyclerView.ViewHolder {


        // Custom view
        @Bind(R.id.adapter_custom_view_for_sales_header_list)
        AdapterViewSalesHeaderList mAdapterView;


        /**
         * Constructor
         * @param itemView      {@link View} object
         */
        public ViewHolder(View itemView) {
            super(itemView);

            // Initialize the butterKnife
            ButterKnife.bind(this, itemView);
        }


        /**
         * Getter method
         * @return      {@link AdapterViewSalesHeaderList} object
         */
        public AdapterViewSalesHeaderList getmAdapterView() {
            return mAdapterView;
        }
    }


    /**
     * This is an abstract class for the FirebaseUI recycler view adapter
     */
    public abstract class MyAdapter extends FirebaseRecyclerAdapter<SalesHeaderModel, PaidSalesHeaderListFragmentUI.ViewHolder> {


        /**
         * User's UID
         */
        protected String mAdapterUserUid;

        /**
         * Constructor matching super ++
         * @param modelClass            The {@link SalesHeaderModel}
         * @param modelLayout           Resource layout xml
         * @param viewHolderClass       The view holder
         * @param ref                   The database ref to node
         * @param userUid               The user UID
         */
        public MyAdapter(Class<SalesHeaderModel> modelClass, int modelLayout, Class<ViewHolder> viewHolderClass, Query ref, String userUid) {
            super(modelClass, modelLayout, viewHolderClass, ref);
            mAdapterUserUid = userUid;

        }
    }


    /**
     * Interface to callback on the {@link com.stockita.stockitapointofsales.activities.SecondActivity} for
     * {@link PaidSalesDetailActivity} and {@link PaymentActivity}
     */
    public interface PaidSalesHeaderListInterface {
        void callPaidSalesDetailActivity(String encodedEmail, String paidSalesHeaderKey);
        void callPaymentActivity(String encodedEmail, String paidSalesHeaderKey);
    }



}
