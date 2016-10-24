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

package com.stockita.stockitapointofsales.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v13.app.ActivityCompat;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.view.ActionMode;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.desmond.squarecamera.CameraActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stockita.stockitapointofsales.R;
import com.stockita.stockitapointofsales.customViews.ActivityViewMainActivity;
import com.stockita.stockitapointofsales.data.ItemImageModel;
import com.stockita.stockitapointofsales.data.ItemModel;
import com.stockita.stockitapointofsales.data.SalesDetailModel;
import com.stockita.stockitapointofsales.data.SalesHeaderModel;
import com.stockita.stockitapointofsales.interfaces.ItemMasterAddEditCallbacks;
import com.stockita.stockitapointofsales.interfaces.OpenSalesHeaderListCallbacks;
import com.stockita.stockitapointofsales.interfaces.SalesDetailPendingCallbacks;
import com.stockita.stockitapointofsales.itemmaster.ItemMasterEditFormDialogFragment;
import com.stockita.stockitapointofsales.itemmaster.ItemMasterAddFormDialogFragment;
import com.stockita.stockitapointofsales.itemmaster.ItemMasterIntentService;
import com.stockita.stockitapointofsales.itemmaster.ItemMasterListFragmentUI;
import com.stockita.stockitapointofsales.salespack.openpack.OpenSalesDetailActivity;
import com.stockita.stockitapointofsales.salespack.openpack.OpenSalesHeaderListFragmentUI;
import com.stockita.stockitapointofsales.salespack.pendingpack.SalesPendingPayButtonIntentService;
import com.stockita.stockitapointofsales.salespack.pendingpack.SalesPendingAddFormDialogFragment;
import com.stockita.stockitapointofsales.salespack.pendingpack.SalesPendingCheckoutDialogFragment;
import com.stockita.stockitapointofsales.salespack.pendingpack.SalesPendingEditFormDialogFragment;
import com.stockita.stockitapointofsales.salespack.pendingpack.SalesPendingListFragmentUI;
import com.stockita.stockitapointofsales.utilities.Constants;
import com.stockita.stockitapointofsales.utilities.SettingsActivity;
import com.stockita.stockitapointofsales.utilities.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

/**
 * This is where the user will get in here after signing in, this class host three fragment
 * because using ViewPager {@link MainActivity.SectionsPagerAdapter}
 */
