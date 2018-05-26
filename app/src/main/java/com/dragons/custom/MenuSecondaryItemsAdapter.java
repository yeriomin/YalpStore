package com.dragons.custom;

import android.content.Context;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dragons.aurora.R;
import com.github.florent37.shapeofview.shapes.CircleView;

import java.util.ArrayList;

class MenuSecondaryItemsAdapter extends RecyclerView.Adapter<MenuSecondaryItemsAdapter.MenuItem> {

    private Context context;
    private View.OnClickListener onClickListener;

    private ArrayList<MenuEntry> items;

    private Integer[] colors = {
            R.color.colorOrange,
            R.color.colorBlue,
            R.color.colorGreen,
            R.color.colorGold,
            R.color.colorRed,
            R.color.colorCyan
    };

    MenuSecondaryItemsAdapter(Context context, @MenuRes int secondaryMenuId, View.OnClickListener onClickListener) {
        this.context = context;
        this.onClickListener = onClickListener;
        this.items = new ArrayList<>();
        MenuParserHelper.parseMenu(context, secondaryMenuId, items);
    }

    @NonNull
    @Override
    public MenuItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
        return new MenuItem(v);

    }

    @Override
    public void onBindViewHolder(@NonNull MenuItem holder, int position) {
        holder.label.setText(items.get(position).getTitle());
        holder.icon.setImageDrawable(items.get(position).getIcon());
        holder.itemView.setTag(items.get(position).getResId());
        holder.itemView.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class MenuItem extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView label;

        MenuItem(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.menu_item_icon);
            label = (TextView) itemView.findViewById(R.id.menu_item_label);
        }
    }
}
