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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.stockita.stockitapointofsales.R;
import com.stockita.stockitapointofsales.activities.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This is the activity that host the {@link PaidSalesDetailFragmentUI}
 */
public class PaidSalesDetailActivity extends BaseActivity {

    private static final String TAG_LOG = PaidSalesDetailActivity.class.getSimpleName();
    private static final String KEY_ONE = TAG_LOG + ".KEY_ONE";
    private static final String KEY_TWO = TAG_LOG + ".KEY_TWO";

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.container)
    FrameLayout container;


    /**
     * Factory method to call this activity from
     * {@link com.stockita.stockitapointofsales.activities.SecondActivity#callPaidSalesDetailActivity(String, String)}
     * @param context                   The activity context
     * @param userUid                   The user's UID
     * @param paidSalesHeaderKey        The push() key for the sales header
     */
    public static void paidSalesDetailActivity(Context context, String userUid, String paidSalesHeaderKey) {

        Intent intent = new Intent(context, PaidSalesDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ONE, userUid);
        bundle.putString(KEY_TWO, paidSalesHeaderKey);
        intent.putExtras(bundle);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paid_sales_detail);

        // ButterKnife
        ButterKnife.bind(this);

        // toolbar
        setSupportActionBar(toolbar);


        // Show the back button on the top left
        if (toolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // When user press the back arrow on the toolbar it will hit the back button
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }

        if (savedInstanceState == null) {

            Bundle bundle = getIntent().getExtras();

            String userUid = bundle.getString(KEY_ONE);
            String paidSalesHeaderKey = bundle.getString(KEY_TWO);


            PaidSalesDetailFragmentUI fragment = PaidSalesDetailFragmentUI.newInstance(userUid, paidSalesHeaderKey);

            getFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();

        }

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // toolbar title & subtitle
        toolbar.setTitle("Stockita Point of Sale");
        toolbar.setSubtitle("Paid Sales Detail");
    }
}
