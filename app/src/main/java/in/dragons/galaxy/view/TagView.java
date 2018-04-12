package in.dragons.galaxy.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import in.dragons.galaxy.R;

public class TagView extends RelativeLayout{

    static int style;
    static String mono_title, dual_title0, dual_title1;
    TextView mono, dual0, dual1;
    TypedArray a;

    public TagView(Context context) {
        super(context);
        init(context, null);
    }

    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TagView, 0, 0);
        try {
            style = a.getInteger(R.styleable.TagView_TagStyle, 0);
            mono_title = a.getString(R.styleable.TagView_MonoTagName);
            dual_title0 = a.getString(R.styleable.TagView_DualTagName0);
            dual_title1 = a.getString(R.styleable.TagView_DualTagName1);
        } finally {
            a.recycle();
        }
        switch (getStyle()) {
            case 0:
                MonoTagView(context);
                break;
            case 1:
                DualTagView(context);
                break;
        }
    }

    private void MonoTagView(Context context) {
        View root = inflate(context, R.layout.tagview_mono, this);
        mono = root.findViewById(R.id.tag_mono_txt);
        if (mono_title != null)
        mono.setText(mono_title);
    }

    private void DualTagView(Context context) {
        View root = inflate(context, R.layout.tagview_duo, this);
        dual0 = root.findViewById(R.id.tag_dual_txt0);
        if (dual_title0 != null) {
            dual0.setText(dual_title0);
        }
        dual1 = root.findViewById(R.id.tag_dual_txt1);
        if (dual_title1 != null) {
            dual1.setText(dual_title1);
        }
    }

    public void setMono_title(String mono_title) {
        TagView.mono_title = mono_title;
    }

    public void setDual_title0(String dual_title0) {
        TagView.dual_title0 = dual_title0;
    }

    public void setDual_title1(String dual_title1) {
        TagView.dual_title1 = dual_title1;
    }

    public void setMonoOnClickListener(OnClickListener onClickListener) {
        mono.setOnClickListener(onClickListener);
    }

    public void setDual0OnClickListener(OnClickListener onClickListener) {
        dual0.setOnClickListener(onClickListener);
    }

    public void setDual1OnClickListener(OnClickListener onClickListener) {
        dual1.setOnClickListener(onClickListener);
    }

    public void setStyle(int style) {
        TagView.style = style;
    }

    public int getStyle() {
        return style;
    }
}
