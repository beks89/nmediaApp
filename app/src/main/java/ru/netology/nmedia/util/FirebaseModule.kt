package ru.netology.nmedia.util

import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class FirebaseModule {
    @Provides
    fun provideFirebaseMessaging() = FirebaseMessaging.getInstance()

    @Provides
    fun provideGoogleApiAvailability() = GoogleApiAvailability.getInstance()
}