package com.blacklivesmatter.policebrutality.di.module

import android.content.Context
import com.blacklivesmatter.policebrutality.data.AppDatabase
import com.blacklivesmatter.policebrutality.data.IncidentDao
import dagger.Module
import dagger.Provides

@Module
class DaoModule {

    @Provides
    fun provideIncidentDao(context: Context): IncidentDao {
        return AppDatabase.getInstance(context).incidentDao()
    }
}