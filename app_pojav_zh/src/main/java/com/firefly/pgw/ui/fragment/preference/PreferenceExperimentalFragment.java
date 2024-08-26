package com.firefly.pgw.ui.fragment.preference;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.SwitchPreference;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Arrays;
import java.util.Set;

import com.firefly.pgw.renderer.RendererManager;
import com.firefly.pgw.ui.dialog.EditMesaVersionDialog;
import com.movtery.pojavzh.ui.dialog.TipDialog;
import com.firefly.pgw.utils.ListAndArray;
import com.firefly.pgw.utils.MesaUtils;

import net.kdt.pojavlaunch.PojavApplication;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.prefs.screens.LauncherPreferenceFragment;

// Experimental Settings for Mesa renderer
public class PreferenceExperimentalFragment extends LauncherPreferenceFragment {
    public static final String TAG = "PreferenceExperimentalFragment";

    @Override
    public void onCreatePreferences(Bundle b, String str) {
        addPreferencesFromResource(R.xml.pref_experimental);
        computeVisibility();

        final Preference downloadMesa = requirePreference("DownloadMesa", Preference.class);
        downloadMesa.setOnPreferenceClickListener((a)-> {
            loadMesaList();
            return true;
        });

        final ListPreference CMesaLib = requirePreference("CMesaLibrary", ListPreference.class);
        final ListPreference CDriverModel = requirePreference("CDriverModels", ListPreference.class);
        final ListPreference CMesaLDOP = requirePreference("ChooseMldo", ListPreference.class);

        setListPreference(CMesaLib, "CMesaLibrary");
        setListPreference(CDriverModel, "CDriverModels");
        setListPreference(CMesaLDOP, "ChooseMldo");

        CMesaLib.setOnPreferenceChangeListener((pre, obj) -> {
            RendererManager.MESA_LIBS = (String) obj;
            setListPreference(CDriverModel, "CDriverModels");
            CDriverModel.setValueIndex(0);
            return true;
        });

        CDriverModel.setOnPreferenceChangeListener((pre, obj) -> {
            RendererManager.DRIVER_MODEL = (String) obj;
            return true;
        });

        CMesaLDOP.setOnPreferenceChangeListener((pre, obj) -> {
            RendererManager.LOADER_OVERRIDE = (String)obj;
            return true;
        });

        SwitchPreference expRendererPref = requirePreference("ExperimentalSetup", SwitchPreference.class);
        expRendererPref.setOnPreferenceChangeListener((p, v) -> {
            // onChangeRenderer(); Don't need it.
            boolean isExpRenderer = (boolean) v;
            if (isExpRenderer) {
                onExpRendererDialog(p);
            }
            return true;
        });

        // Custom GL/GLSL
        final PreferenceCategory customMesaVersionPref = requirePreference("customMesaVersionPref", PreferenceCategory.class);
        SwitchPreference setSystemVersion = requirePreference("ebSystem", SwitchPreference.class);
        setSystemVersion.setOnPreferenceChangeListener((p, v) -> {
            boolean set = (boolean) v;
            if (!set) return false;
            closeOtherCustomMesaPref(customMesaVersionPref);
            return true;
        });

        SwitchPreference setSpecificVersion = requirePreference("ebSpecific", SwitchPreference.class);
        setSpecificVersion.setOnPreferenceChangeListener((p, v) -> {
            boolean set = (boolean) v;
            if (!set) return false;
            closeOtherCustomMesaPref(customMesaVersionPref);
            return true;
        });

        SwitchPreference setGLVersion = requirePreference("ebCustom", SwitchPreference.class);
        setGLVersion.setOnPreferenceChangeListener((p, v) -> {
            boolean set = (boolean) v;
            if (!set) return false;
            closeOtherCustomMesaPref(customMesaVersionPref);
            return true;
        });
        setGLVersion.setOnPreferenceClickListener(preference -> {
            new EditMesaVersionDialog(requireContext()).show();
            return true;
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences p, String s) {
        super.onSharedPreferenceChanged(p, s);
        computeVisibility();
    }

    private void computeVisibility() {
        requirePreference("SpareFrameBuffer").setVisible(LauncherPreferences.PREF_EXP_SETUP);
        requirePreference("MesaRendererChoose").setVisible(LauncherPreferences.PREF_EXP_SETUP);
        requirePreference("customMesaVersionPref").setVisible(LauncherPreferences.PREF_EXP_SETUP);
        requirePreference("customMesaLoaderDriverOverride").setVisible(LauncherPreferences.PREF_EXP_SETUP);
        requirePreference("ChooseMldo").setVisible(LauncherPreferences.PREF_LOADER_OVERRIDE);
    }

    private void closeOtherCustomMesaPref(PreferenceCategory customMesaVersionPref) {
        for (int i1 = 0; i1 < customMesaVersionPref.getPreferenceCount(); i1++) {
            Preference preference2 = customMesaVersionPref.getPreference(i1);
            if (preference2 instanceof SwitchPreference) {
                ((SwitchPreference) preference2).setChecked(false);
            }
        }
    }

    private void setListPreference(ListPreference listPreference, String preferenceKey) {
        ListAndArray array = null;
        String value = listPreference.getValue();
        if (preferenceKey.equals("CMesaLibrary")) {
            array = RendererManager.getCompatibleCMesaLib(requireContext());
            boolean have = false;
            for (int a = 0; a < array.getList().size(); a++) {
                if (array.getList().get(a).equalsIgnoreCase(value)) {
                    have = true;
                    break;
                }
            }
            if (!have) {
                value = array.getList().get(0);
                listPreference.setValue(value);
            }
            RendererManager.MESA_LIBS = value;
        } else if (preferenceKey.equals("CDriverModels")) {
            array = RendererManager.getCompatibleCDriverModel(requireContext());
            RendererManager.DRIVER_MODEL = value;
        } else if (preferenceKey.equals("ChooseMldo")) {
            array = RendererManager.getCompatibleCMesaLDO(requireContext());
            RendererManager.LOADER_OVERRIDE = value;
        }
        listPreference.setEntries(array.getArray());
        listPreference.setEntryValues(array.getList().toArray(new String[0]));
    }

    private void onExpRendererDialog(Preference pre) {
        new TipDialog.Builder(requireContext())
            .setTitle(R.string.zh_warning)
            .setMessage(R.string.preference_rendererexp_alertdialog_message)
            .setCancelClickListener(() -> {
                ((SwitchPreference) pre).setChecked(false);
            }).setCancelable(false).buildDialog();
    }

    private void loadMesaList() {
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setMessage(R.string.preference_rendererexp_mesa_download_load)
                .show();
        PojavApplication.sExecutorService.execute(() -> {
            Set<String> list = MesaUtils.INSTANCE.getMesaList();
            requireActivity().runOnUiThread(() -> {
                dialog.dismiss();

                if (list == null) {
                    AlertDialog alertDialog1 = new AlertDialog.Builder(requireActivity())
                            .setMessage(R.string.preference_rendererexp_mesa_get_fail)
                            .create();
                    alertDialog1.show();
                } else {
                    final String[] items3 = new String[list.size()];
                    list.toArray(items3);
                    // Add List
                    AlertDialog alertDialog3 = new AlertDialog.Builder(requireActivity())
                            .setTitle(R.string.preference_rendererexp_mesa_select_download)
                            .setItems(items3, (dialogInterface, i) -> {
                                if (i < 0 || i > items3.length)
                                    return;
                                dialogInterface.dismiss();
                                downloadMesa(items3[i]);
                            })
                            .create();
                    alertDialog3.show();
                }
            });
        });
    }

    private void downloadMesa(String version) {
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setMessage(R.string.preference_rendererexp_mesa_downloading)
                .show();
        PojavApplication.sExecutorService.execute(() -> {
            boolean data = MesaUtils.INSTANCE.downloadMesa(version);
            requireActivity().runOnUiThread(() -> {
                dialog.dismiss();
                if (data) {
                    Toast.makeText(requireContext(), R.string.preference_rendererexp_mesa_downloaded, Toast.LENGTH_SHORT)
                            .show();
                } else {
                    AlertDialog alertDialog1 = new AlertDialog.Builder(requireActivity())
                            .setMessage(R.string.preference_rendererexp_mesa_download_fail)
                            .create();
                    alertDialog1.show();
                }
            });
        });
    }

}
