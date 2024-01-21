package com.ynr.taximedriver.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ynr.taximedriver.R;
import com.ynr.taximedriver.model.DispatchHistoryModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DispatchHistoryAdapter extends RecyclerView.Adapter<DispatchHistoryAdapter.MyViewHolder> {
    private List<DispatchHistoryModel.DispatchHistory> dispatchList;
    static Context context;

    public DispatchHistoryAdapter(Context context, List<DispatchHistoryModel.DispatchHistory> dispatchList) {
        Collections.reverse(dispatchList);
        this.context = context;
        this.dispatchList = dispatchList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView pickupLocation, dropLocation, date, distance, status, tripCost;

        public MyViewHolder(View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.dateAndTime);
            pickupLocation = itemView.findViewById(R.id.pickup_address);
            dropLocation = itemView.findViewById(R.id.btnDropLocation);
            distance = itemView.findViewById(R.id.tvTripDistance);
            status = itemView.findViewById(R.id.trip_status);
            tripCost = itemView.findViewById(R.id.trip_cost);
        }
    }

    @Override
    public DispatchHistoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dispatch_history_row_item, parent, false);
        return new DispatchHistoryAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DispatchHistoryAdapter.MyViewHolder holder, int position) {
//        holder.date.setText(timeFormat(dispatchList.get(position).getRecordedTime()));
//        try {
//            holder.pickupLocation.setText(dispatchList.get(position).getRealPickupLocation().getAddress());
//            holder.dropLocation.setText(dispatchList.get(position).getRealDropLocation().getAddress());
//            holder.tripCost.setText(context.getResources().getString(R.string.rs) + " " + round(dispatchList.get(position).getTotalPrice(), 2));
//        } catch (Exception e) {
//            holder.pickupLocation.setText(dispatchList.get(position).getPickupLocation().getAddress());
//            holder.dropLocation.setText(dispatchList.get(position).getDropLocations().get(0).getAddress());
//            holder.tripCost.setText(context.getResources().getString(R.string.rs) + " " + round(dispatchList.get(position).getHireCost(), 2));
//        }
//        holder.distance.setText(String.valueOf(round(dispatchList.get(position).getDistance(), 2)) + " " + context.getResources().getString(R.string.km));
//        if (dispatchList.get(position).getStatus().equals(KeyString.ACCEPTED)) {
//            holder.status.setText(context.getResources().getString(R.string.on_going));
//            holder.status.setTextColor(context.getResources().getColor(R.color.greenOne));
//        } else if (dispatchList.get(position).getStatus().equals(KeyString.DONE)) {
//            holder.status.setText(context.getResources().getString(R.string.completed));
//            holder.status.setTextColor(context.getResources().getColor(R.color.blueOne));
//        } else if (dispatchList.get(position).getStatus().equals(KeyString.CANCELED)) {
//            holder.status.setText(context.getResources().getString(R.string.cancelled));
//            holder.status.setTextColor(context.getResources().getColor(R.color.redOne));
//        } else {
//            holder.status.setText(context.getResources().getString(R.string.pending));
//            holder.status.setTextColor(context.getResources().getColor(R.color.yellowOne));
//        }
    }

    @Override
    public int getItemCount() {
        return dispatchList.size();
    }

    /**
     * convert UTC time to local time
     * @param dateTime
     * @return
     */
    private String timeFormat(String dateTime) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat localFormat = new SimpleDateFormat("yyyy/MM/dd',' HH:mm");
        localFormat.setTimeZone(TimeZone.getTimeZone("colombo"));
        return localFormat.format(date);
    }

    /**
     * round double value with two floating point
     * @param value
     * @param places
     * @return
     */
    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
