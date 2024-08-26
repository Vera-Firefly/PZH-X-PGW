package net.kdt.pojavlaunch.modloaders;

import android.content.Intent;

import com.movtery.pojavzh.utils.PathAndUrlManager;

import net.kdt.pojavlaunch.utils.DownloadUtils;

import java.io.File;
import java.util.List;

public class OptiFineUtils {

    public static OptiFineVersions downloadOptiFineVersions(boolean force) throws Exception {
        return DownloadUtils.downloadStringCached("https://optifine.net/downloads", "of_downloads_page", force, new OptiFineScraper());
    }

    public static void addAutoInstallArgs(Intent intent, File modInstallerJar) {
        intent.putExtra("javaArgs", "-javaagent:"+ PathAndUrlManager.DIR_DATA+"/forge_installer/forge_installer.jar"
                + "=OFNPS" +// No Profile Suppression
                " -jar "+modInstallerJar.getAbsolutePath());
    }

    public static class OptiFineVersions {
        public List<String> minecraftVersions;
        public List<List<OptiFineVersion>> optifineVersions;
    }
    public static class OptiFineVersion {
        public String minecraftVersion;
        public String versionName;
        public String downloadUrl;
    }
}
