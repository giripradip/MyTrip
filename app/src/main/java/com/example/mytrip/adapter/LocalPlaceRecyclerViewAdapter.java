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

public class LocalPlaceRecyclerViewAdapter extends RecyclerView.Adapter<LocalPlaceRecyclerViewAdapter.ViewHolder> implements Filterable {

    private static final long CLICK_TIME_INTERVAL = 1000;
    public static OnFavouritePlaceListener onFavouritePlaceListener;

    private List<Place> placeList;
    private List<Place> placeListCopy;
    private OnPlaceSelectedListener mListener;
    private Context context;


    public LocalPlaceRecyclerViewAdapter(List<Place> places, OnPlaceSelectedListener listener) {

        placeList = places;
        mListener = listener;
        placeListCopy = new ArrayList<>(places);
    }

    public void setData(List<Place> places) {

        placeList = places;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.address_item, parent, false);

        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.place = placeList.get(position);
        holder.tvName.setText(holder.place.getName());
        holder.tvFullAddress.setText(holder.place.getFullAddress());
        if (holder.place.isFavourite()) {
            holder.ivFav.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_selected_24dp));
        } else {
            holder.ivFav.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_blank_24dp));
        }
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private final View mView;
        private final TextView tvName;
        private final TextView tvFullAddress;
        private final ImageView ivFav;

        private Place place;
        private long lastClickTime = 0;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            tvName = view.findViewById(R.id.tv_primary_address);
            tvFullAddress = view.findViewById(R.id.tv_secondary_address);
            ivFav = view.findViewById(R.id.iv_fav);
            ivFav.setVisibility(View.VISIBLE);
            mView.setOnClickListener(v -> {
                if (null != mListener && !isDoubleClick()) {
                    mListener.onPlaceSelected(place);
                }
            });

            ivFav.setOnClickListener(view1 -> {
                if (place.isFavourite()) {
                    ivFav.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_blank_24dp));
                    place.setFavourite(false);
                    onFavouritePlaceListener.onPlaceFavourite(place);
                    return;
                }
                place.setFavourite(true);
                ivFav.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_selected_24dp));
                onFavouritePlaceListener.onPlaceFavourite(place);
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
            placeListCopy.remove(placeList.get(position));
            placeList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, placeList.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Place getAt(int position) {
        return placeList.get(position);
    }

    public List<Place> getPlaceList() {
        return placeList;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString().trim().toLowerCase();
                try {
                    if (charString.isEmpty()) {
                        placeList = placeListCopy;
                    } else {
                        List<Place> filteredList = new ArrayList<>();
                        for (Place row : placeListCopy) {
                            // here we are looking for name or address match
                            if (row.getName().trim().toLowerCase().contains(charString)
                                    || row.getFullAddress().trim().contains(charString)) {
                                filteredList.add(row);
                            }
                        }
                        placeList = filteredList;
                    }
                } catch (Exception e) {
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = placeList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                placeList = (ArrayList<Place>) filterResults.values;
                String charString = charSequence.toString().trim();
                if (!charString.isEmpty()) {
                    if (placeList.size() > 3)
                        placeList.subList(3, placeList.size()).clear();
                }
                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }
}
