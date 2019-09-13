package com.example.mytrip.adapter;

import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mytrip.R;
import com.example.mytrip.custominterface.OnLongClickItemListener;
import com.example.mytrip.custominterface.OnMyTripInfoItemClickListener;
import com.example.mytrip.helper.Helper;
import com.example.mytrip.model.MyTripInfo;

import java.util.List;

public class MyTripRecyclerViewAdapter extends RecyclerView.Adapter<MyTripRecyclerViewAdapter.ViewHolder> {

    private static final long CLICK_TIME_INTERVAL = 1000;

    private List<MyTripInfo> myTripInfoList;
    private OnMyTripInfoItemClickListener mListener;
    private OnLongClickItemListener longClickItemListener;
    private Context context;


    public MyTripRecyclerViewAdapter(List<MyTripInfo> myTripInfos, OnMyTripInfoItemClickListener listener,
                                     OnLongClickItemListener lListener) {

        myTripInfoList = myTripInfos;
        mListener = listener;
        longClickItemListener = lListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_trip_list_item, parent, false);

        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.myTripInfo = myTripInfoList.get(position);
        holder.tvFromAddress.setText(holder.myTripInfo.getStartAddress());
        int startTimeStamp = holder.myTripInfo.getStartDateTime();
        if (startTimeStamp != 0) {

            String startDate = context.getString(R.string.start_date).concat(Helper.getDate(startTimeStamp));
            holder.tvStartDate.setText(startDate);
            String startTime = context.getString(R.string.start_time).concat(Helper.getTime(startTimeStamp));
            holder.tvStartTime.setText(startTime);
        }

        holder.tvDestAddress.setText(holder.myTripInfo.getDestinationAddress());
        int endTimeStamp = holder.myTripInfo.getEndDateTime();
        if (endTimeStamp != 0) {
            String endDate = context.getString(R.string.end_date).concat(Helper.getDate(endTimeStamp));
            holder.tvDestDate.setText(endDate);
            String endTime = context.getString(R.string.end_time).concat(Helper.getTime(endTimeStamp));
            holder.tvDestTime.setText(endTime);
        }
    }

    @Override
    public int getItemCount() {
        return myTripInfoList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView tvFromAddress;
        public final TextView tvStartDate;
        public final TextView tvStartTime;

        public final TextView tvDestAddress;
        public final TextView tvDestDate;
        public final TextView tvDestTime;

        public MyTripInfo myTripInfo;
        private long lastClickTime = 0;

        public ViewHolder(View view) {
            super(view);
            mView = view;


            tvFromAddress = view.findViewById(R.id.tv_start_address);
            tvStartDate = view.findViewById(R.id.tv_start_date);
            tvStartTime = view.findViewById(R.id.tv_start_time);

            tvDestAddress = view.findViewById(R.id.tv_dest_address);
            tvDestDate = view.findViewById(R.id.tv_dest_date);
            tvDestTime = view.findViewById(R.id.tv_dest_time);

            mView.setOnClickListener(v -> {
                if (null != mListener && !isDoubleClick()) {
                    mListener.onMyTripInfoItemClick(myTripInfo);
                }
            });

            mView.setOnLongClickListener(v -> {
                if (longClickItemListener != null) {
                    longClickItemListener.onLongClickItem(getAdapterPosition());
                }
                return true;
            });
        }

        private boolean isDoubleClick() {

            // preventing double, using threshold of 1000 ms
            if (SystemClock.elapsedRealtime() - lastClickTime < CLICK_TIME_INTERVAL) {
                return true;
            }
            lastClickTime = SystemClock.elapsedRealtime();
            return false;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tvFromAddress.getText() + "'";
        }
    }

    public void removeAt(int position) {
        try {
            myTripInfoList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, myTripInfoList.size());
        } catch (Exception e) {
        }
    }

    public MyTripInfo getAt(int position) {
        return myTripInfoList.get(position);
    }
}
