package net.kdt.pojavlaunch.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.movtery.pojavzh.extra.ZHExtraConstants;
import com.movtery.pojavzh.ui.fragment.FragmentWithAnim;
import com.movtery.pojavzh.utils.PathAndUrlManager;
import com.movtery.pojavzh.utils.ZHTools;
import com.movtery.pojavzh.utils.anim.ViewAnimUtils;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.extra.ExtraCore;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalLoginFragment extends FragmentWithAnim {
    public static final String TAG = "LOCAL_LOGIN_FRAGMENT";
    private View mMainMenu;
    private EditText mUsernameEditText;

    public LocalLoginFragment(){
        super(R.layout.fragment_local_login);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mMainMenu = view;
        mUsernameEditText = view.findViewById(R.id.login_edit_name);
        Button mLoginButton = view.findViewById(R.id.login_button);
        ImageView mReturnButton = view.findViewById(R.id.zh_login_return);

        mLoginButton.setOnClickListener(v -> {
            if(!checkEditText()) return;

            ExtraCore.setValue(ZHExtraConstants.LOCAL_LOGIN_TODO, new String[]{
                    mUsernameEditText.getText().toString(), "" });

            Tools.backToMainMenu(requireActivity());
        });
        mReturnButton.setOnClickListener(v -> ZHTools.onBackPressed(requireActivity()));

        ViewAnimUtils.slideInAnim(this);
    }


    /** @return Whether the mail (and password) text are eligible to make an auth request  */
    private boolean checkEditText(){

        String text = mUsernameEditText.getText().toString();

        Pattern pattern = Pattern.compile("[^a-zA-Z0-9_]");
        Matcher matcher = pattern.matcher(text);

        if (text.isEmpty()) {
            mUsernameEditText.setError(getString(R.string.zh_account_local_account_empty));
            return false;
        } else if (text.length() < 3) {
            mUsernameEditText.setError(getString(R.string.zh_account_local_account_less));
            return false;
        } else if (text.length() > 16) {
            mUsernameEditText.setError(getString(R.string.zh_account_local_account_greater));
            return false;
        } else if (matcher.find()) {
            mUsernameEditText.setError(getString(R.string.zh_account_local_account_illegal));
            return false;
        }

        boolean exists = new File(PathAndUrlManager.DIR_ACCOUNT_NEW + "/" + text + ".json").exists();
        if (exists) {
            mUsernameEditText.setError(getString(R.string.zh_account_local_account_exists));
        }
        return !(exists);
    }

    @Override
    public YoYo.YoYoString[] slideIn() {
        YoYo.YoYoString yoYoString = ViewAnimUtils.setViewAnim(mMainMenu, Techniques.BounceInDown);
        YoYo.YoYoString[] array = {yoYoString};
        super.setYoYos(array);
        return array;
    }

    @Override
    public YoYo.YoYoString[] slideOut() {
        YoYo.YoYoString yoYoString = ViewAnimUtils.setViewAnim(mMainMenu, Techniques.FadeOutUp);
        YoYo.YoYoString[] array = {yoYoString};
        super.setYoYos(array);
        return array;
    }
}
