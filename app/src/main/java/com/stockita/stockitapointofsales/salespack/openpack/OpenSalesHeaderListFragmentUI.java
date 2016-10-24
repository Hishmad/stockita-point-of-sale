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
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.stockita.stockitapointofsales.R;
import com.stockita.stockitapointofsales.activities.MainActivity;
import com.stockita.stockitapointofsales.customViews.AdapterViewSalesHeaderList;
import com.stockita.stockitapointofsales.data.SalesHeaderModel;
import com.stockita.stockitapointofsales.interfaces.OpenSalesHeaderListCallbacks;
import com.stockita.stockitapointofsales.salespack.pendingpack.SalesPendingListFragmentUI;
import com.stockita.stockitapointofsales.utilities.Constants;
import com.stockita.stockitapointofsales.utilities.ManageDateTime;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This will display RV list from /openSalesHeader node
 */
public class OpenSalesHeaderListFragmentUI extends Fragment {

    // Constants
    private static final String TAG_LOG = OpenSalesHeaderListFragmentUI.class.getSimpleName();
    private static final String KEY_ONE = TAG_LOG + ".KEY_ONE";


    /**
     * The user's UID
     */
    private String mUserUid;


    /**
     * The adapter that will populate data into the list
     */
    public MyAdapter mMyAdapter;


    /**
     * View object
     */
    @Bind(R.id.list_of_sales_header)
    RecyclerView mListOfSalesHeader;


    /**
     * Empty constructor
     */
    public OpenSalesHeaderListFragmentUI() {}


