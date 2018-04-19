package com.dragons.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.MenuRes;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.dragons.aurora.R;

import static com.dragons.custom.CustomAppBar.MORE_ICON_TAG;

/**
 * Created by Valentin on 14/06/2017.
 */

class MenuNavigationItemsAdapter extends RecyclerView.Adapter<MenuNavigationItemsAdapter.MenuNavItem> {

    private Context context;
    private View.OnClickListener onClickListener;
    private int foregroundColour;
    private boolean keepRipple = true;

    private List<MenuEntry> navItems;

    MenuNavigationItemsAdapter(Context context, @MenuRes int menuRes, View.OnClickListener onClickListener,
                                      @ColorInt int foregroundColour) {
        this.context = context;
        this.onClickListener = onClickListener;
        this.foregroundColour = foregroundColour;
        this.navItems = new ArrayList<>();

        populateNavigationItems(menuRes);
    }

    @Override
    public MenuNavItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_nav_item, parent, false);
        v.getLayoutParams().width = parent.getMeasuredWidth() / navItems.size();
        return new MenuNavItem(v);
    }

    @Override
    public void onBindViewHolder(MenuNavItem holder, int position) {
        MenuEntry item = navItems.get(position);
        holder.label.setText(item.getTitle());
        holder.label.setTextColor(foregroundColour);
        holder.icon.setImageDrawable(item.getIcon());
        holder.icon.setColorFilter(foregroundColour);
        holder.itemView.setTag(item.getResId());

        if (item.getTitle().isEmpty()) setupMoreIcon(holder);

        handleRipple(holder);

        holder.itemView.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return navItems.size();
    }

    public void setForegroundColour(@ColorInt int foregroundColour) {
        this.foregroundColour = foregroundColour;
    }

    public void setKeepRipple(boolean keepRipple) {
        this.keepRipple = keepRipple;
        notifyDataSetChanged();
    }

    private void handleRipple(MenuNavigationItemsAdapter.MenuNavItem holder) {
        if (!keepRipple) {
            TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            holder.itemView.setBackgroundResource(outValue.resourceId);
        }
    }

    private void populateNavigationItems(int menuRes){
        MenuParserHelper.parseMenu(context, menuRes, navItems);
        Drawable moreIcon = context.getResources().getDrawable(R.drawable.more);
        navItems.add(new MenuEntry("", moreIcon, 0));
    }

    private void setupMoreIcon(MenuNavItem menuNavItem){
        menuNavItem.itemView.setFocusable(false);
        menuNavItem.itemView.setFocusableInTouchMode(false);
        menuNavItem.itemView.setBackground(null);
        menuNavItem.icon.setColorFilter(foregroundColour);

        menuNavItem.itemView.setTag(MORE_ICON_TAG);
    }

    class MenuNavItem extends RecyclerView.ViewHolder{

        ImageView icon;
        TextView label;

        MenuNavItem(View itemView){
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.nav_item_icon);
            label = (TextView) itemView.findViewById(R.id.nav_item_label);
        }
    }
}
