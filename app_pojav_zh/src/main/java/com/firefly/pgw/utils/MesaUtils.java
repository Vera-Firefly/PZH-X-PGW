package com.firefly.pgw.utils;

import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.movtery.pojavzh.utils.PathAndUrlManager;
import net.kdt.pojavlaunch.utils.DownloadUtils;
import net.kdt.pojavlaunch.utils.FileUtils;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class MesaUtils {
    public static final MesaUtils INSTANCE = new MesaUtils();
    private static final String baseUrl = "https://www.coloryr.com/mesa/";
    private final File mesaDir;

    private MesaUtils() {
        mesaDir = new File(PathAndUrlManager.MESA_DIR);
        if (!mesaDir.exists() && !mesaDir.mkdirs()) {
            throw new RuntimeException("Failed to create mesa directory");
        }
    }

    private Map<String, MesaObj> mesaObjMap;

    public List<String> getMesaLibList() {
        List<String> list = new ArrayList<>();
        File[] files = mesaDir.listFiles();
        for (File file : files) {
            if (file.isDirectory() && new File(file.getAbsolutePath() + "/libOSMesa_8.so").exists()) {
                list.add(file.getName());
            }
        }

        return list;
    }

    public String getMesaLib(String version) {
        return PathAndUrlManager.MESA_DIR + "/" + version + "/libOSMesa_8.so";
    }

    public Set<String> getMesaList() {
        try {
            String data = DownloadUtils.downloadString(baseUrl + "version.json");
            mesaObjMap = new Gson().fromJson(data, TypeToken.getParameterized(Map.class, String.class, MesaObj.class).getType());

            return mesaObjMap.keySet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean downloadMesa(String version) {
        if (mesaObjMap == null) {
            return false;
        }
        MesaObj mesa = mesaObjMap.get(version);
        if (mesa == null) {
            return false;
        }

        try {
            File lib = new File(mesaDir, version);
            lib.mkdirs();
            File zip = new File(lib, "libOSMesa_" + getArch() + ".zip");
            try (FileOutputStream writer = new FileOutputStream(zip)) {
                DownloadUtils.download(baseUrl + version + "/libOSMesa_" + getArch() + ".zip", writer);
            }
            //TODO SHA1 Checked
            try (ZipFile zipFile = new ZipFile(zip)) {
                Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
                while (zipEntries.hasMoreElements()) {
                    ZipEntry zipEntry = zipEntries.nextElement();
                    String entryName = zipEntry.getName();
                    File zipDestination = new File(lib, entryName);
                    if (zipDestination.exists()) {
                        zipDestination.delete();
                    }
                    FileUtils.ensureParentDirectory(zipDestination);
                    try (InputStream inputStream = zipFile.getInputStream(zipEntry);
                         OutputStream outputStream = new FileOutputStream(zipDestination)) {
                        IOUtils.copy(inputStream, outputStream);
                    }
                }
            }
            zip.delete();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private static String getArch() {
        String abi = Build.SUPPORTED_ABIS[0];
        if (abi.equals("arm64-v8a")) {
            return "aarch64";
        } else if (abi.equals("armeabi-v7a")) {
            return "arm32";
        } else {
            return "x86_64";
        }
        // No libraries supported for x86
    }

    public static class MesaObj {
        public String aarch64;
        public String arm32;
        public String x86_64;
    }
}
