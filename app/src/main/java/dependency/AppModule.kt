package dependency

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
//    @Provides
//    @Singleton
//    fun provideFireStoreInstance(): FirebaseFirestore {
//        return FirebaseFirestore.getInstance()
//    }
//    @Provides
//    @Singleton
//    fun providesUserRepository(firestore: FirebaseFirestore,preferenceManager: PreferenceManager,firebaseStorage: FirebaseStorage):UserRepository{
//        return UserRepository(firestore,preferenceManager,firebaseStorage)
//    }
//    @Provides
//    @Singleton
//    fun providesGameSettingsRepository(firestore: FirebaseFirestore,userRepository: UserRepository):CreateGameRepository{
//        return CreateGameRepository(firestore,userRepository)
//    }
//    @Provides
//    @Singleton
//    fun providePreferenceManager(@ApplicationContext context:Context): PreferenceManager {
//        return PreferenceManager(context)
//    }
//    @Provides
//    @Singleton
//    fun providesDefaultCardOptions():DefaultCardOptions{
//        return DefaultCardOptions
//    }
//    @Provides
//    @Singleton
//    fun providesGameDetailsRepository(firestore: FirebaseFirestore,preferenceManager: PreferenceManager):GameDetailsRepository{
//        return GameDetailsRepository(firestore,preferenceManager)
//    }
//
////    @Provides
////    @Singleton
////    fun providesWaitingRoomRepository(firestore: FirebaseFirestore):WaitingRoomRepository{
////        return WaitingRoomRepository(firestore)
////    }
//    @Provides
//    @Singleton
//    fun providesFirebaseStorageInstance():FirebaseStorage{
//        return FirebaseStorage.getInstance()
//    }
//
//    @Provides
//    @Singleton
//    fun providesDownloadResourcesRepository(firebaseStorage: FirebaseStorage):DownloadGameResourcesRepository{
//        return DownloadGameResourcesRepository(firebaseStorage)
//    }
}