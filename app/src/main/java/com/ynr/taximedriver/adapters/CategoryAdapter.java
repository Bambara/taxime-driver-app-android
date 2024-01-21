package com.ynr.taximedriver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ynr.taximedriver.R;
import com.ynr.taximedriver.model.vehicalCategoryModel.VehicleCategoryResponseModel;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {
    private final List<VehicleCategoryResponseModel.Content> categories;
    private final Context context;
    private int selected;
    private static SingleClickListener clickListener;

    public CategoryAdapter(Context context, VehicleCategoryResponseModel categories, int selected) {
        this.context = context;
        this.categories = categories.getContent();
        this.selected = selected;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView subcategoryImage;
        TextView subcategoryName;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.subcategoryImage = itemView.findViewById(R.id.subcategory_image);
            this.subcategoryName = itemView.findViewById(R.id.tvSubCategory);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int currentSelection = selected;
            selected = getAdapterPosition();
            notifyItemChanged(currentSelection);
            notifyItemChanged(selected);
            clickListener.onItemClickListener(getAdapterPosition(), categories);
        }
    }

    public void setOnItemClickListener(SingleClickListener clickListener) {
        CategoryAdapter.clickListener = clickListener;
    }

    @Override
    public CategoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_recyclerview_item, parent, false);
        return new CategoryAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CategoryAdapter.MyViewHolder holder, final int position) {
        if (selected == position) {
            Glide.with(context).load(categories.get(position).getSubCategoryIconSelected()).into(holder.subcategoryImage);
            holder.subcategoryName.setText(categories.get(position).getSubCategoryName());
            holder.subcategoryImage.setColorFilter(null);
        } else {
            Glide.with(context).load(categories.get(position).getSubCategoryIcon()).into(holder.subcategoryImage);
            holder.subcategoryName.setText(categories.get(position).getSubCategoryName());
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public interface SingleClickListener {
        void onItemClickListener(int position, List<VehicleCategoryResponseModel.Content> categories);
    }
}
