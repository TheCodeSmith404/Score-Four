package dependency

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import data.PreferenceManager
import data.repository.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFireStoreInstance(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
    @Provides
    @Singleton
    fun providesUserRepository(firestore: FirebaseFirestore):UserRepository{
        return UserRepository(firestore)
    }
    @Provides
    @Singleton
    fun providePreferenceManager(@ApplicationContext context:Context): PreferenceManager {
        return PreferenceManager(context)
    }
}