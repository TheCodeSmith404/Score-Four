package com.tcs.games.score4.dependency

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.tcs.games.score4.data.PreferenceManager
import com.tcs.games.score4.data.defaults.DefaultCardOptions
import com.tcs.games.score4.data.repository.CreateGameRepository
import com.tcs.games.score4.data.repository.DownloadGameResourcesRepository
import com.tcs.games.score4.data.repository.GameDetailsRepository
import com.tcs.games.score4.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
    fun providesGameSettingsRepository(firestore: FirebaseFirestore,userRepository: UserRepository):CreateGameRepository{
        return CreateGameRepository(firestore,userRepository)
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

//    @Provides
//    @Singleton
//    fun providesWaitingRoomRepository(firestore: FirebaseFirestore):WaitingRoomRepository{
//        return WaitingRoomRepository(firestore)
//    }
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
    @Provides
    @Singleton
    fun providesFirebaseDatabaseInstance():FirebaseDatabase{
        return FirebaseDatabase.getInstance("https://score-4-fb9aa-default-rtdb.asia-southeast1.firebasedatabase.app")
    }
    @Singleton
    @Provides
    fun providesFirebaseAuthInstance(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}