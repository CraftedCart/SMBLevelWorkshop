package craftedcart.smblevelworkshop.resource;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CraftedCart
 * Created on 04/04/2016 (DD/MM/YYYY)
 */
public class LangManager {

    //                         <Locale,    <Key   , Value >>
    @NotNull private static Map<String, Map<String, String>> localeMap = new HashMap<>();
    @NotNull private static String selectedLang = "en_US";

    /**
     * Add an item given a locale, key and value
     *
     * @param locale The language locale (eg: en_US)
     * @param key The identifier
     * @param value The translated string
     */
    public static void addItem(String locale, String key, String value) {
        if (localeMap.containsKey(locale)) {
            Map<String, String> langMap = localeMap.get(locale);
            langMap.put(key, value);
            localeMap.put(locale, langMap);
        } else {
            Map<String, String> langMap = new HashMap<>();
            langMap.put(key, value);
            localeMap.put(locale, langMap);
        }
    }

    /**
     * Get a translated string, given a locale and key
     *
     * @param locale The language locale (eg: en_US)
     * @param key The identifier
     * @return The translated string, or the key if the entry wasn't found
     */
    public static String getItem(String locale, String key) {
        if (localeMap.containsKey(locale)) {
            if (localeMap.get(locale).containsKey(key)) {
                return localeMap.get(locale).get(key);
            } else {
                return key;
            }
        } else {
            return key;
        }
    }

    public static String getItem(String key) {
        return getItem(selectedLang, key);
    }

    public static void setSelectedLang(@NotNull String selectedLang) {
        LangManager.selectedLang = selectedLang;
    }

    @NotNull
    public static String getSelectedLang() {
        return selectedLang;
    }

}
