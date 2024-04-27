package zad1;

import java.util.HashMap;
import java.util.Map;

public class Dictionaries {

    protected static Map<String, Map<String, String>> dictionaries;

    public static void init() {
        Map<String, String> eng = new HashMap<>();
        eng.put("cześć", "hello");
        eng.put("jeden", "one");
        eng.put("ty", "you");

        Map<String, String> spanish = new HashMap<>();
        spanish.put("cześć", "hola");
        spanish.put("jeden", "uno");
        spanish.put("ty", "tu");

        Map<String, String> french = new HashMap<>();
        french.put("cześć", "salute");
        french.put("jeden", "un");
        french.put("ty", "toi");

        dictionaries = new HashMap<>();

        dictionaries.put("EN", eng);
        dictionaries.put("SP", spanish);
        dictionaries.put("FR", french);
    }


}
