package data

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.core.net.toUri

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

    var profileUrl: Uri?
        get() = sharedPreferences.getString("image_url", null)?.toUri()
        set(value) {
            sharedPreferences.edit().putString("image_url", value.toString()).apply()
        }
    var currentGameId:String
        get()=sharedPreferences.getString("current_game_id","404")?:"404"
        set(value){
            sharedPreferences.edit().putString("current_game_id",value).apply()
        }
    var profileImageChanged:Boolean
        get()=sharedPreferences.getBoolean("profile_image_changed",false)
        set(value){
            sharedPreferences.edit().putBoolean("profile_image_changed",false).apply()
        }
}
