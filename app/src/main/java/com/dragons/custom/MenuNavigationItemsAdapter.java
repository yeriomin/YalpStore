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

import java.util.ArrayList;
import java.util.List;

import static com.dragons.custom.CustomAppBar.MORE_ICON_TAG;

class MenuNavigationItemsAdapter extends RecyclerView.Adapter<MenuNavigationItemsAdapter.MenuNavItem> {

    private Context context;
    private View.OnClickListener onClickListener;

    private List<MenuEntry> navItems;

    MenuNavigationItemsAdapter(Context context, @MenuRes int menuRes, View.OnClickListener onClickListener) {
        this.context = context;
        this.onClickListener = onClickListener;
        this.navItems = new ArrayList<>();

        populateNavigationItems(menuRes);
    }

    @NonNull
    @Override
    public MenuNavItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_nav_item, parent, false);
        v.getLayoutParams().width = parent.getMeasuredWidth() / navItems.size();
        return new MenuNavItem(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuNavItem holder, int position) {
        MenuEntry item = navItems.get(position);
        holder.label.setText(item.getTitle());
        holder.icon.setImageDrawable(item.getIcon());
        holder.itemView.setTag(item.getResId());

        if (item.getTitle().isEmpty()) setupMoreIcon(holder);

        holder.itemView.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return navItems.size();
    }

    private void populateNavigationItems(int menuRes) {
        MenuParserHelper.parseMenu(context, menuRes, navItems);
        //Drawable moreIcon = context.getResources().getDrawable(R.drawable.more);
        //navItems.add(new MenuEntry("", moreIcon, 0));
    }

    private void setupMoreIcon(MenuNavItem menuNavItem) {
        menuNavItem.itemView.setFocusable(false);
        menuNavItem.itemView.setFocusableInTouchMode(false);
        menuNavItem.itemView.setBackground(null);
        menuNavItem.itemView.setTag(MORE_ICON_TAG);
    }

    class MenuNavItem extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView label;

        MenuNavItem(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.nav_item_icon);
            label = (TextView) itemView.findViewById(R.id.nav_item_label);
        }
    }
}
