/*
 * Yalp Store
 * Copyright (C) 2018 Sergey Yeriomin <yeriomin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.github.yeriomin.yalpstore.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.YalpStoreActivity;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.task.playstore.FlagTask;

public class FlagDialogBuilder {

    static private final GooglePlayAPI.ABUSE[] reasonIds = new GooglePlayAPI.ABUSE[] {
        GooglePlayAPI.ABUSE.SEXUAL_CONTENT,
        GooglePlayAPI.ABUSE.GRAPHIC_VIOLENCE,
        GooglePlayAPI.ABUSE.HATEFUL_OR_ABUSIVE_CONTENT,
        GooglePlayAPI.ABUSE.HARMFUL_TO_DEVICE_OR_DATA,
        GooglePlayAPI.ABUSE.IMPROPER_CONTENT_RATING,
        GooglePlayAPI.ABUSE.ILLEGAL_PRESCRIPTION,
        GooglePlayAPI.ABUSE.IMPERSONATION,
        GooglePlayAPI.ABUSE.OTHER,
    };
    static private final String[] reasonLabels = new String[8];

    private YalpStoreActivity activity;
    private App app;

    public FlagDialogBuilder setActivity(YalpStoreActivity activity) {
        this.activity = activity;
        reasonLabels[0] = activity.getString(R.string.flag_sexual_content);
        reasonLabels[1] = activity.getString(R.string.flag_graphic_violence);
        reasonLabels[2] = activity.getString(R.string.flag_hateful_content);
        reasonLabels[3] = activity.getString(R.string.flag_harmful_to_device);
        reasonLabels[4] = activity.getString(R.string.flag_improper_content_rating);
        reasonLabels[5] = activity.getString(R.string.flag_pharma_content);
        reasonLabels[6] = activity.getString(R.string.flag_impersonation_copycat);
        reasonLabels[7] = activity.getString(R.string.flag_other_objection);
        return this;
    }

    public FlagDialogBuilder setApp(App app) {
        this.app = app;
        return this;
    }

    public DialogWrapperAbstract build() {
        return new DialogWrapper(activity)
            .setTitle(R.string.flag_page_description)
            .setNegativeButton(
                android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
            )
            .setAdapter(
                new ArrayAdapter<>(activity, android.R.layout.select_dialog_item, reasonLabels),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        FlagTask task = new FlagTask();
                        task.setContext(activity);
                        task.setApp(app);
                        GooglePlayAPI.ABUSE reason = reasonIds[which];
                        task.setReason(reason);
                        if (reason == GooglePlayAPI.ABUSE.HARMFUL_TO_DEVICE_OR_DATA || reason == GooglePlayAPI.ABUSE.OTHER) {
                            new ExplanationDialogBuilder().setActivity(activity).setTask(task).setReason(reason).build().show();
                        } else {
                            task.execute();
                        }
                        dialog.dismiss();
                    }
                }
            )
            .create()
        ;
    }

    private static class ExplanationDialogBuilder {

        private Activity activity;
        private FlagTask task;
        private GooglePlayAPI.ABUSE reason;

        public ExplanationDialogBuilder setActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public ExplanationDialogBuilder setTask(FlagTask task) {
            this.task = task;
            return this;
        }

        public ExplanationDialogBuilder setReason(GooglePlayAPI.ABUSE reason) {
            this.reason = reason;
            return this;
        }

        public DialogWrapperAbstract build() {
            final EditText editText = new EditText(activity);
            return new DialogWrapper(activity)
                .setTitle(reason == GooglePlayAPI.ABUSE.HARMFUL_TO_DEVICE_OR_DATA
                    ? R.string.flag_harmful_prompt
                    : R.string.flag_other_concern_prompt
                )
                .setView(editText)
                .setNegativeButton(
                    android.R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }
                )
                .setPositiveButton(
                    android.R.string.yes,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            task.setExplanation(editText.getText().toString());
                            task.execute();
                            dialog.dismiss();
                        }
                    }
                )
                .create()
            ;
        }
    }
}
