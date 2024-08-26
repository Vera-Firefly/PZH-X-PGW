package net.kdt.pojavlaunch.utils;

import static net.kdt.pojavlaunch.prefs.LauncherPreferences.DEFAULT_PREF;

import android.content.*;
import android.content.res.*;
import android.os.LocaleList;

import androidx.preference.*;
import java.util.*;
import net.kdt.pojavlaunch.prefs.*;

public class LocaleUtils extends ContextWrapper {

    public LocaleUtils(Context base) {
        super(base);
    }

    public static ContextWrapper setLocale(Context context) {
        if (DEFAULT_PREF == null) {
            DEFAULT_PREF = PreferenceManager.getDefaultSharedPreferences(context);
            LauncherPreferences.loadPreferences(context);
        }

        if(DEFAULT_PREF.getBoolean("force_english", false)){
            Resources resources = context.getResources();
            Configuration configuration = resources.getConfiguration();

            configuration.setLocale(Locale.ENGLISH);
            Locale.setDefault(Locale.ENGLISH);
            LocaleList localeList = new LocaleList(Locale.ENGLISH);
            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);

            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
            context = context.createConfigurationContext(configuration);
        }

        return new LocaleUtils(context);
    }
}
