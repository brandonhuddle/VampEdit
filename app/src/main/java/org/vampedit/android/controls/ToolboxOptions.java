package org.vampedit.android.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import org.vampedit.android.R;

public class ToolboxOptions extends FrameLayout {
    public ToolboxOptions(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public ToolboxOptions(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ToolboxOptions(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        View view = inflate(getContext(), R.layout.control_toolbox_options, null);
        addView(view);
    }
}
