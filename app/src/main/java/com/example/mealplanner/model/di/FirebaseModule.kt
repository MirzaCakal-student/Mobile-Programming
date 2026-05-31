package com.example.mealplanner.model.di

import com.example.mealplanner.model.repository.community.CommunityRecipeRepository
import com.example.mealplanner.model.repository.community.CommunityRecipeRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that exposes the Firebase singletons to the rest of the app.
 *
 *   FirebaseAuth     — used by FirebaseAuthRepositoryImpl for sign-up / sign-in / sign-out
 *   FirebaseFirestore — used by CommunityRecipeRepositoryImpl for realtime CRUD
 *
 * Both are configured from google-services.json at build time, so we just grab the
 * default instances here. No URLs or credentials are hardcoded.
 */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides @Singleton
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore

    @Provides @Singleton
    fun provideCommunityRecipeRepository(
        impl: CommunityRecipeRepositoryImpl
    ): CommunityRecipeRepository = impl
}