public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, ItemMasterAddEditCallbacks,
        SalesDetailPendingCallbacks, OpenSalesHeaderListCallbacks,
        SalesPendingCheckoutDialogFragment.PaymentButtonFromSalesPendingCheckoutDialogFragment,
        ItemMasterListFragmentUI.DeleteMultiItemMaster, SalesPendingListFragmentUI.DeleteMultiSalesPending,
        OpenSalesHeaderListFragmentUI.DeleteMultiOpenSalesHeader {


    /**
     * Tag
     */
    private static final String TAG_LOG = MainActivity.class.getSimpleName();

    /**
     * This constant is used by the instance of {@link ItemMasterAddFormDialogFragment}
     */
    private static final String FRAGMENT_DIALOG_ITEM_MASTER_FORM =
            "fragment_dialog_item_master_form";

    /**
     * This constant is used by the instance of {@link ItemMasterEditFormDialogFragment}
     */
    private static final String FRAGMENT_DIALOG_ITEM_MASTER_EDIT =
            "FRAGMENT_DIALOG_ITEM_MASTER_EDIT";


    /**
     * This constant is used by the instance of {@link SalesPendingEditFormDialogFragment}
     */
    private static final String FRAGMENT_DIALOG_SALES_DETAIL_PENDING_EDIT =
            "FRAGMENT_DIALOG_SALES_DETAIL_PENDING_EDIT";

    /**
     * This constant is used by the instance of {@link SalesPendingAddFormDialogFragment}
     */
    private static final String FRAGMENT_DIALOG_SALES_DETAIL_PENDING_ADD =
            "FRAGMENT_DIALOG_SALES_DETAIL_PENDING_ADD";

    /**
     * This constant is used by the instance of {@link SalesPendingCheckoutDialogFragment}
     */
    private static final String FRAGMENT_DIALOG_SALES_DETAIL_PENDING_CHECKOUT =
            "FRAGMENT_DIALOG_SALES_DETAIL_PENDING_CHECKOUT";

    /**
     * This constant is used b the instance of {@link ItemMasterListFragmentUI} to save instance state in the bundle
     * when configuration changes.
     */
    private static final String KEY_ONE = TAG_LOG + ".KEY_ONE";

    /**
     * This constant is used by the instance of {@link SalesPendingListFragmentUI} to save instance state in the bundle
     * when configuration changes.
     */
    private static final String KEY_TWO = TAG_LOG + ".KEY_TWO";


    /**
     * This constant is used by the instance of {@link OpenSalesHeaderListFragmentUI} to save instance state in the bundle
     * when configuration changes.
     */
    private static final String KEY_THREE = TAG_LOG + ".KEY_THREE";

    /**
     * This constant is used mImageStorageRef of type {@link FirebaseStorage} and it is used to as key to store and restore
     * state when configuration changes during uploading an image.
     */
    private static final String KEY_FOUR = TAG_LOG + ".KEY_FOUR";


    /**
     * This constant is used as key for permission result if going to be state in {@link #requestPermissions(String[], int)}
     */
    private static final int REQUEST_PERMISSIONS = 9000;

    /**
     * This constant is used as key for the request code in the {@link #onActivityResult(int, int, Intent)} related to
     * Credit Card scanner.
     */
    private static final int MY_SCAN_REQUEST_CODE = 101;

    /**
     * This constant is used as key for the request code in the {@link #onActivityResult(int, int, Intent)} related to
     * Camera.
     */
    public static final int TAKE_PHOTO = 201;


    /**
     * This constant is used as key for the request code in the {@link #onActivityResult(int, int, Intent)} related to
     * getting image from gallery.
     */
    public static final int GET_FROM_GALLERY_REQUEST_CODE = 301;


    /**
     * The following constants are keys, and it is used specific only for field variables related to credit card
     * workaround.
     */
    private static final String KEY_MM_ONE = TAG_LOG + "KEY_MM_ONE";
    private static final String KEY_MM_TWO = TAG_LOG + "KEY_MM_TWO";
    private static final String KEY_MM_THREE = TAG_LOG + "KEY_MM_THREE";
    private static final String KEY_MM_FOUR = TAG_LOG + "KEY_MM_FOUR";
    private static final String KEY_MM_FIVE = TAG_LOG + "KEY_MM_FIVE";

    /**
     * The following constants are keys, and it is used specific only for field variables related to image from gallery
     */
    private static final String KEY_AA_ONE = TAG_LOG + "KEY_AA_ONE";
    private static final String KEY_AA_TWO = TAG_LOG + "KEY_AA_TWO";

    /**
     * The instance of {@link android.view.ActionMode}
     */
    private android.view.ActionMode mActionMode;

    /**
     * List of push keys from {@link ItemMasterListFragmentUI.MyAdapter} for the item selected by the user using the
     * {@link android.view.ActionMode}
     * The purpose for this container is to get the id on which item is to be deleted using action mode.
     */
    private ArrayList<String> mListOfDeleteItemMasterIds;

    /**
     * List of {@link android.support.v7.widget.RecyclerView.ViewHolder} objects
     * from {@link ItemMasterListFragmentUI.MyAdapter}
     * for the item selected by the user using the {@link android.view.ActionMode}
     * The purpose of this container is to get the object in which we can change the color when the user selected it.
     */
    private ArrayList<ItemMasterListFragmentUI.ViewHolder> mListOfViewHolderItemMaster;

    /**
     * List of push keys, from {@link SalesPendingListFragmentUI.MyAdapter} for the item selected by the user using the
     * {@link android.view.ActionMode}
     * The purpose for this container is to get the id on which item is to be deleted using action mode.
     */
    private ArrayList<String> mListOfDeleteSalesPendingIds;

    /**
     * List of {@link android.support.v7.widget.RecyclerView.ViewHolder} objects
     * from {@link SalesPendingListFragmentUI.MyAdapter}
     * for the item selected by the user using the {@link android.view.ActionMode}
     * The purpose of this container is to get the object in which we can change the color when the user selected it.
     */
    private HashMap<String, SalesPendingListFragmentUI.ViewHolder> mMapOfViewHolderSalesPending;

    /**
     * List of push keys, from {@link OpenSalesHeaderListFragmentUI.MyAdapter} for the item selected by the use using the
     * {@link android.view.ActionMode}
     * The purpose for this container is to get the id on which item is to be deleted using action mode.
     */
    private ArrayList<String> mListOfDeleteOpenSalesHeaderIds;

    /**
     * List of {@link android.support.v7.widget.RecyclerView.ViewHolder} objects
     * from {@link OpenSalesHeaderListFragmentUI.MyAdapter}
     * for the item selected by the user using the {@link android.view.ActionMode}
     * The purpose of this container is to get the object in which we can change the color when the user selected it.
     */
    private HashMap<String, OpenSalesHeaderListFragmentUI.ViewHolder> mMapOfViewHolderOpenSalesHeader;


    /**
     * This will get the position of the fragment and pass them to the FAB
     */
    private static int sSelection;


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * Custom view for the activity_main
     */
    @Bind(R.id.coordinator)
    ActivityViewMainActivity mCoordinator;

    /**
     * {@link DrawerLayout} widget
     */
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;

    /**
     * This is the instance to the Firebase image storage location
     */
    private StorageReference mImageStorageRef;

    /**
     * Fragments that will populate the ViewPager,
     * this fragment will show a list of item master
     */
    private ItemMasterListFragmentUI mItemMasterListFragmentUI;

    /**
     * Fragment that will populate the viewPager,
     * this fragment will show a list of pending sales detail
     */
    private SalesPendingListFragmentUI mSalesListFragmentUI;

    /**
     * Fragment that will populate the viewPage,
     * this fragment will show a list of open sales header
     */
    private OpenSalesHeaderListFragmentUI mOpenSalesHeaderListFragmentUI;

    /**
     * The filed variables below are a temporary placeholder, the workaround is when the data is coming from
     * the {@link SalesPendingCheckoutDialogFragment} we reference them using these variables, next we call
     * {@link CardIOActivity} using {@link #startActivityForResult(Intent, int)} so later we can get the credit
     * card number from {@link #onActivityResult(int, int, Intent)} the requestCode is {@link #MY_SCAN_REQUEST_CODE}
     * next frmo the {@link #onActivityResult(int, int, Intent)} we call {@link SalesPendingPayButtonIntentService}
     * and pass all these variables and the credit card number that we just obtained.
     * Checkout the {@link #onPayButton(String, SalesHeaderModel, ArrayList, String, String)} callbacks method.
     */
    private String qqUserUid;
    private SalesHeaderModel qqSalesHeaderModel;
    private ArrayList<SalesDetailModel> qqSalesDetailModelList;
    private String qqCashPaid;
    private String qqChangeCash;

    /**
     * The field variables below are a temporary placeholder, the workaround is when the data is coming from
     * the {@link ItemMasterListFragmentUI.MyAdapter} we reference them using these variables, next we call
     * the Gallery or the Camera, for the user to pick image for this item, we later get the result from
     * {@link #onActivityResult(int, int, Intent)} and the requestCode is {@link #GET_FROM_GALLERY_REQUEST_CODE}
     * or {@link #TAKE_PHOTO}
     */
    private String aaItemMasterUserUid;
    private String aaItemMasterPushKey;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ButterKnife
        ButterKnife.bind(this);

        // Set the Toolbar
        setSupportActionBar(mCoordinator.getmToolbar());

        // Set the Navigation draw layout
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mCoordinator.getmToolbar(), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Navigation draw view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        /**
         * The code below is about the nav_header
         */
        View headerLayout = navigationView.getHeaderView(0);
        final ImageView imageView = (ImageView) headerLayout.findViewById(R.id.imageView);
        TextView name = (TextView) headerLayout.findViewById(R.id.textView1);
        TextView email = (TextView) headerLayout.findViewById(R.id.textView2);

        // Display user image in circular frame, in the navigation header
        if (mPhotoUrl != null) {
            Glide.with(this).load("https://lh3.googleusercontent.com" + mPhotoUrl).asBitmap().into(new BitmapImageViewTarget(imageView) {

                @Override
                protected void setResource(@NonNull Bitmap resource) {
                    super.setResource(resource);

                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    imageView.setImageDrawable(circularBitmapDrawable);

                }
            });
        }

        // Display the user name and email on the nav_header
        name.setText(mUserName);
        email.setText(Utility.decodeEmail(mUserEncodedEmail));


        /**
         * Create the adapter that will return a fragment for each of the
         * primary sections of the activity.
         */
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        /* Set up the ViewPager with the sections adapter. */
        if (mCoordinator.getmViewPagerContainer() != null) {
            mCoordinator.getmViewPagerContainer().setAdapter(mSectionsPagerAdapter);
        }

        /* Tab layout */
        if (mCoordinator.getmTabs() != null) {
            mCoordinator.getmTabs().setupWithViewPager(mCoordinator.getmViewPagerContainer());
        }

        /**
         * This listener is to get the tab position, however it does an other think
         * but we are only interested for the current tab position
         */
        if (mCoordinator.getmTabs() != null) {
            mCoordinator.getmTabs().addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {

                    /* First we invoke this method for mViewPager */
                    mCoordinator.getmViewPagerContainer().setCurrentItem(tab.getPosition());

                    /**
                     *  Then we can assign the value of the current position to
                     *  a static field, there are some other way to do this.
                     */
                    sSelection = tab.getPosition();


                    /**
                     * Make the FAB invisible in some selection
                     */
                    if (tab.getPosition() == 2) {
                        mCoordinator.getmFab().setVisibility(View.INVISIBLE);
                    } else {
                        mCoordinator.getmFab().setVisibility(View.VISIBLE);
                    }
                }


                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                    // If sliding then finish the action mode
                    if (mActionMode != null) {
                        mActionMode.finish();
                    }
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }


        /**
         * Restore saved instance state on configuration changes
         */
        if (savedInstanceState != null) {

            // Restore the fragment instance state
            mItemMasterListFragmentUI =
                    (ItemMasterListFragmentUI) getFragmentManager().getFragment(savedInstanceState, KEY_ONE);
            mSalesListFragmentUI =
                    (SalesPendingListFragmentUI) getFragmentManager().getFragment(savedInstanceState, KEY_TWO);
            mOpenSalesHeaderListFragmentUI =
                    (OpenSalesHeaderListFragmentUI) getFragmentManager().getFragment(savedInstanceState, KEY_THREE);

            aaItemMasterUserUid = savedInstanceState.getString(KEY_AA_ONE);
            aaItemMasterPushKey = savedInstanceState.getString(KEY_AA_TWO);
            qqUserUid = savedInstanceState.getString(KEY_MM_ONE);
            qqSalesHeaderModel = savedInstanceState.getParcelable(KEY_MM_TWO);
            qqSalesDetailModelList = savedInstanceState.getParcelableArrayList(KEY_MM_THREE);
            qqCashPaid = savedInstanceState.getString(KEY_MM_FOUR);
            qqChangeCash = savedInstanceState.getString(KEY_MM_FIVE);

        }

    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Toolbar title and subtitle
        mCoordinator.getmToolbar().setTitle("Stockita Point of Sale");
        mCoordinator.getmToolbar().setSubtitle("Transaction Screen");

    }


    @Override
    protected void onStart() {
        super.onStart();

        /**
         * Check for permission, then request if need it
         */
        if (checkVersionMarsh()) {
            requestMultiplePermissions();
        }
    }


    /**
     * When the user click the FAB, there are only two FAB available in the MainActivity
     * one is to {@link ItemMasterAddFormDialogFragment} and the second one is to
     * {@link SalesPendingAddFormDialogFragment}
     */
    @OnClick(R.id.fab)
    void fabOnClick() {

        switch (sSelection) {

            /**
             * The following code will show a DialogFragment of {@link ItemMasterAddFormDialogFragment}
             */
            case 0:

                // Instantiate the dialog fragment object and pass the encoded email as an argument
                ItemMasterAddFormDialogFragment itemMasterNewFormDialogFragment = ItemMasterAddFormDialogFragment.newInstance(mUserUid);

                // Show the Dialog Fragment on the screen
                itemMasterNewFormDialogFragment.show(getFragmentManager(), FRAGMENT_DIALOG_ITEM_MASTER_FORM);

                break;

            /**
             * The following code will show a DialogFragment of {@link SalesPendingAddFormDialogFragment}
             */
            case 1:

                // Instantiate the dialog fragment object and pass the encoded email as an argument
                SalesPendingAddFormDialogFragment salesPendingAddFormDialogFragment =
                        SalesPendingAddFormDialogFragment.newInstance(mUserUid, null, null);

                // Show the Dialog Fragment on the screen
                salesPendingAddFormDialogFragment.show(getFragmentManager(), FRAGMENT_DIALOG_SALES_DETAIL_PENDING_ADD);

                break;

            case 2:
                // No fab here
                break;
        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        /**
         * Code below to save all the fragment's state
         */

        if (mItemMasterListFragmentUI != null) {
            getFragmentManager().putFragment(outState, KEY_ONE, mItemMasterListFragmentUI);
        }

        if (mSalesListFragmentUI != null) {
            getFragmentManager().putFragment(outState, KEY_TWO, mSalesListFragmentUI);
        }

        if (mOpenSalesHeaderListFragmentUI != null) {
            getFragmentManager().putFragment(outState, KEY_THREE, mOpenSalesHeaderListFragmentUI);
        }

        // If there's an upload in progress, save the reference so you can query it later
        if (mImageStorageRef != null) {
            outState.putString(KEY_FOUR, mImageStorageRef.toString());
        }

        if (aaItemMasterUserUid != null) {
            outState.putString(KEY_AA_ONE, aaItemMasterUserUid);
        }

        if (aaItemMasterPushKey != null) {
            outState.putString(KEY_AA_TWO, aaItemMasterPushKey);
        }

        if (qqUserUid != null) {
            outState.putSerializable(KEY_MM_ONE, qqUserUid);
        }

        if (qqSalesHeaderModel != null) {
            outState.putParcelable(KEY_MM_TWO, qqSalesHeaderModel);
        }

        if (qqSalesDetailModelList != null) {
            outState.putParcelableArrayList(KEY_MM_THREE, qqSalesDetailModelList);
        }

        if (qqCashPaid != null) {
            outState.putString(KEY_MM_FOUR, qqCashPaid);
        }

        if (qqChangeCash != null) {
            outState.putString(KEY_MM_FIVE, qqChangeCash);
        }

    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);


        /**
         * The code below is related to uploading image to the server storage
         */


        // If there was an upload in progress, get its reference and create a new StorageReference
        final String stringRef = savedInstanceState.getString(KEY_FOUR);
        if (stringRef == null) {
            return;
        }
        mImageStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(stringRef);

        // Find all UploadTasks under this StorageReference (in this example, there should be one)
        List tasks = mImageStorageRef.getActiveUploadTasks();
        if (tasks.size() > 0) {

            // Get the task monitoring the upload
            UploadTask task = (UploadTask) tasks.get(0);

            // Add new listeners to the task using an Activity scope
            task.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot state) {
                    // TODO: handleSuccess(state); //call a user defined function to handle the event.
                }
            });
        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Get the id of the item menu
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // Drawer menu items
        switch (id) {
            case R.id.archive:
                startActivity(new Intent(this, SecondActivity.class));
                finish();
                break;
            case R.id.nav_manage:
                startActivity(new Intent(this, SettingsActivity.class));
                finish();
                break;
            case R.id.logout:
                signOut();
                finish();
                break;
        }

        // This where to handle closing the drawer back.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        // Return true
        return true;
    }


    /**
     * All runtime permissions should be written here
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void requestMultiplePermissions() {

        // Get the string permissions from the manifest
        String storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String cameraPermission = Manifest.permission.CAMERA;

        // Get the the current permissions status whether granted or rejected
        int hasStoragePermission = checkSelfPermission(storagePermission);
        int hasCameraPermission = checkSelfPermission(cameraPermission);

        // Initialize a list of string as a container to pac all the permission
        List<String> permissionsList = new ArrayList<>();

        // Check if not already granted then add the permission to the container
        if (hasStoragePermission != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(storagePermission);
        }

        // Check if not already granted then add the permission to the container
        if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(cameraPermission);
        }

        // Check if the list is not empty
        if (!permissionsList.isEmpty()) {

            /**
             * Convert the {@link permissionsList} into String Array object
             */
            String[] params = permissionsList.toArray(new String[permissionsList.size()]);

            /**
             * Invoke thie method {@link #requestPermissions(String[], int)}
             */
            requestPermissions(params, REQUEST_PERMISSIONS);
        }
    }


    /**
     * This callback coming from the {@link ItemMasterListFragmentUI.MyAdapter}
     * @param userUid The user UID
     * @param pushKey The server unique key
     * @param model   The model POJO
     */
    @Override
    public void onItemMasterEditDialogCallBacks(int requestCode, String userUid, String pushKey, Object model) {

        switch (requestCode) {

            /**
             * This case coming from {@link ItemMasterListFragmentUI.MyAdapter} to
             * show {@link ItemMasterEditFormDialogFragment}, so the user can edit the item master.
             */
            case Constants.REQUEST_CODE_DIALOG_ONE:

                // Instantiate the dialog fragment object and pass the encoded email, pushKey, and the itemModel as an argument
                ItemMasterEditFormDialogFragment itemMasterEditFormDialogFragment =
                        ItemMasterEditFormDialogFragment.newInstance(userUid, pushKey, (ItemModel) model);

                // Show the Dialog Fragment on the screen
                itemMasterEditFormDialogFragment.show(getFragmentManager(), FRAGMENT_DIALOG_ITEM_MASTER_EDIT);
                break;


            /**
             * If the user choose to add image from the gallery
             */
            case Constants.REQUEST_CODE_DIALOG_TWO:

                aaItemMasterUserUid = userUid;
                aaItemMasterPushKey = pushKey;

                // Check of permission
                if (android.support.v4.app.ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                // Verify that the intent will resolve to an activity */
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, GET_FROM_GALLERY_REQUEST_CODE);
                }

                break;

            /**
             * If the user choose to take a photo from the camera
             */
            case Constants.REQUEST_CODE_DIALOG_THREE:

                aaItemMasterUserUid = userUid;
                aaItemMasterPushKey = pushKey;

                // Check for permission
                if (android.support.v4.app.ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Intent startCustomCameraIntent = new Intent(this, CameraActivity.class);
                startActivityForResult(startCustomCameraIntent, TAKE_PHOTO);

                break;
        }
    }


    /**
     * This callbacks method is to communicate back between fragments
     * {@link ItemMasterEditFormDialogFragment} and {@link ItemMasterAddFormDialogFragment}
     * and {@link ItemMasterListFragmentUI.MyAdapter}
     */
    @Override
    public void getTheResultFromTheDialog(int requestCode, String message, String itemMasterPushKey, Object model) {

        switch (requestCode) {

            /**
             * This case is coming from
             * {@link ItemMasterEditFormDialogFragment},
             * and {@link ItemMasterAddFormDialogFragment#clickSaveButton()}
             * and {@link ItemMasterAddFormDialogFragment#clickCancelButton()}
             */
            case Constants.REQUEST_CODE_DIALOG_ONE:

                // Now we can pass the data back to the current fragment.
                mItemMasterListFragmentUI.setAnyResult(message);
                break;
        }
    }


    /**
     * This callback is coming from {@link SalesPendingListFragmentUI.MyAdapter}
     * Its job is to show a dialog so the user can edit data already in the sales pending list
     *
     * @param requestCode   The request code
     * @param userUid       The user's UID
     * @param pushKeyDetail The push() key for the detail sales or the detail pending sales
     * @param pushKeyHeader The push() key for the header sales if already checkout
     * @param model         The model POJO
     */
    @Override
    public void onSalesEditDialogCallbacks(int requestCode, String userUid, String pushKeyDetail, String pushKeyHeader, Object model) {

        switch (requestCode) {

            /**
             * This is when the user click the edit item on the popup {@link SalesPendingListFragmentUI.MyAdapter}
             * it will trigger a dialog to edit the pending sales
             */
            case Constants.REQUEST_CODE_SALES_DIALOG_ONE:

                // Instantiate the dialog fragment object and pass the encoded email, pushKey, and the SalesDetailModel as an argument
                SalesPendingEditFormDialogFragment salesPendingEditFormDialogFragment =
                        SalesPendingEditFormDialogFragment.newInstance(userUid, pushKeyDetail, (SalesDetailModel) model);

                // Show the Dialog Fragment on the screen
                salesPendingEditFormDialogFragment.show(getFragmentManager(), FRAGMENT_DIALOG_SALES_DETAIL_PENDING_EDIT);

                break;

        }

    }


    /**
     * If the user choose an item {@link ItemModel} from the
     * {@link com.stockita.stockitapointofsales.itemmaster.LookupItemMasterListDialogFragment}
     * then it will pass them to {@link SalesPendingAddFormDialogFragment}
     *
     * @param itemMasterPushKey The item master server push() key
     * @param model             The {@link ItemModel}
     */
    @Override
    public void sendItemMasterData(String itemMasterPushKey, ItemModel model) {

        /**
         * The following code will show a DialogFragment {@link SalesPendingAddFormDialogFragment}
         */

        // Instantiate the dialog fragment object and pass the encoded email, the item push() key and itemModel as an argument
        SalesPendingAddFormDialogFragment salesPendingAddFormDialogFragment =
                SalesPendingAddFormDialogFragment.newInstance(mUserUid, itemMasterPushKey, model);

        // Show the Dialog Fragment on the screen
        salesPendingAddFormDialogFragment.show(getFragmentManager(), FRAGMENT_DIALOG_SALES_DETAIL_PENDING_ADD);

    }


    /**
     * Just keep this method empty this is from {@link OpenSalesHeaderListCallbacks}
     */
    @Deprecated
    @Override
    public void getSalesCheckoutDialog(String userUid, String total, String salesHeaderKey) {
        // Nothing
    }


    /**
     * Callback from {@link SalesPendingListFragmentUI#onMenuItemClick(MenuItem)}
     * the {@link SalesPendingCheckoutDialogFragment}
     *
     * @param userUid User's UID
     */
    @Override
    public void getSalesCheckoutDialog(String userUid, String total) {

        // Instantiate the dialog fragment object and pass the encoded email as an argument
        SalesPendingCheckoutDialogFragment salesPendingCheckoutDialogFragment = SalesPendingCheckoutDialogFragment.newInstance(userUid, total);

        // Show the Dialog Fragment on the screen
        salesPendingCheckoutDialogFragment.show(getFragmentManager(), FRAGMENT_DIALOG_SALES_DETAIL_PENDING_CHECKOUT);
    }


    /**
     * This method will call {@link com.stockita.stockitapointofsales.salespack.openpack.OpenSalesDetailActivity}
     * this method is coming from {@link com.stockita.stockitapointofsales.salespack.openpack.OpenSalesHeaderListFragmentUI.MyAdapter}
     * This callbacks is when the user clicked on an item in the {@link OpenSalesHeaderListFragmentUI}
     * so they can go into the detail {@link com.stockita.stockitapointofsales.salespack.openpack.OpenSalesDetailFragmentUI}
     *
     * @param userUid   user's UID
     * @param headerKey the invoice header push() key
     */
    @Override
    public void callSalesDetailActivity(String userUid, String headerKey) {

        // Use factory method
        OpenSalesDetailActivity.openSalesDetailActivity(this, userUid, headerKey);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            /**
             * Credit card scanner, this is coming from
             * {@link #onPayButton(String, SalesHeaderModel, ArrayList, String, String)} ()}
             */
            case MY_SCAN_REQUEST_CODE:
                String number = "";
                String expMonth = "";
                String expYear = "";

                if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                    CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                    number = scanResult.getFormattedCardNumber();
                    expMonth = String.valueOf(scanResult.expiryMonth);
                    expYear = String.valueOf(scanResult.expiryYear);


                } else {
                    Toast.makeText(this, "scan cancel", Toast.LENGTH_SHORT).show();

                }

                /**
                 * Pass the data again with credit card information, this is IntentService
                 * {@link SalesPendingPayButtonIntentService#insertPay(Context, String, SalesHeaderModel, ArrayList, String, String, String, String)}
                 */
                SalesPendingPayButtonIntentService.insertPay(this, qqUserUid, qqSalesHeaderModel, qqSalesDetailModelList, number, expMonth + "/" + expYear, qqCashPaid, qqChangeCash);

                // break
                break;

            /**
             * Result of image from the gallery then upload to firebase storage
             */
            case GET_FROM_GALLERY_REQUEST_CODE:

                if (resultCode == Activity.RESULT_OK) {

                    // The image Uri
                    Uri selectedImage = data.getData();

                    // Convert the image Uri into real file path
                    String realFilePath = null;
                    try {
                        realFilePath = Utility.convertMediaUriToPath(selectedImage, this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // This helper method is to upload image to server
                    saveImageIntoRealDatabaseAndStorage(realFilePath);

                } else {
                    Toast.makeText(this, "System failed", Toast.LENGTH_SHORT).show();
                }
                break;


            /**
             * Result from the camera
             */
            case TAKE_PHOTO:

                if (resultCode == Activity.RESULT_OK) {

                    // The image Uri
                    Uri selectedImage = data.getData();

                    // Convert the image Uri into real file path
                    String realFilePath = selectedImage.getPath();

                    // This helper method is to upload image to server
                    saveImageIntoRealDatabaseAndStorage(realFilePath);

                } else {
                    // Image capture failed, advise user
                    Toast.makeText(this, "System failed", Toast.LENGTH_SHORT).show();
                }

        }
    }


    /**
     * Helper method to save and upload images to the server
     */
    private void saveImageIntoRealDatabaseAndStorage(String realFilePath) {


        /**
         * Add the {@link ItemImageModel} to the server
         */

        // Instantiate the model pass file path and push key as argument
        ItemImageModel itemImageModel = new ItemImageModel(realFilePath, aaItemMasterPushKey);

        // Initialize the server location for real time database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(aaItemMasterUserUid)
                .child(Constants.FIREBASE_ITEM_MASTER_IMAGE_LOCATION)
                .child(aaItemMasterPushKey);

        // Set the value with push() so each itemMaster can have multiple itemImage
        databaseReference.push().setValue(itemImageModel);

        /**
         * Now upload the file to the cloud
         */

        // Initialize storage
        mImageStorageRef = FirebaseStorage.getInstance().getReference();

        // Get reference to the specific storage location to storage the images
        StorageReference imageStorageRefForUser =
                mImageStorageRef
                        .child(aaItemMasterUserUid)
                        .child(Constants.FIREBASE_ITEM_MASTER_IMAGE_LOCATION)
                        .child(aaItemMasterPushKey);

        // Get the imagePath from the model then convert imagePath to Uri
        Uri file = Uri.fromFile(new File(itemImageModel.getImageUrl()));

        // Pack the Uri object into StorageReference object
        StorageReference uriRef = imageStorageRefForUser.child(file.getLastPathSegment());

        // Create & add file metadata including the content type
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();

        // Upload the file, pass the Uri file and the metadata as argument
        UploadTask uploadTask = uriRef.putFile(file, metadata);

        // Register observers to listen for when the upload is done or if it fails
        uploadTask.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                //Uri downloadUrl = taskSnapshot.getDownloadUrl();

            }
        });

    }


    /**
     * When the user click pay from {@link SalesPendingCheckoutDialogFragment}
     * Because we are using {@link CardIOActivity} so the result is in the {@link #onActivityResult(int, int, Intent)}
     * for requestCode {@link #MY_SCAN_REQUEST_CODE}
     *
     * @param userUid              User's UID
     * @param salesHeaderModel     {@link SalesHeaderModel}
     * @param salesDetailModelList {@link SalesDetailModel} ArraLisy
     * @param cashPaid             cash received
     * @param changeCash           change returned to the customer
     */
    @Override
    public void onPayButton(String userUid,
                            SalesHeaderModel salesHeaderModel,
                            ArrayList<SalesDetailModel> salesDetailModelList,
                            String cashPaid,
                            String changeCash) {

        // Pass the data to the field for later use, this is like a temporary placeholder
        // So later onActivityResult we will retrieve these variables
        qqUserUid = userUid;
        qqSalesHeaderModel = salesHeaderModel;
        qqSalesDetailModelList = salesDetailModelList;
        qqCashPaid = cashPaid;
        qqChangeCash = changeCash;

        // Get the credit card reader
        Intent scanIntent = new Intent(this, CardIOActivity.class);

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
    }


    /**
     * This callbacks is coming from
     * {@link com.stockita.stockitapointofsales.itemmaster.ItemMasterListFragmentUI.MyAdapter}
     * This is one of three callbacks methods to perform multi item delete using {@link android.view.ActionMode}
     *
     * @param headerPushKeyList Container for push key of each item master in an ArrayList
     * @param viewHolderList    Container for view holder object in an ArrayList
     */
    @Override
    public void sendDeleteMultiItemMaster(ArrayList<String> headerPushKeyList, ArrayList<ItemMasterListFragmentUI.ViewHolder> viewHolderList) {

        // Pass a reference
        mListOfDeleteItemMasterIds = headerPushKeyList;
        mListOfDeleteItemMasterIds = new ArrayList<>();
        mListOfDeleteItemMasterIds.clear();
        mListOfDeleteItemMasterIds.addAll(headerPushKeyList);

        mListOfViewHolderItemMaster = new ArrayList<>();
        mListOfViewHolderItemMaster.clear();
        mListOfViewHolderItemMaster.addAll(viewHolderList);

    }


    /**
     * Getter method for the {@link ActionMode} instance
     * This this two of three callbacks methods to perform multi item delete
     * @return {@link ActionMode} object
     */
    @Override
    public android.view.ActionMode getActionModeItemMaster() {
        return mActionMode;
    }


    /**
     * This method coming from {@link com.stockita.stockitapointofsales.itemmaster.ItemMasterListFragmentUI.MyAdapter}
     * and its function is to activate/deactivate the {@link ActionMode} and to perform the multi item delete
     * This this three of three callbacks methods
     */
    @Override
    public void onTurnActionModeOnItemMaster() {

        // Initialize the action mode callbacks
        mActionMode = (this).startActionMode(new android.view.ActionMode.Callback() {

            @Override
            public boolean onCreateActionMode(android.view.ActionMode actionMode, Menu menu) {

                // Here we inflate the menu
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.menu_action_mode, menu);

                // Set the boolean to true, so later the user can perform multi item selection
                mItemMasterListFragmentUI.mMyAdapter.isContextualMode = true;
                return true;
            }

            @Override
            public boolean onPrepareActionMode(android.view.ActionMode actionMode, Menu menu) {
                return true;
            }

            @Override
            public boolean onActionItemClicked(android.view.ActionMode actionMode, MenuItem menuItem) {

                // When the user clicked on of the action menu item it will perform the following operations
                switch (menuItem.getItemId()) {

                    // Delete multi items
                    case R.id.action_delete:

                        // Iterate and delete
                        for (String pushIds : mListOfDeleteItemMasterIds) {
                            ItemMasterIntentService.deleteOneItemMaster(getBaseContext(), mUserUid, pushIds);
                        }

                        // Dismiss the action mode
                        mActionMode.finish();

                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(android.view.ActionMode actionMode) {

                // return the color to normal
                for (int i = 0; i < mListOfViewHolderItemMaster.size(); i++) {
                    mListOfViewHolderItemMaster.get(i).mAdapterView.getmRoot().setBackgroundColor(getResources().getColor(R.color.white));

                }

                // Free resources
                mItemMasterListFragmentUI.mMyAdapter.isContextualMode = false;
                mListOfDeleteItemMasterIds = null;
                mListOfViewHolderItemMaster = null;
                mActionMode = null;

            }
        });


        // Display the number of items clicked by the user as a counter on the toolbar
        if (mActionMode != null) {
            int size = mListOfDeleteItemMasterIds.size();
            mActionMode.setTitle(size + " Item(s) will be deleted");

        }

    }


    /**
     * This callbacks related to ActionMode and its coming from
     * {@link com.stockita.stockitapointofsales.salespack.pendingpack.SalesPendingListFragmentUI.MyAdapter}
     *
     * @param pushKeyList       Container for sales detail push() key
     * @param viewHolderHashMap Container for view holder
     */
    @Override
    public void sendDeleteMultiSalesPending(ArrayList<String> pushKeyList, HashMap<String, SalesPendingListFragmentUI.ViewHolder> viewHolderHashMap) {

        // Pass the reference
        mListOfDeleteSalesPendingIds = new ArrayList<>();
        mListOfDeleteSalesPendingIds.clear();
        mListOfDeleteSalesPendingIds.addAll(pushKeyList);

        mMapOfViewHolderSalesPending = new HashMap<>();
        mMapOfViewHolderSalesPending.clear();
        mMapOfViewHolderSalesPending.putAll(viewHolderHashMap);


    }


    /**
     * This method coming from {@link com.stockita.stockitapointofsales.salespack.pendingpack.SalesPendingListFragmentUI.MyAdapter}
     * and its function is to activate/deactivate the {@link ActionMode} and to perform the multi item delete
     */
    @Override
    public void onTurnActionModeOnSalesPending() {

        // Initialize the action mode callbacks
        mActionMode = (this).startActionMode(new android.view.ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(android.view.ActionMode actionMode, Menu menu) {

                // Inflate the action mode menu
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.menu_action_mode, menu);

                // Set the boolean to true
                mSalesListFragmentUI.mMyAdapter.isContextualMode = true;
                return true;
            }

            @Override
            public boolean onPrepareActionMode(android.view.ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(android.view.ActionMode actionMode, MenuItem menuItem) {

                // Get the item menu ids
                switch (menuItem.getItemId()) {

                    case R.id.action_delete:

                        // Iterate and delete
                        for (String pushIds: mListOfDeleteSalesPendingIds) {

                            // Get the reference
                            DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference()
                                    .child(mUserUid)
                                    .child(Constants.FIREBASE_SALES_DETAIL_PENDING_LOCATION)
                                    .child(pushIds);

                            // Pass null to delete
                            itemRef.setValue(null);

                        }

                        // Dismiss the action mode
                        mActionMode.finish();

                        return true;
                }

                return false;
            }

            @Override
            public void onDestroyActionMode(android.view.ActionMode actionMode) {

                // Return the color to normal
                for (int i = 0; i < mListOfDeleteSalesPendingIds.size(); i++) {
                    String pushKey = mListOfDeleteSalesPendingIds.get(i);
                    mMapOfViewHolderSalesPending.get(pushKey).getAdapterView().getmRoot().setBackgroundColor(getResources().getColor(R.color.white));
                    mMapOfViewHolderSalesPending.get(pushKey).getAdapterView().getmItemPopupMenu().setBackgroundColor(getResources().getColor(R.color.white));
                }


                // Free resources
                mSalesListFragmentUI.mMyAdapter.isContextualMode = false;
                mActionMode = null;
                mListOfDeleteSalesPendingIds = null;
                mMapOfViewHolderSalesPending = null;

            }
        });


        // Display the number of items clicked by the user as a counter on the toolbar
        if (mActionMode != null) {
            int size = mListOfDeleteSalesPendingIds.size();
            mActionMode.setTitle(size + " Item(s) will be deleted");
        }
    }


    /**
     * This method coming from {@link com.stockita.stockitapointofsales.salespack.openpack.OpenSalesHeaderListFragmentUI.MyAdapter}
     * @param pushKeyList               Container for sales header push() key
     * @param viewHolderMap             Container for view holder
     */
    @Override
    public void sendDeleteMultiOpenSalesHeader(ArrayList<String> pushKeyList, HashMap<String, OpenSalesHeaderListFragmentUI.ViewHolder> viewHolderMap) {

        mListOfDeleteOpenSalesHeaderIds = new ArrayList<>();
        mListOfDeleteOpenSalesHeaderIds.clear();
        mListOfDeleteOpenSalesHeaderIds.addAll(pushKeyList);

        mMapOfViewHolderOpenSalesHeader = new HashMap<>();
        mMapOfViewHolderOpenSalesHeader.clear();
        mMapOfViewHolderOpenSalesHeader.putAll(viewHolderMap);

    }


    /**
     * This method callback coming from {@link com.stockita.stockitapointofsales.salespack.openpack.OpenSalesHeaderListFragmentUI.MyAdapter}
     */
    @Override
    public void onTurnActionModeOnOpenSalesHeader() {

        mActionMode = (this).startActionMode(new android.view.ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(android.view.ActionMode actionMode, Menu menu) {
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.menu_action_mode, menu);
                mOpenSalesHeaderListFragmentUI.mMyAdapter.isContextualMode = true;
                return true;
            }

            @Override
            public boolean onPrepareActionMode(android.view.ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(android.view.ActionMode actionMode, MenuItem menuItem) {

                // Get the menu item ids
                switch (menuItem.getItemId()) {


                    // Delete multi item
                    case R.id.action_delete:

                        // Iterate and delete
                        for (String pushIds : mListOfDeleteOpenSalesHeaderIds) {

                            // Get the database reference
                            DatabaseReference openSalesRef = FirebaseDatabase.getInstance().getReference()
                                    .child(mUserUid);

                            // Delete that match the pushKeyHeader
                            HashMap<String, Object> deleteOpenSales = new HashMap<>();
                            deleteOpenSales.put("/" + Constants.FIREBASE_OPEN_SALES_DETAIL_LOCATION + "/" + pushIds, null);
                            deleteOpenSales.put("/" + Constants.FIREBASE_OPEN_SALES_HEADER_LOCATION + "/" + pushIds, null);
                            openSalesRef.updateChildren(deleteOpenSales);

                        }

                        // invoke finish
                        mActionMode.finish();

                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(android.view.ActionMode actionMode) {

                // return the color to normal
                for (int i = 0; i < mListOfDeleteOpenSalesHeaderIds.size(); i++) {
                    String pushKey = mListOfDeleteOpenSalesHeaderIds.get(i);
                    mMapOfViewHolderOpenSalesHeader.get(pushKey).getmAdapterView().getmRoot().setBackgroundColor(getResources().getColor(R.color.white));
                    mMapOfViewHolderOpenSalesHeader.get(pushKey).getmAdapterView().getmHeaderPopupMenu().setBackgroundColor(getResources().getColor(R.color.white));
                }

                // Free resources
                mOpenSalesHeaderListFragmentUI.mMyAdapter.isContextualMode = false;
                mActionMode = null;
                mListOfDeleteOpenSalesHeaderIds= null;
                mMapOfViewHolderOpenSalesHeader = null;

            }
        });

        // Display the number of items clicked by the user as a counter on the toolbar
        if (mActionMode != null) {
            int size = mListOfDeleteOpenSalesHeaderIds.size();
            mActionMode.setTitle(size + " Item(s) will be deleted");
        }
    }


    /**
     * This method comming from {@link com.stockita.stockitapointofsales.salespack.openpack.OpenSalesHeaderListFragmentUI.MyAdapter}
     * @return          {@link android.view.ActionMode} object
     */
    @Override
    public android.view.ActionMode getActionModeOpenSalesHeader() {
        return mActionMode;
    }


    /**
     * Class for the pager adapter to support the sliding tab
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {


        /**
         * Constructor
         *
         * @param fm {@link FragmentManager} instance
         */
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {

            // Local final variables
            final int ZERO = 0;
            final int ONE = 1;
            final int TWO = 2;

            switch (position) {

                case ZERO:
                    /**
                     * {@link ItemMasterListFragmentUI#newInstance(String, String, String)}
                     */
                    mItemMasterListFragmentUI = ItemMasterListFragmentUI.newInstance(mUserEncodedEmail, mUserName, mUserUid);
                    return mItemMasterListFragmentUI;
                case ONE:
                    /**
                     * {@link SalesPendingListFragmentUI#newInstance(String, String, String)}
                     */
                    mSalesListFragmentUI = SalesPendingListFragmentUI.newInstance(mUserEncodedEmail, mUserName, mUserUid);
                    return mSalesListFragmentUI;
                case TWO:
                    /**
                     * {@link OpenSalesHeaderListFragmentUI#newInstance(String)}
                     */
                    mOpenSalesHeaderListFragmentUI = OpenSalesHeaderListFragmentUI.newInstance(mUserUid);
                    return mOpenSalesHeaderListFragmentUI;

            }

            return null;
        }


        @Override
        public int getCount() {
            return 3;
        }


        @Override
        public CharSequence getPageTitle(int position) {

            // local constant
            final int ZERO = 0;
            final int ONE = 1;
            final int TWO = 2;

            switch (position) {

                // Return a String for header label of of each sliding tab

                case ZERO:
                    return "Item list";
                case ONE:
                    return "Point of sale";
                case TWO:
                    return "Open bill";

            }

            return super.getPageTitle(position);
        }
    }
}

