package cn.com.startai.socket.app.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.startai.socket.R;
import cn.com.startai.socket.debuger.impl.DetectInfo;
import cn.com.startai.socket.debuger.impl.ProductDetectionManager;

/**
 * author: Guoqiang_Sun
 * date : 2018/3/28 0028
 * desc :
 */

public class DetectionRecyclerAdapter extends Adapter<DetectionRecyclerAdapter.MyViewHolder> {

    private final List<DetectInfo> mDatas = new ArrayList<>();

    private Context mCtx;

    private String TAG = ProductDetectionManager.TAG;

    private RecyclerView mRecycleview;

    public DetectionRecyclerAdapter(Context mCtx, RecyclerView rv) {
        this.mCtx = mCtx;
//        itemTouchHelper.attachToRecyclerView(rv);
        this.mRecycleview = rv;
    }

    private OnClick mOnClick;

    public void setOnClick(OnClick mOnClick) {
        this.mOnClick = mOnClick;
    }

    public void addAllData(List<DetectInfo> tDatas) {
        mDatas.clear();
        mDatas.addAll(tDatas);
        notifyDataSetChanged();
    }

    public void refreshUI() {
        if (mRecycleview.getScrollState() == RecyclerView.SCROLL_STATE_IDLE || (!mRecycleview.isComputingLayout())) {
//            Tlog.v(TAG, " refreshUI:");
            notifyDataSetChanged();
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mCtx).inflate(R.layout.item_recycler_detection, parent, false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        DetectInfo detect = mDatas.get(position);

        holder.mNameTxt.setText(detect.mUIName);
        holder.mStartupBtn.setText(detect.startup ? "结束" : "开启");

        holder.mTotalSendCountTxt.setText(String.valueOf(detect.mTotalSendCount));
        holder.mTotalRecCountTxt.setText(String.valueOf(detect.mTotalRecCount));

        holder.mCruSendTimesTxt.setText(String.valueOf(detect.mCurSendCount));
        holder.mCurRecTimesTxt.setText(String.valueOf(detect.mCurRecCount));

        holder.mSendTimesTxt.setText(String.valueOf(detect.mMaxSendTimes));
        double ms = (detect.mSendInterval) / 1000D;
        holder.mSendIntervalTxt.setText(String.valueOf(ms));

        holder.mLinearLay.setOnClickListener(mListener);
        holder.mLinearLay.setTag(position);

    }

    private final View.OnClickListener mListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            Integer position = (Integer) v.getTag();
            DetectInfo detect = mDatas.get(position);
            if (mOnClick != null) {
                mOnClick.onClick(detect);
            }

        }
    };

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mCruSendTimesTxt;
        TextView mCurRecTimesTxt;

        TextView mTotalSendCountTxt;
        TextView mTotalRecCountTxt;

        Button mStartupBtn;
        TextView mNameTxt;

        TextView mSendTimesTxt;
        TextView mSendIntervalTxt;

        LinearLayout mLinearLay;

        MyViewHolder(View itemView) {
            super(itemView);
            mCruSendTimesTxt = itemView.findViewById(R.id.cur_send_txt);
            mCurRecTimesTxt = itemView.findViewById(R.id.cur_rec_txt);

            mTotalSendCountTxt = itemView.findViewById(R.id.total_send_txt);
            mTotalRecCountTxt = itemView.findViewById(R.id.total_rec_txt);

            mStartupBtn = itemView.findViewById(R.id.startup_btn);
            mNameTxt = itemView.findViewById(R.id.name_txt);


            mSendTimesTxt = itemView.findViewById(R.id.send_times_txt);
            mSendIntervalTxt = itemView.findViewById(R.id.send_interval_txt);

            mLinearLay = itemView.findViewById(R.id.startup_lin_lay);
        }
    }

//
//    final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
//
//        //用于设置拖拽和滑动的方向
//        @Override
//        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//            int dragFlags = 0, swipeFlags = 0;
//            if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager || recyclerView.getLayoutManager() instanceof GridLayoutManager) {
//                //网格式布局有4个方向
//                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
//            } else if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
//                //线性式布局有2个方向
//                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
//
//                swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END; //设置侧滑方向为从两个方向都可以
//            }
//            return makeMovementFlags(dragFlags, swipeFlags);//swipeFlags 为0的话item不滑动
//        }
//
//        //长摁item拖拽时会回调这个方法
//        @Override
//        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//            int from = viewHolder.getAdapterPosition();
//            int to = target.getAdapterPosition();
//            DetectInfo detect = mDatas.get(from);
//            mDatas.remove(from);
//            mDatas.add(to, detect);//交换数据链表中数据的位置
//            notifyItemMoved(from, to);//更新适配器中item的位置
//            return true;
//        }
//
//        @Override
//        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//            //这里处理滑动删除
//        }
//
//        @Override
//        public boolean isLongPressDragEnabled() {
//            return false;//返回true则为所有item都设置可以拖拽
//        }
//    });


    public interface OnClick {
        void onClick(DetectInfo detect);
    }

}
