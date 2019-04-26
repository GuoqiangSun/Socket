package cn.com.startai.socket.app.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cn.com.startai.socket.R;
import cn.com.startai.socket.app.adapter.DBDataAdapter;
import cn.com.startai.socket.db.gen.CountElectricityDao;
import cn.com.startai.socket.db.manager.DBManager;
import cn.com.startai.socket.debuger.Debuger;
import cn.com.startai.socket.global.Utils.DateUtils;
import cn.com.startai.socket.mutual.Controller;
import cn.com.startai.socket.mutual.js.bean.CountElectricity;
import cn.com.startai.socket.sign.scm.impl.SocketScmManager;
import cn.com.startai.socket.sign.scm.util.MySocketDataCache;
import cn.com.swain.baselib.log.Tlog;
import cn.com.swain.support.protocolEngine.pack.ResponseData;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/13 0013
 * desc :
 */

public class DBFragment extends BaseFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tlog.v(TAG, " DBFragment onCreate() ");
    }

    @Override
    protected View inflateView() {
        Tlog.v(TAG, " DBFragment inflateView() ");
        View inflate = View.inflate(getActivity(), R.layout.fragment_db,
                null);

        ListView mDBDataLsv = inflate.findViewById(R.id.data_lstv);
        DBDataAdapter mDBDataAdapter = new DBDataAdapter(getContext());
        mDBDataLsv.setAdapter(mDBDataAdapter);
        View mHeadView = View.inflate(getContext(), R.layout.item_db_data, null);
        mDBDataLsv.addHeaderView(mHeadView);
        mDBDataLsv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                int i = position - 1;
                if (i >= 0) {
                    showDialog(mDBDataAdapter.getItem(i));
                }

                return false;
            }
        });

        mDBDataLsv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int i = position - 1;
                if (i >= 0) {
                    showDialog(mDBDataAdapter.getItem(i));
                }
            }
        });


        Button mQueryBtn = inflate.findViewById(R.id.query_btn);
        mQueryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountElectricityDao countElectricityDao =
                        DBManager.getInstance().getDaoSession().getCountElectricityDao();

                List<CountElectricity> list = countElectricityDao.queryBuilder()
                        .where(CountElectricityDao.Properties.Mac.eq(
                                Debuger.getInstance().getProductDevice())).list();

                mDBDataAdapter.clearAdd(list);

            }
        });

        return inflate;

    }

    private void queryFromServer(long currentTimeMillis) {
        Date mStartDate = new Date(currentTimeMillis);
        Date mEndDate = new Date(currentTimeMillis + DateUtils.ONE_DAY);

        ResponseData mResponseData = MySocketDataCache.getQueryHistoryCount(Debuger.getInstance().getProductDevice(),
                mStartDate, mEndDate);

        SocketScmManager scmManager = Controller.getInstance().getScmManager();

        if (scmManager != null) {
            scmManager.onOutputProtocolData(mResponseData);
        } else {
            Toast.makeText(getContext(), " scmManager is null", Toast.LENGTH_SHORT).show();
        }

    }


    private void showDialog(CountElectricity mCountElectricity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setIcon(R.mipmap.ic_launcher);

        float total = 0f;
        SimpleDateFormat mFormat = new SimpleDateFormat("MM/dd HH:mm:ss", Locale.getDefault());
        //    指定下拉列表的显示数据
        ArrayList<String> data = new ArrayList<>();

        byte[] electricity = null;

        if (mCountElectricity != null) {
            electricity = mCountElectricity.getElectricity();
        }

        int onHourSum = 0;
        long timestamp;

        if (electricity != null) {

            timestamp = mCountElectricity.getTimestamp();
            data.add("From Device: " + mFormat.format(timestamp));

            if (electricity.length >= 8) {

                DecimalFormat df = new DecimalFormat("0.000");
                DecimalFormat dff = new DecimalFormat("000");
                int count = electricity.length / 8;


                byte[] countData = new byte[CountElectricity.ONE_PKG_LENGTH];

                for (int i = 0; i < count; i++) {

                    long curMillis = timestamp + (i + 1) * 1000 * 60 * 5;

                    System.arraycopy(electricity,
                            i * CountElectricity.ONE_PKG_LENGTH,
                            countData,
                            0,
                            CountElectricity.ONE_PKG_LENGTH);

                    int e = (countData[0] & 0xFF) << 24 | (countData[1] & 0xFF) << 16
                            | (countData[2] & 0xFF) << 8 | (countData[3] & 0xFF);

                    int s = (countData[4] & 0xFF) << 24 | (countData[5] & 0xFF) << 16
                            | (countData[6] & 0xFF) << 8 | (countData[7] & 0xFF);


                    onHourSum += e;
                    total += e;

                    data.add(
                            dff.format(i) + ".  " +
                                    mFormat.format(new Date(curMillis)) +
                                    " -e:" + df.format(e)
//                                +
//                                " -s:" + df.format(s)
                    )
                    ;
                    if ((i + 1) % CountElectricity.SIZE_ONE_HOUR == 0) {
                        data.add("Hour Of Sum " + ((i + 1) / CountElectricity.SIZE_ONE_HOUR) + " h -e:" + onHourSum);
                        onHourSum = 0;
                    }
                }
            }else {
                data.add(" null ");
            }

        } else {
            timestamp = System.currentTimeMillis();
            data.add("query from device: " + mFormat.format(timestamp));
            data.add(" null ");
        }

        String[] cities = data.toArray(new String[0]);
        builder.setTitle("电量:" + String.valueOf(total));

        //    设置一个下拉的列表选择项
        builder.setItems(cities, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    queryFromServer(timestamp);
                }
            }
        });
        builder.show();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Tlog.v(TAG, " DBFragment onCreateView() ");
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Tlog.v(TAG, " DBFragment onDestroyView() ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Tlog.v(TAG, " DBFragment onDestroy() ");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tlog.v(TAG, " DBFragment onActivityResult ");
        super.onActivityResult(requestCode, resultCode, data);
    }
}
