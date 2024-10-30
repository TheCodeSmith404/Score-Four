package data

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context:Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    var isLoggedIn:Boolean
        get()=sharedPreferences.getBoolean("is_logged_in",false)
        set(value){
            sharedPreferences.edit().putBoolean("is_logged_in",value)
        }

}