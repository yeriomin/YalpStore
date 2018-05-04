package com.dragons.aurora.view;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dragons.aurora.R;

public class UpdatableAppBadge extends AppBadge {

    private ImageView viewChanges;
    private LinearLayout changesContainer;

    @Override
    public void draw() {
        line2.clear();
        line3.clear();
        Context c = view.getContext();
        String updated = app.getUpdated();
        if (!TextUtils.isEmpty(updated)) {
            line2.add(Formatter.formatShortFileSize(c, app.getSize()));
            line3.add(c.getString(R.string.list_line_2_updatable, updated));
        }
        if (app.isSystem()) {
            line3.add(c.getString(R.string.list_app_system));
        }
        drawMore();
        super.draw();
    }


    private void drawMore() {
        viewChanges = view.findViewById(R.id.viewChanges);
        viewChanges.setOnClickListener(v -> {
            expandMore();
        });
    }

    private void expandMore() {
        changesContainer = view.findViewById(R.id.changes_container);
        if (changesContainer.getVisibility() == View.GONE) drawChanges();
        else removeChanges();
    }

    private void drawChanges() {
        String changelog = app.getChanges();
        TextView changes = view.findViewById(R.id.changes_upper);
        if (changelog.isEmpty())
            changes.setText("Changelog not Available");
        else
            changes.setText(Html.fromHtml(app.getChanges()).toString());
        changesContainer.setVisibility(View.VISIBLE);
        viewChanges.setImageResource(R.drawable.ic_expand_less);
    }

    private void removeChanges() {
        changesContainer.setVisibility(View.GONE);
        viewChanges.setImageResource(R.drawable.ic_expand_more);
    }
}
