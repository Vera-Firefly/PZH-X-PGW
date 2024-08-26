package net.kdt.pojavlaunch.prefs.screens;

import static net.kdt.pojavlaunch.prefs.LauncherPreferences.DEFAULT_PREF;
import static net.kdt.pojavlaunch.prefs.LauncherPreferences.PREF_NOTCH_SIZE;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.firefly.pgw.renderer.RendererManager;
import com.firefly.pgw.utils.ListAndArray;
import com.movtery.pojavzh.feature.log.Logging;
import com.movtery.pojavzh.ui.dialog.EditTextDialog;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.prefs.CustomSeekBarPreferencePro;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;

/**
 * Fragment for any settings video related
 */
public class LauncherPreferenceVideoFragment extends LauncherPreferenceFragment {
    public static final String TAG = "LauncherPreferenceVideoFragment";
    @Override
    public void onCreatePreferences(Bundle b, String str) {
        addPreferencesFromResource(R.xml.pref_video);

        int scaleFactor = LauncherPreferences.PREF_SCALE_FACTOR;

        //Disable notch checking behavior on android 8.1 and below.
        requirePreference("ignoreNotch").setVisible(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && PREF_NOTCH_SIZE > 0);

        CustomSeekBarPreferencePro seek5 = requirePreference("resolutionRatio",
                CustomSeekBarPreferencePro.class);
        if (scaleFactor > 100) {
            seek5.setRange(25, scaleFactor);
        } else {
            seek5.setRange(25, 100);
        }
        seek5.setValue(scaleFactor);
        seek5.setSuffix(" %");

        // #724 bug fix
        if (seek5.getValue() < 25) {
            seek5.setValue(100);
        }

        seek5.setOnPreferenceClickListener(preference -> {
            setVideoResolutionDialog(seek5);
            return true;
        });

        // Sustained performance is only available since Nougat
        SwitchPreference sustainedPerfSwitch = requirePreference("sustainedPerformance",
                SwitchPreference.class);
        sustainedPerfSwitch.setVisible(true);

        final ListPreference rendererListPreference = requirePreference("renderer", ListPreference.class);
        final ListPreference expRendererListPreference = requirePreference("renderer_exp", ListPreference.class);
        setListPreference(rendererListPreference, "renderer");
        setListPreference(expRendererListPreference, "renderer_exp");

        computeVisibility();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences p, String s) {
        super.onSharedPreferenceChanged(p, s);
        computeVisibility();
    }

    private void computeVisibility(){
        requirePreference("force_vsync", SwitchPreferenceCompat.class)
                .setVisible(LauncherPreferences.PREF_USE_ALTERNATE_SURFACE);
    }

    private void setListPreference(ListPreference listPreference, String key) {
        boolean prefExpSetup = key.equals("renderer_exp") == LauncherPreferences.PREF_EXP_SETUP;
        listPreference.setVisible(prefExpSetup);
        if (!prefExpSetup) return;

        ListAndArray array = RendererManager.getCompatibleRenderers(requireContext());
        Tools.LOCAL_RENDERER = listPreference.getValue();
        listPreference.setEntries(array.getArray());
        listPreference.setEntryValues(array.getList().toArray(new String[0]));

        listPreference.setOnPreferenceChangeListener((pre, obj) -> updateRendererPref((String) obj));
    }

    private boolean updateRendererPref(String name) {
        Tools.LOCAL_RENDERER = name;
        return true;
    }

    private void setVideoResolutionDialog(CustomSeekBarPreferencePro seek) {
        EditTextDialog.Builder builder = new EditTextDialog.Builder(requireContext())
            .setTitle(R.string.mcl_setting_title_resolution_scaler)
            .setEditText(String.valueOf(seek.getValue()))
            .setConfirmListener(editBox -> {
                String checkValue = editBox.getText().toString();
                if (checkValue.isEmpty()) {
                    editBox.setError(requireContext().getString(R.string.global_error_field_empty));
                    return false;
                }
                int Value;
                try {
                    Value = Integer.parseInt(checkValue);
                } catch (NumberFormatException e) {
                    Logging.e("VideoResolution", e.toString());
                    // editBox.setError(e.toString());
                    editBox.setError(requireContext().getString(R.string.zh_input_invalid));
                    return false;
                }
                if (Value < 25) {
                    editBox.setError(requireContext().getString(R.string.zh_input_too_small, 25));
                    return false;
                }
                if (Value > 1000) {
                    editBox.setError(requireContext().getString(R.string.zh_input_too_big, 1000));
                    return false;
                }
                if (Value > 100) {
                    seek.setRange(25, Value);
                } else {
                    seek.setRange(25, 100);
                }
                seek.setValue(Value);
                return true;
            });
        builder.buildDialog();
    }
}
