package cn.com.startai.socket.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.com.startai.socket.R;
import cn.com.startai.socket.mutual.js.bean.CountElectricity;

/**
 * author: Guoqiang_Sun
 * date: 2018/11/16 0016
 * Desc:
 */
public class DBDataAdapter extends BaseAdapter {

    private Context mContext;
    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());

    public DBDataAdapter(Context mContext) {
        this.mContext = mContext;

    }

    private List<CountElectricity> mList = new ArrayList<>();

    public void clearAdd(List<CountElectricity> tList) {
        mList.clear();
        mList.addAll(tList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public CountElectricity getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder mViewHolder;

        if (convertView == null) {

            mViewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_db_data, null);
            mViewHolder.mIdTxt = convertView.findViewById(R.id.db_id_txt);
            mViewHolder.mMacTxt = convertView.findViewById(R.id.db_mac_txt);
            mViewHolder.mSeqTxt = convertView.findViewById(R.id.db_seq_txt);
            mViewHolder.mTsTxt = convertView.findViewById(R.id.db_ts_txt);
            mViewHolder.mBlobTxt = convertView.findViewById(R.id.db_blob_txt);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        CountElectricity countElectricity = mList.get(position);

        mViewHolder.mIdTxt.setText(String.valueOf(countElectricity.getId()));
        mViewHolder.mMacTxt.setText(String.valueOf(countElectricity.getMac()));
        mViewHolder.mSeqTxt.setText(String.valueOf(countElectricity.getComplete()));
        long timestamp = countElectricity.getTimestamp();
        String format = mFormat.format(new Date(timestamp));
        mViewHolder.mTsTxt.setText(format);
        byte[] electricity = countElectricity.getElectricity();
        mViewHolder.mBlobTxt.setText(String.valueOf(electricity == null ? 0 : electricity.length / 8));

        return convertView;
    }


    public static class ViewHolder {

        public TextView mIdTxt;
        public TextView mMacTxt;
        public TextView mSeqTxt;
        public TextView mTsTxt;
        public TextView mBlobTxt;


    }

}
