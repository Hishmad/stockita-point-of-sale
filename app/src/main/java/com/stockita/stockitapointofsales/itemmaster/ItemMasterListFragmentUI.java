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

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stockita.stockitapointofsales.R;
import com.stockita.stockitapointofsales.activities.MainActivity;
import com.stockita.stockitapointofsales.customViews.AdapterViewItemList;
import com.stockita.stockitapointofsales.data.ItemImageModel;
import com.stockita.stockitapointofsales.data.ItemModel;
import com.stockita.stockitapointofsales.interfaces.ItemMasterAddEditCallbacks;
import com.stockita.stockitapointofsales.utilities.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This fragment will display a list of item using the adapter from FirebaseUI
 */
public class ItemMasterListFragmentUI extends Fragment {


    // Constant
    private static final String TAG_LOG = ItemMasterListFragmentUI.class.getSimpleName();
    private static final String KEY_DATA_ENCODED_EMAIL = TAG_LOG + "KEY_DATA_ENCODED_EMAIL";
    private static final String KEY_DATA_USER_NAME = TAG_LOG + "KEY_DATA_USER_NAME";
    private static final String KEY_DATA_USER_UID = TAG_LOG + "KEY_DATA_USER_UID";


    /**
     * The user login email encoded to match
     * the server spec
     */
    private String mUserEncodedEmail;

    /**
     * The user name from the server
     */
    private String mUserNama;

    /**
     * The user UID from the server
     */
    private String mUserUid;

    /**
     * This is a {@link FirebaseRecyclerAdapter}
     * to populate the {@link ItemModel} from the server
     * into this list.
     */
    public MyAdapter mMyAdapter;


    // Views
    @Bind(R.id.item_master_list)
    RecyclerView mItemMasterList;


    /**
     * Empty constructor
     */
    public ItemMasterListFragmentUI() {
    }


