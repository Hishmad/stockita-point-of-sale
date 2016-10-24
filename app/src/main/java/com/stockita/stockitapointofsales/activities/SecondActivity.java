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

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.stockita.stockitapointofsales.R;
import com.stockita.stockitapointofsales.salespack.paidpack.BarGraph;
import com.stockita.stockitapointofsales.salespack.paidpack.PaidSalesDetailActivity;
import com.stockita.stockitapointofsales.salespack.paidpack.PaidSalesHeaderListFragmentUI;
import com.stockita.stockitapointofsales.salespack.paidpack.PaymentActivity;
import com.stockita.stockitapointofsales.utilities.SettingsActivity;
import com.stockita.stockitapointofsales.utilities.Utility;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * This activity host the archive such like {@link PaidSalesHeaderListFragmentUI}  and
 * the {@link BarGraph}
 */
public class SecondActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        PaidSalesHeaderListFragmentUI.PaidSalesHeaderListInterface {

    /**
     * Tag constants
     */
    private static final String TAG_LOG = SecondActivity.class.getSimpleName();

    /**
     * Key for the {@link PaidSalesHeaderListFragmentUI}
     */
    private static final String KEY_ONE = TAG_LOG + ".KEY_ONE";

    /**
     * Key for The {@link BarGraph}
     */
    private static final String KEY_TWO = TAG_LOG + ".KEY_TWO";


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
     * Fragment that will populate the viewPage, this fragment is for the open sales header
     */
    private PaidSalesHeaderListFragmentUI mPaidSalesHeaderListFragmentUI;

    /**
     * Fragment that will display the bar graph of monthly paid sales
     */
    private BarGraph mBarGraph;


    // The following are the view widgets
    @Bind(R.id.container)
    ViewPager container;
    @Bind(R.id.tabs)
    TabLayout tabs;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // ButterKnife
        ButterKnife.bind(this);

        // Toolbar
        setSupportActionBar(toolbar);

        // Set the fab to gone
        fab.setVisibility(View.GONE);

        // Navigation draw
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        /**
         * The code below is about the nav_header
         */
        View headerLayout = navigationView.getHeaderView(0);
        final ImageView imageView = (ImageView) headerLayout.findViewById(R.id.imageView);
        TextView name = (TextView) headerLayout.findViewById(R.id.textView1);
        TextView email = (TextView) headerLayout.findViewById(R.id.textView2);

        // Display user image in circular frame
        if (mPhotoUrl != null) {
            Glide.with(this).load("https://lh3.googleusercontent.com" + mPhotoUrl).asBitmap().into(new BitmapImageViewTarget(imageView) {

                @Override
                protected void setResource(Bitmap resource) {
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

        //Set up the ViewPager with the sections adapter. */
        if (container != null) {
            container.setAdapter(mSectionsPagerAdapter);
        }

        /* Tab layout */
        if (tabs != null) {
            tabs.setupWithViewPager(container);
        }

        /**
         * This is listener is to get the tab position, however it does an other think
         * but we are only interested for the current tab position
         */
        if (tabs != null) {
            tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {

                    /* First we invoke this method for mViewPager */
                    container.setCurrentItem(tab.getPosition());

                    /**
                     *  Then we can assign the value of the current position to
                     *  a static field, there are some other way to do this.
                     */
                    sSelection = tab.getPosition();

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }


        if (savedInstanceState != null) {
            mPaidSalesHeaderListFragmentUI =
                    (PaidSalesHeaderListFragmentUI) getFragmentManager().getFragment(savedInstanceState, KEY_ONE);
            mBarGraph =
                    (BarGraph) getFragmentManager().getFragment(savedInstanceState, KEY_TWO);

        }

    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // toolbar title & subtitle
        toolbar.setTitle("Stockita Point of Sale");
        toolbar.setSubtitle("Archive Screen");

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            startActivity(new Intent(getBaseContext(), MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.second, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
            case R.id.transactions:
                startActivity(new Intent(this, MainActivity.class));
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


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @OnClick(R.id.fab)
    void fab() {

        // No fab yet, at this time set to GONE

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        if (mPaidSalesHeaderListFragmentUI != null) {
            getFragmentManager().putFragment(outState, KEY_ONE, mPaidSalesHeaderListFragmentUI);
        }

        if (mBarGraph != null) {
            getFragmentManager().putFragment(outState, KEY_TWO, mBarGraph);
        }
    }


    /**
     * This callbacks is coming from {@link PaidSalesHeaderListFragmentUI}
     * for calling the {@link PaidSalesDetailActivity} to show the detail sales
     * @param userUid                   User's UID
     * @param paidSalesHeaderKey        paid sales header push key
     */
    @Override
    public void callPaidSalesDetailActivity(String userUid, String paidSalesHeaderKey) {

        // Call the factory method
        PaidSalesDetailActivity.paidSalesDetailActivity(this, userUid, paidSalesHeaderKey);

    }


    /**
     * This callbacks is coming from {@link PaidSalesHeaderListFragmentUI}
     * for calling {@link PaymentActivity} to show detail payment
     * @param userUid                   User's UID
     * @param paidSalesHeaderKey        paid sales header push key
     */
    @Override
    public void callPaymentActivity(String userUid, String paidSalesHeaderKey) {

        // Call the factory method
        PaymentActivity.paymentActivity(this, userUid, paidSalesHeaderKey);

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
            //final int ONE = 1;

            switch (position) {

                case ZERO:
                    mPaidSalesHeaderListFragmentUI = PaidSalesHeaderListFragmentUI.newInstance(mUserUid);
                    return mPaidSalesHeaderListFragmentUI;
//                case ONE:
//                    mBarGraph = BarGraph.newInstance(mUserUid);
//                    return mBarGraph;

            }

            return null;
        }


        @Override
        public int getCount() {
            return 1;
        }


        @Override
        public CharSequence getPageTitle(int position) {

            // local constant
            final int ZERO = 0;
            //final int ONE = 1;

            switch (position) {

                // Return a String for header label of of each sliding tab

                case ZERO:
                    return "Paid Sales";
//                case ONE:
//                    return "Bar Chart";

            }

            return super.getPageTitle(position);
        }
    }
}
