package com.dragons.custom;

import android.content.Context;
import android.support.annotation.MenuRes;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dragons.aurora.R;

import java.util.ArrayList;

class MenuSecondaryItemsAdapter extends RecyclerView.Adapter<MenuSecondaryItemsAdapter.MenuItem> {

    private Context context;
    private View.OnClickListener onClickListener;
    private boolean keepRipple = true;

    private ArrayList<MenuEntry> itemss;

    MenuSecondaryItemsAdapter(Context context, @MenuRes int secondaryMenuId, View.OnClickListener onClickListener) {
        this.context = context;
        this.onClickListener = onClickListener;
        this.itemss = new ArrayList<>();

        MenuParserHelper.parseMenu(context, secondaryMenuId, itemss);
    }

    @Override
    public MenuItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
        return new MenuItem(v);

    }

    @Override
    public void onBindViewHolder(MenuItem holder, int position) {
        holder.label.setText(itemss.get(position).getTitle());
        holder.icon.setImageDrawable(itemss.get(position).getIcon());
        holder.itemView.setTag(itemss.get(position).getResId());

        handleRipple(holder);

        holder.itemView.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return itemss.size();
    }

    public void setKeepRipple(boolean keepRipple) {
        this.keepRipple = keepRipple;
        notifyDataSetChanged();
    }

    private void handleRipple(MenuItem holder) {
        if (!keepRipple) {
            TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            holder.itemView.setBackgroundResource(outValue.resourceId);
        }
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