    /**
     * This is when we instantiate the fragment and pass the data
     *
     * @param userEncodedEmail The user encoded email
     * @param userName         The user login name
     * @param userUid          The user login UID from the server
     * @return This fragment
     */
    public static ItemMasterListFragmentUI newInstance(String userEncodedEmail, String userName, String userUid) {

        // Instantiate this fragment
        ItemMasterListFragmentUI fragment = new ItemMasterListFragmentUI();

        // Instantiate the bundle
        Bundle bundle = new Bundle();

        // put the data in the bundle
        bundle.putString(KEY_DATA_ENCODED_EMAIL, userEncodedEmail);
        bundle.putString(KEY_DATA_USER_NAME, userName);
        bundle.putString(KEY_DATA_USER_UID, userUid);

        // pass the bundle to the fragment
        fragment.setArguments(bundle);

        // return the fragment
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the user encoded email from the activity
        mUserEncodedEmail = getArguments().getString(KEY_DATA_ENCODED_EMAIL);

        // Get the user name from the activity
        mUserNama = getArguments().getString(KEY_DATA_USER_NAME);

        // Get the user UID from the activity
        mUserUid = getArguments().getString(KEY_DATA_USER_UID);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Initialize the view
        final View view = inflater.inflate(R.layout.fragment_list_of_item_master, container, false);

        // Initialize the ButterKnife
        ButterKnife.bind(this, view);

        // Set more efficient
        mItemMasterList.setHasFixedSize(true);

        /**
         * Code below is for the number of span depend device (Phone / tablet)
         * and orientation (portrait / landscape)
         */
        int columnCount;
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        boolean isLandscape = getResources().getBoolean(R.bool.isLandscape);

        if (isTablet && isLandscape) {
            columnCount = 5;
        } else if (!isTablet && isLandscape) {
            columnCount = 3;
        } else if (isTablet && !isLandscape) {
            columnCount = 4;
        } else {
            columnCount = 2;
        }

        // Initialize the LayoutManager for item master
        GridLayoutManager layoutManager =
                new GridLayoutManager(getActivity(), columnCount, GridLayoutManager.VERTICAL, false);

        // Set the layout manager for the item master
        mItemMasterList.setLayoutManager(layoutManager);

        // Server reference location item master
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(mUserUid)
                .child(Constants.FIREBASE_ITEM_MASTER_LOCATION);

        // Initialize the adapter
        mMyAdapter = new MyAdapter(ItemModel.class, R.layout.adapter_each_card_in_item_list,
                ViewHolder.class, databaseReference, mUserUid) {

            @Override
            protected void populateViewHolder(final ViewHolder viewHolder, final ItemModel model, int position) {

                // Get the pushKey()
                DatabaseReference keyRef = getRef(viewHolder.getLayoutPosition());

                // Convert the keyRef to type String, then pass it to the UI
                final String pushKey = keyRef.getKey();

                // Get the item number
                String itemNumber = model.getItemNumber();

                // Get the item desc
                String itemDesc = model.getItemDesc();
                viewHolder.mAdapterView.getmItemDesc().setText(itemDesc);

                // Get Unit of measure
                String itemUnit = model.getUnitOfMeasure();

                // Get item price
                String itemPrice = model.getItemPrice();
                viewHolder.mAdapterView.getmItemPrice().setText(itemPrice);


                /**
                 * The code below for the popup menu in each element in the list
                 */
                viewHolder.mAdapterView.getmPopupMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        // If action mode then cancel before we can proceed with the click
                        if (((DeleteMultiItemMaster) getActivity()).getActionModeItemMaster() != null) {
                            ((DeleteMultiItemMaster) getActivity()).getActionModeItemMaster().finish();
                        }

                        // Get the menu item id
                        int id = menuItem.getItemId();

                        switch (id) {

                            // Delete
                            case R.id.menu_delete:

                                // Alert the user before delete.
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Warning")
                                        .setMessage("You are about to delete this item and all its images?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                /**
                                                 * Do the delete here...
                                                 * {@link ItemMasterIntentService#deleteOneItemMaster(Context, String, String)}
                                                 */
                                                ItemMasterIntentService.deleteOneItemMaster(getActivity(), mAdapterUserUid, pushKey);

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

                                /**
                                 * Pass the data to {@link MainActivity#onItemMasterEditDialogCallBacls}
                                 */
                                ((ItemMasterAddEditCallbacks) getActivity()).onItemMasterEditDialogCallBacks(Constants.REQUEST_CODE_DIALOG_ONE, mAdapterUserUid, pushKey, model);
                                return true;


                            // Add or replace image
                            case R.id.menu_image_edit:

                                /**
                                 * Pass the data to {@link MainActivity#onItemMasterEditDialogCallBacls}
                                 */
                                ((ItemMasterAddEditCallbacks) getActivity()).onItemMasterEditDialogCallBacks(Constants.REQUEST_CODE_DIALOG_TWO, mAdapterUserUid, pushKey, model);
                                return true;

                            // Take photo from the camera
                            case R.id.menu_take_photo:

                                /**
                                 * Pass the data to {@link MainActivity#onItemMasterEditDialogCallBacls}
                                 */
                                ((ItemMasterAddEditCallbacks) getActivity()).onItemMasterEditDialogCallBacks(Constants.REQUEST_CODE_DIALOG_THREE, mAdapterUserUid, pushKey, model);
                                return true;


                        }

                        return false;
                    }
                });


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

                            // Initialize the array list as a container for view holder objects
                            mViewHolderList = new ArrayList<>();

                            // Pass the pushKey for this current position
                            mListOfDeletePushKeys.add(pushKey);

                            // Pass the view holder instance for this current position
                            mViewHolderList.add(viewHolder);

                            // turn the color fot he item clicked by the user
                            viewHolder.mAdapterView.getmRoot().setBackgroundColor(getResources().getColor(R.color.purple_100));


                            // Pass the data to the MainActivity using callbacks
                            ((DeleteMultiItemMaster) getActivity()).sendDeleteMultiItemMaster(mListOfDeletePushKeys, mViewHolderList);

                            // Now turn the ActionMode on using callbacks
                            ((DeleteMultiItemMaster) getActivity()).onTurnActionModeOnItemMaster();


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

                                // Iterate for each element
                                for (int i = 0; i < mListOfDeletePushKeys.size(); i++) {

                                    // check if item master push() key is already exist
                                    if (pushKey.equals(mListOfDeletePushKeys.get(i))) {

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
                                    mListOfDeletePushKeys.remove(record);
                                    mViewHolderList.remove(record);

                                    // Change the color back to normal
                                    viewHolder.mAdapterView.getmRoot().setBackgroundColor(getResources().getColor(R.color.white));

                                    // Pass the data to MainActivity
                                    ((DeleteMultiItemMaster) getActivity()).sendDeleteMultiItemMaster(mListOfDeletePushKeys, mViewHolderList);

                                    /// If the list is empty then invoke finish()
                                    if (mListOfDeletePushKeys.size() == 0 && ((DeleteMultiItemMaster) getActivity()).getActionModeItemMaster() != null) {
                                        ((DeleteMultiItemMaster) getActivity()).getActionModeItemMaster().finish();
                                    }

                                } else {

                                    // If not found then add what the user just clicked into the list
                                    mListOfDeletePushKeys.add(pushKey);
                                    mViewHolderList.add(viewHolder);

                                    // Change the color to
                                    viewHolder.mAdapterView.getmRoot().setBackgroundColor(getResources().getColor(R.color.purple_100));

                                    // Pass the data to MainActivity
                                    ((DeleteMultiItemMaster) getActivity()).sendDeleteMultiItemMaster(mListOfDeletePushKeys, mViewHolderList);

                                }

                            }

                            // Display the number of item selected/deselected on the toolbar every time the user click
                            if (((DeleteMultiItemMaster) getActivity()).getActionModeItemMaster() != null) {

                                // simply just show the size of the list
                                int size = mListOfDeletePushKeys.size();

                                // Pass them to the MainActivity using this callback
                                ((DeleteMultiItemMaster) getActivity()).getActionModeItemMaster().setTitle(size + " Item(s) will be deleted");

                            }
                        }
                    }
                });


                /**
                 * Nested recycler view for item images
                 */


                // Get the reference to the Item Image
                DatabaseReference imageRef =
                        FirebaseDatabase.getInstance().getReference()
                                .child(mAdapterUserUid)
                                .child(Constants.FIREBASE_ITEM_MASTER_IMAGE_LOCATION)
                                .child(pushKey);


                // This adapter will display only image for each element that matches the item master pushKey
                mNestedImageAdapter =
                        new FirebaseRecyclerAdapter<ItemImageModel, NestedImageViewHolder>(ItemImageModel.class, R.layout.adapter_each_card_nested_image, NestedImageViewHolder.class, imageRef) {


                            @Override
                            protected void populateViewHolder(final NestedImageViewHolder viewHolder, ItemImageModel itemImageInstance, int position) {


                                // Get the pushKey() for this image in this position
                                DatabaseReference imageKeyRef = getRef(viewHolder.getLayoutPosition());

                                // Convert the keyRef to type String, then pass it to the UI
                                final String imageKeyRefString = imageKeyRef.getKey();


                                /**
                                 * Code below will find the image from the local or server then populate them in the UI
                                 */

                                // Initialize storage
                                StorageReference imageStorageRef = FirebaseStorage.getInstance().getReference()
                                        .child(mAdapterUserUid)
                                        .child(Constants.FIREBASE_ITEM_MASTER_IMAGE_LOCATION)
                                        .child(pushKey);

                                // Get the file path from the model
                                String filePath = itemImageInstance.getImageUrl();

                                // Get the file Uri
                                Uri file = Uri.fromFile(new File(filePath));

                                // Get the local file
                                File localFile = new File(filePath);


                                // Check if the image available in local
                                if (localFile.exists()) {
                                    try {
                                        // Display the image
                                        Glide.with(getActivity())
                                                .load("file://" + localFile)
                                                .centerCrop()
                                                .crossFade()
                                                .into(viewHolder.mItemImageNested);
                                    } catch (Exception e) {
                                        Log.e(TAG_LOG, e.getMessage());
                                    }


                                } else {


                                    // If not available in local then download from the server
                                    imageStorageRef.child(file.getLastPathSegment())
                                            .getDownloadUrl().addOnSuccessListener(getActivity(), new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            try {
                                                // Display the image
                                                Glide.with(getActivity())
                                                        .load(uri)
                                                        .centerCrop()
                                                        .crossFade()
                                                        .into(viewHolder.mItemImageNested);
                                            } catch (Exception e) {
                                                Log.e(TAG_LOG, e.getMessage());
                                            }

                                        }
                                    });

                                }


                                /**
                                 * Code below will delete one image in this position
                                 */
                                viewHolder.mItemImageNested.setOnLongClickListener(new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View view) {

                                        // Alert the user before delete.
                                        new AlertDialog.Builder(getActivity())
                                                .setTitle("Warning")
                                                .setMessage("One image will be deleted?")
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                        /**
                                                         * Call the {@link ItemMasterIntentService#deleteOneItemImage(Context, String, String, String)}
                                                         * to perform delete item image in the worker thread
                                                         */
                                                        ItemMasterIntentService.deleteOneItemImage(getActivity(), mAdapterUserUid, pushKey, imageKeyRefString);

                                                    }
                                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                // do nothing
                                            }
                                        }).show();


                                        return true;
                                    }
                                });

                            }

                            @Override
                            public void onBindViewHolder(NestedImageViewHolder holder, int position, List<Object> payloads) {
                                super.onBindViewHolder(holder, position, payloads);

                                // Add animation here..
                            }


                            @Override
                            public int getItemViewType(int position) {

                                // The layout for each element on the list
                                return R.layout.adapter_each_card_nested_image;

                            }


                            @Override

                            public NestedImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                                // Initialize the view object
                                View nested = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
                                return new NestedImageViewHolder(nested);

                            }

                        }; // End of nested image adapter


                // For the nested item image
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

                // For the nested item image
                viewHolder.mAdapterView.getmItemImage().setHasFixedSize(true);

                // For the nested item image
                viewHolder.mAdapterView.getmItemImage().setLayoutManager(linearLayoutManager);

                // For the nested item image
                viewHolder.mAdapterView.getmItemImage().setAdapter(mNestedImageAdapter);

            }


            @Override
            public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
                super.onBindViewHolder(holder, position, payloads);
                // Add animation here..
            }


            @Override
            public int getItemViewType(int position) {

                // The layout for each element in the list
                return R.layout.adapter_each_card_in_item_list;
            }


            /**
             * It will crash if we don't override this method, like the following code
             * @param parent        ViewGroup object
             * @param viewType      The layout xml frmo the method getItemViewType
             * @return View holder type
             */
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                // Initialize the view object
                View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

                // Return the view holder and pass the view object as argument
                return new ViewHolder(view);

            }
        };

        // Set the adapter for the item master
        mItemMasterList.setAdapter(mMyAdapter);


        // Return the view object
        return view;

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Cleanup the listeners
        if (mMyAdapter != null) {
            mMyAdapter.cleanup();
        }
    }


    /**
     * This method is to receive messages from the MainActivity rather than using
     * onActivityForResult().
     *
     * @param message any message
     */
    public void setAnyResult(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

    }


    /**
     * View holder class for Item master
     */
    public class ViewHolder extends RecyclerView.ViewHolder {


        // Declare the custom view adapter
        public AdapterViewItemList mAdapterView;

        public String string;

        /**
         * Constructor
         *
         * @param itemView Object
         */
        public ViewHolder(View itemView) {
            super(itemView);

            // Initialize the customer adapter
            mAdapterView = (AdapterViewItemList) itemView.findViewById(R.id.adapter_custom_view_for_item_list);
        }
    }


    /**
     * Nested view holder class for the item image
     */
    public class NestedImageViewHolder extends RecyclerView.ViewHolder {

        // Views
        @Bind(R.id.item_image_nested)
        ImageView mItemImageNested;


        /**
         * Constructor
         * @param itemView view object
         */
        public NestedImageViewHolder(View itemView) {
            super(itemView);

            // Initialize the butterKnife
            ButterKnife.bind(this, itemView);

        }
    }


    /**
     * Abstract class for the adapter
     */
    public abstract class MyAdapter extends FirebaseRecyclerAdapter<ItemModel, ViewHolder> {


        /**
         * The user UID
         */
        protected String mAdapterUserUid;

        /**
         * The Adapter for the nested RV for images
         */
        protected FirebaseRecyclerAdapter<ItemImageModel, NestedImageViewHolder> mNestedImageAdapter;

        /**
         * ArrayList type string as container to store item master's push() key
         * for all the element that the user select it to be deleted using the ActionMode
         */
        protected ArrayList<String> mListOfDeletePushKeys;

        /**
         * Container for the view holder that about to be delete
         */
        protected ArrayList<ItemMasterListFragmentUI.ViewHolder> mViewHolderList;

        /**
         * Boolean as flag if ActionMode is on or off.
         */
        public boolean isContextualMode = false;



        /**
         * constructor
         *
         * @param modelClass        {@link ItemModel} instance
         * @param modelLayout       The xml layout file
         * @param viewHolderClass   The view holder instance
         * @param ref               The database server ref
         * @param userUid           The user's UID
         */
        public MyAdapter(Class<ItemModel> modelClass, int modelLayout, Class<ViewHolder> viewHolderClass, Query ref, String userUid) {
            super(modelClass, modelLayout, viewHolderClass, ref);
            this.mAdapterUserUid = userUid;

        }

        @Override
        public void cleanup() {
            super.cleanup();
            if (mNestedImageAdapter != null) {
                mNestedImageAdapter.cleanup();
            }

        }
    }


    /**
     * This interface is callbacks to the {@link MainActivity}
     * in related with ActionMode to delete multi items from the RV
     */
    public interface DeleteMultiItemMaster {

        /**
         * This method will pass the delete item to the {@link MainActivity}
         * @param pushKeyList       The item master push()
         * @param viewHolderList    The view holder object
         */
        void sendDeleteMultiItemMaster(ArrayList<String> pushKeyList, ArrayList<ItemMasterListFragmentUI.ViewHolder> viewHolderList);

        /**
         * This method will activate the {@link android.view.ActionMode} in the {@link MainActivity}
         */
        void onTurnActionModeOnItemMaster();

        /**
         * This well get a reference to the {@link android.view.ActionMode} instance
         * in the {@link MainActivity}
         * @return          {@link android.view.ActionMode} instance
         */
        android.view.ActionMode getActionModeItemMaster();

    }

}
