package xyz.yingshaoxo.nomorechinese

import android.Manifest
import android.app.AndroidAppHelper
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var switch_status = true
        global_switch.isChecked = switch_status

        Dexter.withActivity(this)
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {/* ... */
                    val file = File(this@MainActivity.getExternalFilesDir(null), "config.txt")
                    /*
                    Toast.makeText(
                        this@MainActivity,
                        file.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                     */
                    if (file.exists()) {
                        var data = file.readText().toString()
                        if (data.contains("true")) {
                            switch_status = true
                        } else {
                            switch_status = false
                        }
                    } else {
                        file.writeText(switch_status.toString().toLowerCase())
                    }

                    global_switch.setOnCheckedChangeListener { _, isChecked ->
                        file.writeText(isChecked.toString().toLowerCase())
                    }
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {/* ... */
                    Toast.makeText(
                        this@MainActivity,
                        "I won't work unless you give me all those permissions!",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {/* ... */
                }
            }).check()
    }
}
