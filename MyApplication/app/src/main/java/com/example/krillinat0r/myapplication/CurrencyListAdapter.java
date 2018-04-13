package com.example.krillinat0r.myapplication;

import android.content.Context;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Krillinat0r on 03-04-2018.
 */

public class CurrencyListAdapter extends BaseAdapter {

    private Context mContext;
    private List<CurrencyData> mCurrencyDataList;

    public CurrencyListAdapter(Context mContext, List<CurrencyData> mCurrencyDataList) {
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

        View v = View.inflate(mContext, R.layout.item_currency_list, null);

        TextView coinName = v.findViewById(R.id.Coin_Name);
        coinName.setText(mCurrencyDataList.get(i).get_coinName()); //Set name

        TextView price = v.findViewById(R.id.Coin_Price);
        price.setText("$" + String.valueOf(mCurrencyDataList.get(i).get_coinPrice()));

        TextView coinChange = v.findViewById(R.id.Percent_Change);
        coinChange.setText(String.valueOf(mCurrencyDataList.get(i).get_percentChange()) + "%"); //Set coin value change

        ImageView coinIcon = v.findViewById(R.id.Coin_IMG);
        coinIcon.setImageBitmap(mCurrencyDataList.get(i).get_coinIcon());

        ImageView rateIcon = v.findViewById(R.id.Arrow_Rate_IMG);
        rateIcon.setImageBitmap(mCurrencyDataList.get(i).get_coinRate());

        return v;
    }
}