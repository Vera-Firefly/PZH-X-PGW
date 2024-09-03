package com.firefly.pgw.ui.fragment;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.ListPreference;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import android.widget.ListView;

import com.firefly.pgw.renderer.RendererManager;
import com.firefly.pgw.utils.ListAndArray;
import com.firefly.pgw.utils.MesaUtils;
import com.movtery.pojavzh.ui.dialog.TipDialog;

import net.kdt.pojavlaunch.R;

import java.util.Arrays;
import java.util.List;

public class DeletableListPreference extends ListPreference {

    private List<String> defaultLibs;
    private OnPreferenceChangeListener preferenceChangeListener;

    public DeletableListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadDefaultLibs(context);
    }

    private void loadDefaultLibs(Context context) {
        defaultLibs = Arrays.asList(context.getResources().getStringArray(R.array.osmesa_values));
    }

    @Override
    protected void onClick() {
        String initialValue = getValue();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getDialogTitle());

        CharSequence[] entriesCharSequence = getEntries();
        String[] entries = new String[entriesCharSequence.length];
        for (int i = 0; i < entriesCharSequence.length; i++) {
            entries[i] = entriesCharSequence[i].toString();
        }

        builder.setItems(entries, (dialog, which) -> {
            String newValue = getEntryValues()[which].toString();
            if (!newValue.equals(initialValue)) {
                if (preferenceChangeListener != null) {
                    if (preferenceChangeListener.onPreferenceChange(this, newValue)) {
                        setValue(newValue);
                    }
                } else {
                    setValue(newValue);
                }
            }
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        ListView listView = dialog.getListView();
        listView.setOnItemLongClickListener((adapterView, view, position, id) -> {
            String selectedVersion = getEntryValues()[position].toString();
            if (defaultLibs.contains(selectedVersion)) {
                Toast.makeText(getContext(), R.string.preference_rendererexp_mesa_delete_defaultlib, Toast.LENGTH_SHORT).show();
            } else {
                showDeleteConfirmationDialog(selectedVersion);
            }
            dialog.dismiss();
            return true;
        });
    }

    @Override
    public void setOnPreferenceChangeListener(OnPreferenceChangeListener listener) {
        this.preferenceChangeListener = listener;
        super.setOnPreferenceChangeListener(listener);
    }

    private void showDeleteConfirmationDialog(String version) {
        new TipDialog.Builder(getContext())
            .setTitle(R.string.preference_rendererexp_mesa_delete_title)
            .setMessage(getContext().getString(R.string.preference_rendererexp_mesa_delete_message, version))
            .setConfirmClickListener(() -> {
                boolean success = MesaUtils.INSTANCE.deleteMesaLib(version);
                if (success) {
                    Toast.makeText(getContext(), R.string.preference_rendererexp_mesa_deleted, Toast.LENGTH_SHORT).show();
                    setEntriesAndValues();
                } else {
                    Toast.makeText(getContext(), R.string.preference_rendererexp_mesa_delete_fail, Toast.LENGTH_SHORT).show();
                }
            }).setCancelable(false).buildDialog();
    }

    private void setEntriesAndValues() {
        Tools.IListAndArry array = Tools.getCompatibleCMesaLib(getContext());
        setEntries(array.getArray());
        setEntryValues(array.getList().toArray(new String[0]));
        String currentValue = getValue();
        if (!array.getList().contains(currentValue)) {
            setValueIndex(0);
        }
    }
}