package dependency

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import data.PreferenceManager
import data.defaults.DefaultCardOptions
import data.repository.CreateGameRepository
import data.repository.DownloadGameResourcesRepository
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
    fun providesUserRepository(firestore: FirebaseFirestore,preferenceManager: PreferenceManager,firebaseStorage: FirebaseStorage):UserRepository{
        return UserRepository(firestore,preferenceManager,firebaseStorage)
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
    @Provides
    @Singleton
    fun providesFirebaseStorageInstance():FirebaseStorage{
        return FirebaseStorage.getInstance()
    }

    @Provides
    @Singleton
    fun providesDownloadResourcesRepository(firebaseStorage: FirebaseStorage):DownloadGameResourcesRepository{
        return DownloadGameResourcesRepository(firebaseStorage)
    }
}