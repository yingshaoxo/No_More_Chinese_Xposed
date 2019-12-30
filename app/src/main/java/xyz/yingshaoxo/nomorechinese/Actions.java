package xyz.yingshaoxo.nomorechinese;

import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;


public class Actions implements IXposedHookLoadPackage {
    public XSharedPreferences pref;

    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        //XposedBridge.log("Loaded app: " + lpparam.packageName);

        XSharedPreferences pref = new XSharedPreferences(BuildConfig.APPLICATION_ID.toString(), "main");
        //pref.makeWorldReadable();
        //pref.reload();
        boolean switch_status = pref.getBoolean("switch", true);
        if (switch_status == false) {
            return;
        }

        if (lpparam.packageName.equals("com.android.systemui") || lpparam.packageName.equals("com.android.settings")) {
            return;
        }
        if (lpparam.packageName.equals("de.robv.android.xposed.installer")) {
            return;
        }

        XposedHelpers.findAndHookMethod(TextView.class, "setText", CharSequence.class, TextView.BufferType.class, boolean.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                CharSequence cs = (CharSequence) param.args[0];
                if (cs != null) {// && cs instanceof String) {
                    String origin = cs.toString();
                    if (!origin.isEmpty()) {
                        if (!isEnglish(origin)) {
                            //String result = String.format("%1$" + origin.length() + "s", "");

                            String result = origin.replaceAll("[\u4e00-\u9fa5]+", " ");
                            result = result.replaceAll("[^a-zA-Z0-9\n ]", " ");

                            param.args[0] = result;
                        }
                    }
                }
            }
        });
    }

    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    public static boolean isEnglish(String charaString) {
        charaString = charaString.replaceAll("[\n`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。， 、？]", "");
        return charaString.matches("^[a-zA-Z0-9]*");
    }

    public static boolean isChinese(String str) {
        str = str.replaceAll("[\n`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。， 、？]", "");

        String regEx = "[\\u4e00-\\u9fa5]+";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);

        if (m.find()) {
            return true;
        } else {
            return false;
        }

    }
}

