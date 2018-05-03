package com.example.krillinat0r.myapplication;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Krillinat0r on 03-04-2018.
 */

public class TrendingCurrencyListAdapter extends BaseAdapter {
    private Context mContext;
    private List<TrendingCurrency> mCurrencyDataList;

    public TrendingCurrencyListAdapter(Context mContext, List<TrendingCurrency> mCurrencyDataList) {
        this.mContext = mContext;
        this.mCurrencyDataList = mCurrencyDataList;
    }

    @Override
    public int getCount() {
        return mCurrencyDataList.size();
    }

    @Override
    public Object getItem(int i) {
        return mCurrencyDataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View v = View.inflate(mContext, R.layout.item_trendingcurrency_list, null);

        TextView currencyName = v.findViewById(R.id.txtTitle);
        currencyName.setText(mCurrencyDataList.get(i).getTitle());

        TextView description = v.findViewById(R.id.txtDescription);
        description.setText(mCurrencyDataList.get(i).getDescription());

        TextView date = v.findViewById(R.id.txtDate);
        date.setText(String.valueOf(mCurrencyDataList.get(i).getDate()));

        return v;
    }
}