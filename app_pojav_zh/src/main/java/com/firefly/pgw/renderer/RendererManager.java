package com.firefly.pgw.renderer;

import static net.kdt.pojavlaunch.prefs.LauncherPreferences.PREF_EXP_SETUP;

import android.content.Context;
import android.content.res.Resources;

import com.firefly.pgw.utils.MesaUtils;

import net.kdt.pojavlaunch.R;

import java.util.ArrayList;
import java.util.List;

public class RendererManager {
    public static String MESA_LIBS = null;
    public static String DRIVER_MODEL = null;
    public static String LOADER_OVERRIDE = null;

    /**
     * Return the renderers that are compatible with this device
     */
    public static RenderersList getCompatibleRenderers(Context context) {
        Resources resources = context.getResources();
        String[] defaultRenderers = resources.getStringArray(R.array.renderer_values);
        String[] defaultRendererNames = resources.getStringArray(R.array.renderer);
        List<String> rendererIds = new ArrayList<>();
        List<String> rendererNames = new ArrayList<>();
        for(int i = 0; i < defaultRenderers.length; i++) {
            String rendererList = defaultRenderers[i];
            if (skipRenderer(rendererList, "mesa_3d") && !PREF_EXP_SETUP) continue;
            if (skipRenderer(rendererList, "zink", "virgl", "freedreno", "panfrost") && PREF_EXP_SETUP) continue;
            rendererIds.add(rendererList);
            rendererNames.add(defaultRendererNames[i]);
        }

        return new RenderersList(rendererIds, rendererNames.toArray(new String[0]));
    }

    public static CMesaLibList getCompatibleCMesaLib(Context context) {
        Resources resources = context.getResources();
        String[] defaultCMesaLib = resources.getStringArray(R.array.osmesa_values);
        String[] defaultCMesaLibNames = resources.getStringArray(R.array.osmesa_library);
        List<String> CMesaLibIds = new ArrayList<>();
        List<String> CMesaLibNames = new ArrayList<>();
        for (int i = 0; i < defaultCMesaLib.length; i++) {
            CMesaLibIds.add(defaultCMesaLib[i]);
            CMesaLibNames.add(defaultCMesaLibNames[i]);
        }
        List<String> downloadList = MesaUtils.INSTANCE.getMesaLibList();
        for (String item : downloadList) {
            CMesaLibIds.add(item);
            CMesaLibNames.add(item);
        }

        return new CMesaLibList(CMesaLibIds, CMesaLibNames.toArray(new String[0]));
    }

    public static CDriverModelList getCompatibleCDriverModel(Context context) {
        Resources resources = context.getResources();
        String[] defaultCDriverModel = resources.getStringArray(R.array.driver_models_values);
        String[] defaultCDriverModelNames = resources.getStringArray(R.array.driver_models);
        List<String> CDriverModelIds = new ArrayList<>();
        List<String> CDriverModelNames = new ArrayList<>();
        for(int i = 0; i < defaultCDriverModel.length; i++) {
            String driverModel = defaultCDriverModel[i];
            switch (MESA_LIBS) {
                case "default":
                case "mesa2320d":
                case "mesa2304":
                    if (skipRenderer(driverModel, "virgl", "softpipe", "llvmpipe")) continue;
                    break;
                case "mesa2300d":
                    if (skipRenderer(driverModel, "virgl", "freedreno", "softpipe", "llvmpipe")) continue;
                    break;
                case "mesa2205":
                    if (skipRenderer(driverModel, "panfrost", "freedreno", "softpipe", "llvmpipe")) continue;
                    break;
            }
            CDriverModelIds.add(driverModel);
            CDriverModelNames.add(defaultCDriverModelNames[i]);
        }

        return new CDriverModelList(CDriverModelIds, CDriverModelNames.toArray(new String[0]));
    }

    public static CMesaLDOList getCompatibleCMesaLDO(Context context) {
        Resources resources = context.getResources();
        String[] defaultCMesaLDO = resources.getStringArray(R.array.osmesa_mldo_values);
        String[] defaultCMesaLDONames = resources.getStringArray(R.array.osmesa_mldo);
        List<String> CMesaLDOIds = new ArrayList<>(defaultCMesaLDO.length);
        List<String> CMesaLDONames = new ArrayList<>(defaultCMesaLDONames.length);
        for (int i = 0; i < defaultCMesaLDO.length; i++) {
            CMesaLDOIds.add(defaultCMesaLDO[i]);
            CMesaLDONames.add(defaultCMesaLDONames[i]);
        }

        return new CMesaLDOList(CMesaLDOIds, CMesaLDONames.toArray(new String[0]));
    }

    /**
     * Checks if the renderer Id is compatible with the current device
     */
    public static boolean checkRendererCompatible(Context context, String rendererName) {
        return getCompatibleRenderers(context).rendererIds.contains(rendererName);
    }

    private static boolean skipRenderer(String string, String... renderers) {
        for (String renderer : renderers) {
            if (string.contains(renderer)) return true;
        }
        return false;
    }
}