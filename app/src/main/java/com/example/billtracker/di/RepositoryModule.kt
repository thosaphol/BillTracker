package com.example.billtracker.di

import com.example.billtracker.data.export.JsonDataExporter
import com.example.billtracker.data.repository.BillRepositoryImpl
import com.example.billtracker.data.repository.CategoryRepositoryImpl
import com.example.billtracker.data.repository.HolidayRepositoryImpl
import com.example.billtracker.domain.repository.BillRepository
import com.example.billtracker.domain.repository.CategoryRepository
import com.example.billtracker.domain.repository.DataExporter
import com.example.billtracker.domain.repository.HolidayRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBillRepository(impl: BillRepositoryImpl): BillRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindDataExporter(impl: JsonDataExporter): DataExporter

    @Binds
    @Singleton
    abstract fun bindHolidayRepository(impl: HolidayRepositoryImpl): HolidayRepository
}