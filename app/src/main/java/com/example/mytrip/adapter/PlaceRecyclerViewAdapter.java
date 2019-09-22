package com.example.mytrip.adapter;

import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mytrip.R;
import com.example.mytrip.custominterface.OnFavouritePlaceListener;
import com.example.mytrip.custominterface.OnPlaceSelectedListener;
import com.example.mytrip.model.Place;

import java.util.ArrayList;
import java.util.List;

public class PlaceRecyclerViewAdapter extends RecyclerView.Adapter<PlaceRecyclerViewAdapter.ViewHolder> {

    private static final long CLICK_TIME_INTERVAL = 1000;
    public static OnFavouritePlaceListener onFavouritePlaceListener;

    private List<Place> placeList;
    private OnPlaceSelectedListener mListener;


    public PlaceRecyclerViewAdapter(List<Place> places, OnPlaceSelectedListener listener) {

        placeList = places;
        mListener = listener;
    }

    public void setData(List<Place> places) {

        placeList = places;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.address_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.place = placeList.get(position);
        holder.tvName.setText(holder.place.getName());
        holder.tvFullAddress.setText(holder.place.getFullAddress());
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        final View mView;
        private final TextView tvName;
        private final TextView tvFullAddress;

        private Place place;
        private long lastClickTime = 0;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            tvName = view.findViewById(R.id.tv_primary_address);
            tvFullAddress = view.findViewById(R.id.tv_secondary_address);
            mView.setOnClickListener(v -> {
                if (null != mListener && !isDoubleClick()) {
                    mListener.onPlaceSelected(place);
                }
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
    }

    public void removeAt(int position) {
        try {
            placeList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, placeList.size());
        } catch (Exception e) {
        }
    }

    public Place getAt(int position) {
        return placeList.get(position);
    }
}
