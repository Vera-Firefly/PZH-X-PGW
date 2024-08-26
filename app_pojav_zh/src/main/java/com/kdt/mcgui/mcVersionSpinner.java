package com.kdt.mcgui;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static net.kdt.pojavlaunch.fragments.ProfileEditorFragment.DELETED_PROFILE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.transition.Slide;
import android.transition.Transition;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.daimajia.androidanimations.library.Techniques;
import com.movtery.pojavzh.utils.ZHTools;
import com.movtery.pojavzh.utils.anim.ViewAnimUtils;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.extra.ExtraConstants;
import net.kdt.pojavlaunch.extra.ExtraCore;
import net.kdt.pojavlaunch.fragments.ProfileTypeSelectFragment;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.profiles.ProfileAdapter;
import net.kdt.pojavlaunch.profiles.ProfileAdapterExtra;

import fr.spse.extended_view.ExtendedTextView;

/**
 * A class implementing custom spinner like behavior, notably:
 * dropdown popup view with a custom direction.
 */
public class mcVersionSpinner extends ExtendedTextView {
    private Fragment mParentFragment;
    private static final int VERSION_SPINNER_PROFILE_CREATE = 0;
    public mcVersionSpinner(@NonNull Context context) {
        super(context);
        init();
    }
    public mcVersionSpinner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public mcVersionSpinner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /* The class is in charge of displaying its own list with adapter content being known in advance */
    private ListView mListView = null;
    private PopupWindow mPopupWindow = null;
    private Object mPopupAnimation;
    private int mSelectedIndex;

    private final ProfileAdapter mProfileAdapter = new ProfileAdapter(new ProfileAdapterExtra[]{
            new ProfileAdapterExtra(VERSION_SPINNER_PROFILE_CREATE,
                    R.string.create_profile,
                    ResourcesCompat.getDrawable(getResources(), R.drawable.ic_add, null)),
    });


    /** Set the selection AND saves it as a shared preference */
    public void setProfileSelection(int position){
        setSelection(position);
        LauncherPreferences.DEFAULT_PREF.edit()
                .putString(LauncherPreferences.PREF_KEY_CURRENT_PROFILE,
                        mProfileAdapter.getItem(position).toString())
                .apply();
    }

    public void setSelection(int position){
        if(mListView != null) mListView.setSelection(position);
        mProfileAdapter.setView(this, mProfileAdapter.getItem(position), false);
        mSelectedIndex = position;
    }

    public void setParentFragment(Fragment mParentFragment) {
        this.mParentFragment = mParentFragment;
    }

    /** Reload profiles from the file, forcing the spinner to consider the new data */
    public void reloadProfiles(){
        mProfileAdapter.reloadProfiles();
    }

    /** Initialize various behaviors */
    private void init(){
        // Setup various attributes
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen._12ssp));
        setGravity(Gravity.CENTER_VERTICAL);
        int padding = getContext().getResources().getDimensionPixelOffset(R.dimen._8sdp);
        setPaddingRelative(padding, 0, padding, 0);
        setCompoundDrawablePadding(padding);

        int profileIndex;
        String extra_value = (String) ExtraCore.consumeValue(ExtraConstants.REFRESH_VERSION_SPINNER);
        if(extra_value != null){
            profileIndex = extra_value.equals(DELETED_PROFILE) ? 0
                    : getProfileAdapter().resolveProfileIndex(extra_value);
        }else
            profileIndex = mProfileAdapter.resolveProfileIndex(
                    LauncherPreferences.DEFAULT_PREF.getString(LauncherPreferences.PREF_KEY_CURRENT_PROFILE,""));

        setProfileSelection(Math.max(0,profileIndex));

        // Popup window behavior
        setOnClickListener(new OnClickListener() {
            final int offset = -getContext().getResources().getDimensionPixelOffset(R.dimen._4sdp);
            @Override
            public void onClick(View v) {
                ViewAnimUtils.setViewAnim(mcVersionSpinner.this, Techniques.Pulse);

                if(mPopupWindow == null) getPopupWindow();

                if(mPopupWindow.isShowing()){
                    mPopupWindow.dismiss();
                    return;
                }
                mPopupWindow.showAsDropDown(mcVersionSpinner.this, 0, offset);
                // Post() is required for the layout inflation phase
                post(() -> mListView.setSelection(mSelectedIndex));
            }
        });
    }

    private void performExtraAction(ProfileAdapterExtra extra) {
        //Replace with switch-case if you want to add more extra actions
        if (extra.id == VERSION_SPINNER_PROFILE_CREATE && mParentFragment != null) {
            ZHTools.swapFragmentWithAnim(mParentFragment, ProfileTypeSelectFragment.class, ProfileTypeSelectFragment.TAG, null);
        }
    }

    /** Create the listView and popup window for the interface, and set up the click behavior */
    @SuppressLint("ClickableViewAccessibility")
    private void getPopupWindow(){
        mListView = (ListView) inflate(getContext(), R.layout.spinner_mc_version, null);
        mListView.setAdapter(mProfileAdapter);
        mListView.setOnItemClickListener((parent, view, position, id) -> {
            Object item = mProfileAdapter.getItem(position);
            if(item instanceof String) {
                hidePopup(true);
                setProfileSelection(position);
            }else if(item instanceof ProfileAdapterExtra) {
                hidePopup(false);
                performExtraAction((ProfileAdapterExtra) item);
            }
        });

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            mPopupWindow = new PopupWindow(mListView, (displayMetrics.widthPixels / 5) * 2, (displayMetrics.heightPixels / 5) * 3);
        }
        else mPopupWindow = new PopupWindow(mListView, MATCH_PARENT, getContext().getResources().getDimensionPixelOffset(R.dimen._184sdp));
        mPopupWindow.setElevation(5);
        mPopupWindow.setClippingEnabled(false);

        // Block clicking outside of the popup window
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setTouchInterceptor((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_OUTSIDE){
                mPopupWindow.dismiss();
                return true;
            }
            return false;
        });


        // Custom animation, nice slide in
        mPopupAnimation = new Slide(Gravity.BOTTOM);
        mPopupWindow.setEnterTransition((Transition) mPopupAnimation);
        mPopupWindow.setExitTransition((Transition) mPopupAnimation);
    }

    private void hidePopup(boolean animate) {
        if(mPopupWindow == null) return;
        if(!animate) {
            mPopupWindow.setEnterTransition(null);
            mPopupWindow.setExitTransition(null);
            mPopupWindow.dismiss();
            mPopupWindow.setEnterTransition((Transition) mPopupAnimation);
            mPopupWindow.setExitTransition((Transition) mPopupAnimation);
        }else {
            mPopupWindow.dismiss();
        }
    }

    public ProfileAdapter getProfileAdapter() {
        return mProfileAdapter;
    }
}
