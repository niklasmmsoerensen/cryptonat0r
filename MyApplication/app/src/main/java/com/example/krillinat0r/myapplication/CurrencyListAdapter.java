package com.example.krillinat0r.myapplication;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Krillinat0r on 03-04-2018.
 */

public class CurrencyListAdapter extends BaseAdapter {

    private final String baseUrl = "https://www.cryptocompare.com";

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

        TextView coinName = v.findViewById(R.id.txtTitle);
        coinName.setText(mCurrencyDataList.get(i).getKey()); //Set name

        TextView price = v.findViewById(R.id.Coin_Price);
        price.setText("$" + String.valueOf(mCurrencyDataList.get(i).getCoinPrice()));

        DecimalFormat df = new DecimalFormat("#.##");
        TextView coinChange = v.findViewById(R.id.Percent_Change);



        if(mCurrencyDataList.get(i).getPercentChange() > 0) {
            coinChange.setText("+" + String.valueOf(df.format(mCurrencyDataList.get(i).getPercentChange())) + "%"); //Set coin value change
            coinChange.setTextColor(this.mContext.getResources().getColor(R.color.PositivePrice));
            mCurrencyDataList.get(i).setCoinRate(BitmapFactory.decodeResource(this.mContext.getResources(),
                    R.drawable.arrow_up));
        }
        else {
            coinChange.setText(String.valueOf(df.format(mCurrencyDataList.get(i).getPercentChange())) + "%");
            coinChange.setTextColor(this.mContext.getResources().getColor(R.color.NegativePrice));
            mCurrencyDataList.get(i).setCoinRate(BitmapFactory.decodeResource(this.mContext.getResources(),
                    R.drawable.arrow_down));
        }

        ImageView coinIcon = v.findViewById(R.id.Coin_IMG);
        Picasso.get().load(baseUrl + mCurrencyDataList.get(i).getCoinIconUrl()).into(coinIcon);

        ImageView rateIcon = v.findViewById(R.id.Arrow_Rate_IMG);
        rateIcon.setImageBitmap(mCurrencyDataList.get(i).getCoinRate());

        return v;
    }
}