/**
 * Copyright 2013 Artur Augustyniak
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package pl.aaugustyniak.backproptrainer.config;

/**
 * App Preferences Storage
 *
 * @author Artur Augustyniak <artur@aaugustyniak.pl>
 */
import java.util.HashMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public final class AppPreferences {

    private static Preferences preferences = null;

    static {
        preferences = Preferences.userNodeForPackage(AppPreferences.class);
    }

    public static Preferences getRawStorage() {
        return preferences;
    }

    public static HashMap<String, String> getCurrentPrefs() throws BackingStoreException {
        HashMap<String, String> storedPrefs = new HashMap<String, String>();
        storedPrefs.put("hb_dialect", getHbDialect());
        storedPrefs.put("hb_dsn", getHbDsn());
        storedPrefs.put("hb_user", getHbUser());
        storedPrefs.put("hb_pass", getHbPass());
        storedPrefs.put("hb_driver_class", getHbDriverClass());
        storedPrefs.put("hb_cache", getHbCacheProvider());
        storedPrefs.put("hb_hbm_dll", getHbmDll());
        storedPrefs.put("tcase_folder", getTestCasesFolderPath());
        storedPrefs.put("mlp_folder", getPerceptronsFolderPath());
        storedPrefs.put("threads_num", getAppThreadsNum());

        return storedPrefs;
    }

    public static String getAppThreadsNum() {
        return preferences.get("threads_num", "10");
    }

    public static void setAppThreadsNum(String s) {
        preferences.put("threads_num", s);
    }

    public static String getHbDialect() {
        return preferences.get("hb_dialect", "org.hibernate.dialect.MySQLDialect");
    }

    public static void setHbDialect(String s) {
        preferences.put("hb_dialect", s);
    }

    public static String getHbDsn() {
        return preferences.get("hb_dsn", "jdbc:mysql://localhost:3306/bpackprop_trainer?zeroDateTimeBehavior=convertToNull");
    }

    public static void setHbDsn(String s) {
        preferences.put("hb_dsn", s);
    }

    public static String getHbUser() {
        return preferences.get("hb_user", "root");
    }

    public static void setHbUser(String s) {
        preferences.put("hb_user", s);
    }

    public static String getHbPass() {
        return preferences.get("hb_pass", null);
    }

    public static void setHbPass(String s) {
        preferences.put("hb_pass", s);
    }

    public static String getHbDriverClass() {
        return preferences.get("hb_driver_class", "com.mysql.jdbc.Driver");
    }

    public static void setHbDriverClass(String s) {
        preferences.put("hb_driver_class", s);
    }

    public static String getHbCacheProvider() {
        return preferences.get("hb_cache", "org.hibernate.cache.NoCacheProvider");
    }

    public static void setHbCacheProvider(String s) {
        preferences.put("hb_cache", s);
    }

    public static String getHbmDll() {
        return preferences.get("hb_hbm_dll", "update");
    }

    public static void setHbmDll(String s) {
        preferences.put("hb_hbm_dll", s);
    }

    public static String getTestCasesFolderPath() {
        return preferences.get("tcase_folder", "test_cases");
    }

    public static void setTestCasesFolderPath(String tcaseFolder) {
        preferences.put("tcase_folder", tcaseFolder);
    }

    public static String getPerceptronsFolderPath() {
        return preferences.get("mlp_folder", "perceptrons");
    }

    public static void setPerceptronsFolderPath(String perceptronsFolder) {
        preferences.put("mlp_folder", perceptronsFolder);
    }
}
