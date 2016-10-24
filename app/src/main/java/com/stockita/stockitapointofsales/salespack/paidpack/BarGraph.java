package com.stockita.stockitapointofsales.salespack.paidpack;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.stockita.stockitapointofsales.R;
import com.stockita.stockitapointofsales.data.MonthlySalesModel;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * TODO: Future development
 */
public class BarGraph extends Fragment {

    // Constant
    private static final String TAG_LOG = BarGraph.class.getSimpleName();

    private String mUserEncodedEmail;

    private DatabaseReference mYearMonthRef;
    private ChildEventListener mYearMonthListener;

    private DatabaseReference mPaidSalesHeaderRef;
    private ChildEventListener mPaidSalesHeaderListener;

    private ArrayList<String> mYearMonthList;
    private ArrayList<String> mGrandTotalList;
    private HashMap<String, MonthlySalesModel> mYearMonthSumOfGrandTotal;


    // View
    @Bind(R.id.year_month)
    TextView yearMonthDisplay;
    @Bind(R.id.amount)
    TextView amountDisplay;


    /**
     * Empty constructor
     */
    public BarGraph() {}

    /**
     * Pass data from activity into here
     * @param encodedEmail      User encoded email
     * @return                  This fragment
     */
    public static BarGraph newInstance(String encodedEmail) {

        BarGraph fragment = new BarGraph();
        Bundle args = new Bundle();
        args.putSerializable("one", encodedEmail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserEncodedEmail = getArguments().getString("one");


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_bar_graph, container, false);

        ButterKnife.bind(this, rootView);




        return rootView;

    }
}
