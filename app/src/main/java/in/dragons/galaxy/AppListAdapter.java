package in.dragons.galaxy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import in.dragons.galaxy.view.ListItem;


public class AppListAdapter extends ArrayAdapter<ListItem> {

    private int resourceId;
    private LayoutInflater inflater;

    public AppListAdapter(Context context, int resourceId) {
        super(context, resourceId);
        this.resourceId = resourceId;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view = null == convertView ? inflater.inflate(resourceId, parent, false) : convertView;
        ListItem listItem = getItem(position);
        listItem.setView(view);
        listItem.draw();
        ImageView menu3dot = (ImageView) view.findViewById(R.id.menu_3dot);
        menu3dot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.showContextMenu();
            }
        });
        return view;
    }
}