    /**
     * Get the data from the activity into here
     * @param userUid               The user's UID
     * @return                      This fragment object
     */
    public static OpenSalesHeaderListFragmentUI newInstance(String userUid) {

        OpenSalesHeaderListFragmentUI fragment = new OpenSalesHeaderListFragmentUI();
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
        View view = inflater.inflate(R.layout.fragment_list_of_open_sales_header, container, false);

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
        DatabaseReference openSalesHeaderRef = FirebaseDatabase.getInstance().getReference()
                .child(mUserUid)
                .child(Constants.FIREBASE_OPEN_SALES_HEADER_LOCATION);


        // Initialize the adapter
        mMyAdapter = new MyAdapter(SalesHeaderModel.class,
                R.layout.adapter_each_card_in_sales_header,
                ViewHolder.class, openSalesHeaderRef, mUserUid) {


            @Override
            protected void populateViewHolder(final ViewHolder viewHolder, SalesHeaderModel model, int position) {

                // Get the pushKey()
                final DatabaseReference keyRef = getRef(viewHolder.getLayoutPosition());

                // Convert the keyRef to type String, then pass it to the UI
                final String pushKeyHeader = keyRef.getKey();

                // Get the field from the model
                String customerName = model.getCustomerName();
                String total = model.getTotalAmount();

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

                /**
                 * The below for the popup menu in each element in the list
                 */
                viewHolder.mAdapterView.getmPopupMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

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
                                                 * Do the delete here...
                                                 */

                                                // Get the database reference
                                                DatabaseReference openSalesRef = FirebaseDatabase.getInstance().getReference()
                                                        .child(mAdapterUserUid);

                                                // Delete that match the pushKeyHeader
                                                HashMap<String, Object> deleteOpenSales = new HashMap<>();
                                                deleteOpenSales.put("/" + Constants.FIREBASE_OPEN_SALES_DETAIL_LOCATION + "/" + pushKeyHeader , null);
                                                deleteOpenSales.put("/" + Constants.FIREBASE_OPEN_SALES_HEADER_LOCATION + "/" + pushKeyHeader , null);
                                                openSalesRef.updateChildren(deleteOpenSales);


                                            }
                                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // do nothing
                                    }
                                }).show();
                                return true;

                            case R.id.menu_edit:

                                /**
                                 * Call the {@link OpenSalesDetailActivity} via
                                 * {@link MainActivity#callSalesDetailActivity}
                                 */
                                ((OpenSalesHeaderListCallbacks) getActivity()).callSalesDetailActivity(mAdapterUserUid, pushKeyHeader);
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
                    if (mViewHolderMap.get(pushKeyHeader) == null) {
                        viewHolder.mAdapterView.getmRoot().setBackgroundColor(getResources().getColor(R.color.white));
                        viewHolder.mAdapterView.getmHeaderPopupMenu().setBackgroundColor(getResources().getColor(R.color.white));
                    } else {
                        viewHolder.mAdapterView.getmRoot().setBackgroundColor(getResources().getColor(R.color.purple_100));
                        viewHolder.mAdapterView.getmHeaderPopupMenu().setBackgroundColor(getResources().getColor(R.color.purple_100));

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
                            mViewHolderMap = new HashMap<String, ViewHolder>();

                            // Pass the pushKey for this current position
                            mListOfDeletePushKeys.add(pushKeyHeader);

                            // Pass the view holder instance as a value, and pass the pushKeyDetail as the key
                            mViewHolderMap.put(pushKeyHeader, viewHolder);

                            // turn the color fot he item clicked by the user
                            viewHolder.mAdapterView.getmRoot().setBackgroundColor(getResources().getColor(R.color.purple_100));
                            viewHolder.mAdapterView.getmHeaderPopupMenu().setBackgroundColor(getResources().getColor(R.color.purple_100));

                            // Pass the data to the MainActivity using callbacks
                            ((DeleteMultiOpenSalesHeader) getActivity()).sendDeleteMultiOpenSalesHeader(mListOfDeletePushKeys, mViewHolderMap);

                            // Now turn the ActionMode on using callbacks
                            ((DeleteMultiOpenSalesHeader) getActivity()).onTurnActionModeOnOpenSalesHeader();

                            return true;
                        }

                        return false;
                    }
                });


                /**
                 * When the user single click on an item
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
                                    if (pushKeyHeader.equals(mListOfDeletePushKeys.get(i))) {

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
                                        mViewHolderMap.get(pushKeyTobeRemoved).mAdapterView.getmHeaderPopupMenu().setBackgroundColor(getResources().getColor(R.color.white));

                                        // Remove the push key and the view holder object from the HashMap
                                        mViewHolderMap.remove(pushKeyTobeRemoved);

                                    }

                                    // Pass the data to MainActivity
                                    ((DeleteMultiOpenSalesHeader) getActivity()).sendDeleteMultiOpenSalesHeader(mListOfDeletePushKeys, mViewHolderMap);

                                    /// If the list is empty then invoke finish()
                                    if (mListOfDeletePushKeys.size() == 0 && ((DeleteMultiOpenSalesHeader) getActivity()).getActionModeOpenSalesHeader() != null) {
                                        ((DeleteMultiOpenSalesHeader) getActivity()).getActionModeOpenSalesHeader().finish();
                                    }

                                } else {

                                    // If not found then add what the user just clicked into the list
                                    mListOfDeletePushKeys.add(pushKeyHeader);

                                    // If not found then add what the user just clicked into the hashMap
                                    mViewHolderMap.put(pushKeyHeader, viewHolder);


                                    // Change the color to
                                    if (mViewHolderMap.get(pushKeyHeader) != null) {
                                        mViewHolderMap.get(pushKeyHeader).mAdapterView.getmRoot().setBackgroundColor(getResources().getColor(R.color.purple_100));
                                        mViewHolderMap.get(pushKeyHeader).mAdapterView.getmHeaderPopupMenu().setBackgroundColor(getResources().getColor(R.color.purple_100));
                                    }

                                    // Pass the data to MainActivity
                                    ((DeleteMultiOpenSalesHeader) getActivity()).sendDeleteMultiOpenSalesHeader(mListOfDeletePushKeys, mViewHolderMap);
                                }
                            }

                            // Display the number of item selected on the toolbar
                            if (((DeleteMultiOpenSalesHeader) getActivity()).getActionModeOpenSalesHeader() != null) {
                                int size = mListOfDeletePushKeys.size();
                                ((DeleteMultiOpenSalesHeader) getActivity()).getActionModeOpenSalesHeader().setTitle(size + " Item(s) will be deleted");
                            }
                        }
                    }
                });
            }


            @Override
            public void onBindViewHolder(ViewHolder viewHolder, int position) {
                super.onBindViewHolder(viewHolder, position);

                // for animation
            }


            @Override
            public int getItemViewType(int position) {
                return R.layout.adapter_each_card_in_sales_header;
            }


            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                // Initialize the view object
                View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

                // Return the view holder and pass the view object as argument
                return new ViewHolder(view);

            }
        };

        // Set the adapter for the item master
        mListOfSalesHeader.setAdapter(mMyAdapter);

        // Return view object
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
         * public getter
         * @return          {@link AdapterViewSalesHeaderList} object
         */
        public AdapterViewSalesHeaderList getmAdapterView() {
            return mAdapterView;
        }


    }

    /**
     * This is an abstract class for the FirebaseUI recycler view adapter
     */
    public abstract class MyAdapter extends FirebaseRecyclerAdapter<SalesHeaderModel, OpenSalesHeaderListFragmentUI.ViewHolder> {

        /**
         * The user's  UID
         */
        protected String mAdapterUserUid;

        /**
         * ArrayList type string as container to store sales header's push() key
         * for all the element that the user select it to be deleted using the ActionMode
         */
        protected ArrayList<String> mListOfDeletePushKeys;

        /**
         * Container for the view holder that about to be delete
         */
        protected HashMap<String, OpenSalesHeaderListFragmentUI.ViewHolder> mViewHolderMap;

        /**
         * Boolean as flag if ActionMode is on or off.
         */
        public boolean isContextualMode = false;

        /**
         * Constructor
         */
        public MyAdapter(Class<SalesHeaderModel> modelClass, int modelLayout,
                         Class<ViewHolder> viewHolderClass, Query ref, String userUid) {

            super(modelClass, modelLayout, viewHolderClass, ref);
            this.mAdapterUserUid = userUid;

        }
    }


    /**
     * This interface is callbacks to the {@link MainActivity}
     * in related with ActionMode to delete multi items from the RV
     */
    public interface DeleteMultiOpenSalesHeader{

        /**
         * This method will pass the delete item to the {@link MainActivity}
         * @param pushKeyList                  Container for sales header push() key
         * @param viewHolderHashMap            Container for view holder
         */
        void sendDeleteMultiOpenSalesHeader(ArrayList<String> pushKeyList, HashMap<String, OpenSalesHeaderListFragmentUI.ViewHolder> viewHolderHashMap);

        /**
         * This method will activate the {@link android.support.v7.view.ActionMode} in the {@link MainActivity}
         */
        void onTurnActionModeOnOpenSalesHeader();

        /**
         * This well get a reference to the {@link ActionMode} instance
         * in the {@link MainActivity}
         * @return          {@link ActionMode} instance
         */
        android.view.ActionMode getActionModeOpenSalesHeader();

    }
}
