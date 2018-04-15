package in.dragons.galaxy.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.design.widget.AppBarLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import in.dragons.galaxy.R;

public class AdaptiveToolbar extends AppBarLayout {

    static int style;
    View root;
    RelativeLayout adtoolbarlayout;
    ImageView action_icon;
    ImageButton avatar_icon, download_section;
    TextView title0, title1;


    public AdaptiveToolbar(Context context) {
        super(context);
        init(context, null);
    }

    public AdaptiveToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AdaptiveToolbar, 0, 0);
        try {
            style = a.getInteger(R.styleable.AdaptiveToolbar_ToolbarStyle, 0);
        } finally {
            a.recycle();
        }
        root = inflate(context, R.layout.adaptive_toolbar, this);
        adtoolbarlayout = root.findViewById(R.id.adtoolbar_layout);
        action_icon = root.findViewById(R.id.action_icon);
        avatar_icon = root.findViewById(R.id.account_avatar);
        download_section = root.findViewById(R.id.download_section);
        title0 = root.findViewById(R.id.app_title0);
        title1 = root.findViewById(R.id.app_title1);
        switch (getStyle()) {
            case 0:
                homeToolbar(context);
                break;
            case 1:
                detailsToolbar();
                break;
        }
    }

    private void homeToolbar(Context context) {
        if (avatar_icon.getVisibility() != VISIBLE) {
            avatar_icon.setVisibility(VISIBLE);
        }
        action_icon.setImageResource(R.mipmap.ic_launcher);
        action_icon.setPadding(0,0,0,0);
        action_icon.setContentDescription("home");
        setBackgroundColor(getColorAttr(context, android.R.attr.colorPrimary));
        title0.setText("Galaxy");
        title1.setText("Store");
        title1.setVisibility(VISIBLE);
        download_section.setVisibility(VISIBLE);
    }

    private void detailsToolbar() {
        if (avatar_icon.getVisibility() != GONE) {
            avatar_icon.setVisibility(GONE);
        }
        action_icon.setImageResource(R.drawable.ic_chevron_left);
        action_icon.setPadding(6,6,6,6);
        action_icon.setContentDescription("details");
        setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    public ImageButton getDownload_section() {
        return download_section;
    }

    public TextView getTitle0() {
        return title0;
    }

    public TextView getTitle1() {
        return title1;
    }

    public ImageView getAction_icon() {
        return action_icon;
    }

    public ImageButton getAvatar_icon() {
        return avatar_icon;
    }

    public void setStyle(int style) {
        AdaptiveToolbar.style = style;
    }

    public int getStyle() {
        return style;
    }

    @ColorInt
    public static int getColorAttr(Context context, int attr) {
        TypedArray ta = context.obtainStyledAttributes(new int[]{attr});
        @ColorInt int colorAccent = ta.getColor(0, 0);
        ta.recycle();
        return colorAccent;
    }
}
