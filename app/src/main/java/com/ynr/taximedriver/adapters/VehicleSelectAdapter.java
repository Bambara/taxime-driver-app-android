package com.ynr.taximedriver.adapters;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ynr.taximedriver.R;
import com.ynr.taximedriver.model.vehicleModel.Content;

import java.util.List;

public class VehicleSelectAdapter extends RecyclerView.Adapter<VehicleSelectAdapter.MyViewHolder> {
    private List<Content> vehicleList;
    static Context context;
    private static int selected = -1;
    private static VehicleSelectAdapter.SingleClickListener clickListener;

    public VehicleSelectAdapter(Context context, List<Content> vehicleList, int selected) {
        this.context = context;
        this.vehicleList = vehicleList;
        this.selected = selected;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView selectedIconImage;
        TextView vehicaleName;
        TextView vehicleType;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.selectedIconImage = itemView.findViewById(R.id.select_image);
            this.vehicaleName = itemView.findViewById(R.id.vehicle_name);
            this.vehicleType = itemView.findViewById(R.id.txtVehicleType);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            selected = getAdapterPosition();
            clickListener.onItemClickListener(getAdapterPosition(), vehicleList);
        }

    }

    public void selectedItem() {
        notifyDataSetChanged();
    }

    public void categoryChange(int position) {
        selected = position;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(VehicleSelectAdapter.SingleClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public VehicleSelectAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_select_row_item, parent, false);
        return new VehicleSelectAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VehicleSelectAdapter.MyViewHolder holder, int position) {

        holder.vehicaleName.setText(vehicleList.get(position).getVehicleRegistrationNo());
        holder.vehicleType.setText(vehicleList.get(position).getVehicleSubCategory());

        if (selected == position ) {
            holder.selectedIconImage.setImageResource(R.drawable.right_icon);
        } else {
            holder.selectedIconImage.setImageResource(R.drawable.ic_rectangle_234);
        }

    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    public interface SingleClickListener {
        void onItemClickListener(int position, List<Content> vehicleList);
    }
}
