package xyz.yingshaoxo.nomorechinese

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var sharedPref = this?.getSharedPreferences("main", Context.MODE_PRIVATE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //setWorldReadable()
        } else {
            sharedPref = this?.getSharedPreferences("main", Context.MODE_WORLD_READABLE)
        }
        val switch_status = sharedPref.getBoolean("switch", true)

        global_switch.isChecked = switch_status
        global_switch.setOnCheckedChangeListener { _, isChecked ->
            with (sharedPref.edit()) {
                this.putBoolean("switch", isChecked)
                this.commit()
            }
            setWorldReadable()
        }
    }

    fun setWorldReadable() {
        val dataDir = File(this.getApplicationInfo().dataDir)
        val prefsDir = File(dataDir, "shared_prefs")
        val prefsFile = File(prefsDir, "main" + ".xml")
        if (prefsFile.exists()) {
            //Toast.makeText(this, prefsFile.path.toString(), Toast.LENGTH_LONG).show()
            for (file in arrayOf<File>(dataDir, prefsDir, prefsFile)) {
                file.setReadable(true, false)
                file.setExecutable(true, false)
            }
        }
    }
}
