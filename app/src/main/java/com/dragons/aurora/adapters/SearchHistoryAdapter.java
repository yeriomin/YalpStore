package com.dragons.aurora.adapters;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dragons.aurora.R;
import com.dragons.aurora.activities.SearchActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder> {

    public ArrayList<String> queryHistory;
    private Context c;

    public SearchHistoryAdapter(ArrayList<String> queryHistory, Context c) {
        this.queryHistory = queryHistory;
        this.c = c;
    }

    public void add(int position, String query) {
        queryHistory.add(position, query);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        queryHistory.remove(position);
        notifyItemRemoved(position);
    }

    public void remove(String query) {
        queryHistory.remove(query);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHistoryAdapter.ViewHolder holder, final int position) {
        setQuery(holder.query, holder.time, queryHistory.get(position));
        holder.viewForeground.setOnClickListener(v -> {
            Intent i = new Intent(c.getApplicationContext(), SearchActivity.class);
            i.setAction(Intent.ACTION_SEARCH);
            i.putExtra(SearchManager.QUERY, holder.query.getText());
            c.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return queryHistory.size();
    }

    private void setQuery(TextView name, TextView time, String datedQuery) {
        String[] temp = datedQuery.split(":");
        name.setText(temp[0]);
        time.setText(getDiffString((int) getDiff(temp[1])));
    }

    private long getDiff(String queryDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date curDate = simpleDateFormat.parse(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()));
            return TimeUnit.DAYS.convert(curDate.getTime() - simpleDateFormat.parse(queryDate).getTime(), TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private String getDiffString(int diff) {
        if (diff == 0)
            return "Today";
        if (diff == 1)
            return "Yesterday";
        else if (diff > 1)
            return diff + " days before";
        return "";
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout viewForeground;
        RelativeLayout viewBackground;
        TextView query;
        TextView time;

        ViewHolder(View view) {
            super(view);
            query = view.findViewById(R.id.query);
            time = view.findViewById(R.id.queryTime);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
        }
    }
}
