package xyz.yingshaoxo.nomorechinese

import android.app.Activity
import android.app.AndroidAppHelper
import android.content.Context
import android.content.pm.PackageManager
import android.widget.TextView

import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider

import java.util.regex.Matcher
import java.util.regex.Pattern

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import java.io.File


class Actions : IXposedHookLoadPackage {
    var pref: XSharedPreferences? = null

    @Throws(Throwable::class)
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        //XposedBridge.log("Loaded app: " + lpparam.packageName);

        try {
            var switch_status = false
            val file_path = "/storage/emulated/0/Android/data/xyz.yingshaoxo.nomorechinese/files/config.txt"
            if (File(file_path).exists()) {
                var data = File(file_path).readText().toString()
                XposedBridge.log(data.toString());
                if (data.contains("true")) {
                    switch_status = true
                }
            }
            if (switch_status == false) {
                return
            }
        } catch (e: ArithmeticException) {
            return
        }

        if (lpparam.packageName == "com.android.systemui" || lpparam.packageName == "com.android.settings") {
            return
        }
        if (lpparam.packageName == "de.robv.android.xposed.installer" || lpparam.packageName == "org.meowcat.edxposed.manager" || lpparam.packageName == "com.topjohnwu.magisk") {
            return
        }

        XposedHelpers.findAndHookMethod(
            TextView::class.java,
            "setText",
            CharSequence::class.java,
            TextView.BufferType::class.java,
            Boolean::class.javaPrimitiveType,
            Int::class.javaPrimitiveType,
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                    super.beforeHookedMethod(param)
                    val cs = param!!.args[0] as CharSequence
                    if (cs != null) {// && cs instanceof String) {
                        val origin = cs.toString()
                        if (!origin.isEmpty()) {
                            if (!isEnglish(origin)) {
                                //String result = String.format("%1$" + origin.length() + "s", "");

                                var result = origin.replace("[\u4e00-\u9fa5]+".toRegex(), " ")
                                result = result.replace("[^a-zA-Z0-9\n `~!@#\$%^&*+=|':;',./?\"]".toRegex(), " ")

                                param.args[0] = result
                            }
                        }
                    }
                }
            })
    }

    companion object {

        fun isChinese(c: Char): Boolean {
            val ub = Character.UnicodeBlock.of(c)
            return if (ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub === Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub === Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub === Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub === Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
            ) {
                true
            } else false
        }

        fun isEnglish(charaString: String): Boolean {
            var charaString = charaString
            charaString = charaString.replace(
                "[\n`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。， 、？]".toRegex(),
                ""
            )
            return charaString.matches("^[a-zA-Z0-9]*".toRegex())
        }

        fun isChinese(str: String): Boolean {
            var str = str
            str = str.replace(
                "[\n`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。， 、？]".toRegex(),
                ""
            )

            val regEx = "[\\u4e00-\\u9fa5]+"
            val p = Pattern.compile(regEx)
            val m = p.matcher(str)

            return if (m.find()) {
                true
            } else {
                false
            }

        }
    }
}

