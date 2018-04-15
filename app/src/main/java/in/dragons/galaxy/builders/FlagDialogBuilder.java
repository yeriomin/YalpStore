package in.dragons.galaxy.builders;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.dragons.aurora.playstoreapiv2.GooglePlayAPI;

import in.dragons.galaxy.activities.GalaxyActivity;
import in.dragons.galaxy.R;
import in.dragons.galaxy.model.App;
import in.dragons.galaxy.task.playstore.FlagTask;

public class FlagDialogBuilder {

    static private final GooglePlayAPI.ABUSE[] reasonIds = new GooglePlayAPI.ABUSE[]{
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

    private GalaxyActivity activity;
    private App app;

    public FlagDialogBuilder setActivity(GalaxyActivity activity) {
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

    public AlertDialog build() {
        return new AlertDialog.Builder(activity)
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
                                    new ExplanationDialogBuilder().setContext(activity).setTask(task).setReason(reason).build().show();
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

        private Context context;
        private FlagTask task;
        private GooglePlayAPI.ABUSE reason;

        public ExplanationDialogBuilder setContext(Context context) {
            this.context = context;
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

        public AlertDialog build() {
            final EditText editText = new EditText(context);
            return new AlertDialog.Builder(context)
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
