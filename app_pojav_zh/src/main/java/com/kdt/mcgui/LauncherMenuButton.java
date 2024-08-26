package com.kdt.mcgui;

import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import net.kdt.pojavlaunch.R;

import fr.spse.extended_view.ExtendedButton;

public class LauncherMenuButton extends ExtendedButton {

    public LauncherMenuButton(@NonNull Context context) {
        super(context);
        setSettings();
    }
    public LauncherMenuButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setSettings();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        post(() -> {
            setPivotX(getWidth() / 2f);
            setPivotY(getHeight() / 2f);
            setStateListAnimator(AnimatorInflater.loadStateListAnimator(getContext(), R.xml.anim_scale));
        });
    }

    /** Set style stuff */
    private void setSettings(){
        Resources resources = getContext().getResources();

        int padding = resources.getDimensionPixelSize(R.dimen._22sdp);
        setCompoundDrawablePadding(padding);
        setPaddingRelative(padding, 0, padding, 0);
        setGravity(Gravity.CENTER_VERTICAL);

        setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen._12ssp));

        // Set drawable size
        int[] sizes = getExtendedViewData().getSizeCompounds();
        sizes[0] = resources.getDimensionPixelSize(R.dimen._30sdp);
        getExtendedViewData().setSizeCompounds(sizes);
        postProcessDrawables();
    }
}
