package data

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    var isSignedIn:Boolean
        get()=sharedPreferences.getBoolean("is_signed_in",false)
        set(value){
            sharedPreferences.edit().putBoolean("is_signed_in",value).apply()
        }

    var userName: String
        get() = sharedPreferences.getString("user_name", "404 Not Found") ?: "404 Not Found"
        set(value) {
            sharedPreferences.edit().putString("user_name", value).apply() // Apply changes here
        }

    var imageUrl: String?
        get() = sharedPreferences.getString("image_url", null) // Default to null if not set
        set(value) {
            sharedPreferences.edit().putString("image_url", value).apply()
        }
}
