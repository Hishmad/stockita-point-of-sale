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

package com.stockita.stockitapointofsales.customViews;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.stockita.stockitapointofsales.R;

/**
 * This class is a custom view for the {@link com.stockita.stockitapointofsales.activities.MainActivity}.
 */
public class ActivityViewMainActivity extends CoordinatorLayout {

    // Views
    private Toolbar mToolbar;
    private TabLayout mTabs;
    private ViewPager mViewPagerContainer;
    private FloatingActionButton mFab;

    /**
     * Constructor
     * @param context       Activity context
     */
    public ActivityViewMainActivity(Context context) {
        super(context);
        init(context);
    }

    /**
     * Constructor
     * @param context       Activity context
     * @param attrs         {@link AttributeSet}
     */
    public ActivityViewMainActivity(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Helper method to initialize the view group and the widget
     * @param context   Activity context
     */
    private void init(Context context) {


        // Initialize the layout file which is the root view of all widgets
        View mRootView = inflate(context, R.layout.view_activity_main, this);

        // Initialize the widgets
        mToolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        mTabs = (TabLayout) mRootView.findViewById(R.id.tabs);
        mViewPagerContainer = (ViewPager) mRootView.findViewById(R.id.container);
        mFab = (FloatingActionButton) mRootView.findViewById(R.id.fab);

    }

    public Toolbar getmToolbar() {
        return mToolbar;
    }

    public void setmToolbar(Toolbar mToolbar) {
        this.mToolbar = mToolbar;
    }

    public TabLayout getmTabs() {
        return mTabs;
    }

    public void setmTabs(TabLayout mTabs) {
        this.mTabs = mTabs;
    }

    public ViewPager getmViewPagerContainer() {
        return mViewPagerContainer;
    }

    public void setmViewPagerContainer(ViewPager mViewPagerContainer) {
        this.mViewPagerContainer = mViewPagerContainer;
    }

    public FloatingActionButton getmFab() {
        return mFab;
    }

    public void setmFab(FloatingActionButton mFab) {
        this.mFab = mFab;
    }
}
