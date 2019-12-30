# No_More_Chinese_Xposed
Turn all Chinese words on your android screen into spaces.

### ScreenShot
![Screen Shot](Screenshot.jpg)

### How I made it
https://youtu.be/EnNyQYi-8gQ

https://youtu.be/JyAIHhqtUy4

### Tech Challenges
#### sharedPreferences
1. in your activity, do this every time after configuring commitment:
```kotlin
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
```
```kotlin
        global_switch.setOnCheckedChangeListener { _, isChecked ->
            with (sharedPref.edit()) {
                this.putBoolean("switch", isChecked)
                this.commit()
            }
            setWorldReadable()
        }
```

2. in your xposed java class:
```java
        XSharedPreferences pref = new XSharedPreferences(BuildConfig.APPLICATION_ID.toString(), "main");
```

That's it!
You don't have to use `pref.makeWorldReadable()` or `pref.reload`...