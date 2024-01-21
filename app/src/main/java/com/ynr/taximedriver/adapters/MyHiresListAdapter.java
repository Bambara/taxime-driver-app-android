package com.ynr.taximedriver.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ynr.taximedriver.R;
import com.ynr.taximedriver.model.HiresListModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MyHiresListAdapter extends RecyclerView.Adapter<MyHiresListAdapter.MyViewHolder> {
    private List<HiresListModel.DriverWallet> hiresList;
    static Context context;

    public MyHiresListAdapter(Context context, List<HiresListModel.DriverWallet> hiresList) {
        Collections.reverse(hiresList);
        this.context = context;
        this.hiresList = hiresList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView date, total, adminCommission, earning, pickupLocation, dropLocation;

        public MyViewHolder(View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.dateAndTime);
            total = itemView.findViewById(R.id.total);
            adminCommission = itemView.findViewById(R.id.admin_commission);
            earning = itemView.findViewById(R.id.earning);
            pickupLocation = itemView.findViewById(R.id.pickup_address);
            dropLocation = itemView.findViewById(R.id.btnDropLocation);
        }
    }

    @Override
    public MyHiresListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_hires_row_item, parent, false);
        return new MyHiresListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHiresListAdapter.MyViewHolder holder, int position) {
        holder.date.setText(timeFormat(hiresList.get(position).getTransactionHistory().getDateTime()));
        holder.total.setText(context.getResources().getString(R.string.rs) + round(hiresList.get(position).getTransactionHistory().getTrip().getTotalTripValue(), 2));
        holder.adminCommission.setText(context.getResources().getString(R.string.rs) + round(hiresList.get(position).getTransactionHistory().getTrip().getTripAdminCommission(), 2));
        holder.earning.setText(context.getResources().getString(R.string.rs) + round(hiresList.get(position).getTransactionHistory().getTrip().getTripEarning(), 2));
        holder.pickupLocation.setText(hiresList.get(position).getTransactionHistory().getTrip().getPickupLocation().getAddress());
        holder.dropLocation.setText(hiresList.get(0).getTransactionHistory().getTrip().getDestinations().get(0).getAddress());
    }

    @Override
    public int getItemCount() {
        return hiresList.size();
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
