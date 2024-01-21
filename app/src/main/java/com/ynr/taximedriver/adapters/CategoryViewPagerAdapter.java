package com.ynr.taximedriver.adapters;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class CategoryViewPagerAdapter extends PagerAdapter {

    List<View> views;
    Context context;

    public CategoryViewPagerAdapter(List<View> views, Context context) {
        this.views = views;
        this.context = context;
    }

    public View getView(int position) {
        return views.get(position);
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = views.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public int getItemPosition(Object object) {
        for (int index = 0; index < getCount(); index++) {
            if ((View) object == views.get(index)) {
                return index;
            }
        }
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "View " + (position + 1);
    }
}

//public class CategoryViewPagerAdapter extends PagerAdapter implements CategoryAdapter.SingleClickListener {
//    private List<Category> categories;
//    private static Context context;
//    TextView categoryName;
//    CategoryAdapter subcategoryAdapter;
//    RecyclerView recyclerView;
//
//    public CategoryViewPagerAdapter(Context context, List<Category> categories) {
//        this.context = context;
//        this.categories = categories;
//
//    }
//
//    @Override
//    public int getCount() {
//        return categories.size();
//    }
//
//    @Override
//    public boolean isViewFromObject(View view, Object object) {
//        return view == object;
//    }
//
//    @Override
//    public Object instantiateItem(ViewGroup container, final int position) {
//        LayoutInflater inflater = LayoutInflater.from(context);
//        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.category_viewpager_item, container, false);
//
//        categoryName = layout.findViewById(R.id.category_name);
//        recyclerView = layout.findViewById(R.id.recycler_view);
//
//        categoryName.setText(categories.get(position).getCategoryName());
//        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.HORIZONTAL,false));
//        recyclerView.setHasFixedSize(true);
//        subcategoryAdapter = new CategoryAdapter(context, categories, position);
//        recyclerView.setAdapter(subcategoryAdapter);
//        //RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.HORIZONTAL,false).getOrientation());
//        //recyclerView.addItemDecoration(itemDecoration);
//        subcategoryAdapter.setOnItemClickListener(this);
//
//        container.addView(layout);
//        return layout;
//    }
//
//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
//        container.removeView((View) object);
//    }
//
//    public static int dpToPx(int dp) {
//        float density = context.getResources().getDisplayMetrics().density;
//        return Math.round((float) dp * density);
//    }
//
//    @Override
//    public void onItemClickListener(int position, View view) {
//        subcategoryAdapter.selectedItem();
//    }
//}
