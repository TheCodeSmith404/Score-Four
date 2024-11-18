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
import data.defaults.DefaultCardOptions
import data.repository.CreateGameRepository
import data.repository.GameDetailsRepository
import data.repository.UserRepository
import data.repository.WaitingRoomRepository
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
    fun providesUserRepository(firestore: FirebaseFirestore,preferenceManager: PreferenceManager):UserRepository{
        return UserRepository(firestore,preferenceManager)
    }
    @Provides
    @Singleton
    fun providesGameSettingsRepository(firestore: FirebaseFirestore):CreateGameRepository{
        return CreateGameRepository(firestore)
    }
    @Provides
    @Singleton
    fun providePreferenceManager(@ApplicationContext context:Context): PreferenceManager {
        return PreferenceManager(context)
    }
    @Provides
    @Singleton
    fun providesDefaultCardOptions():DefaultCardOptions{
        return DefaultCardOptions
    }
    @Provides
    @Singleton
    fun providesGameDetailsRepository(firestore: FirebaseFirestore,preferenceManager: PreferenceManager):GameDetailsRepository{
        return GameDetailsRepository(firestore,preferenceManager)
    }

    @Provides
    @Singleton
    fun providesWaitingRoomRepository(firestore: FirebaseFirestore):WaitingRoomRepository{
        return WaitingRoomRepository(firestore)
    }
}