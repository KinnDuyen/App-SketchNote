package com.example.sketchnote.di

import android.content.Context
import com.example.sketchnote.data.local.SketchNoteDatabase
import com.example.sketchnote.data.local.dao.ChecklistItemDao
import com.example.sketchnote.data.local.dao.NoteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * AppModule: Nơi cung cấp các đối tượng (dependencies) cho toàn bộ ứng dụng.
 * Hilt sẽ dựa vào Module này để biết cách khởi tạo Database và các DAO.
 */
@Module
@InstallIn(SingletonComponent::class) // SingletonComponent giúp các đối tượng tồn tại suốt vòng đời ứng dụng
object AppModule {

    /**
     * Cung cấp thực thể duy nhất của Room Database (SketchNoteDatabase).
     * @Singleton đảm bảo chỉ có 1 instance duy nhất được tạo ra để tiết kiệm tài nguyên.
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SketchNoteDatabase {
        return SketchNoteDatabase.getDatabase(context)
    }

    /**
     * Cung cấp NoteDao để thực hiện các thao tác CRUD (Thêm, Sửa, Xóa, Truy vấn) ghi chú.
     */
    @Provides
    @Singleton
    fun provideNoteDao(db: SketchNoteDatabase): NoteDao = db.noteDao()

    /**
     * Cung cấp ChecklistItemDao để quản lý các mục trong danh sách kiểm tra (nếu có).
     */
    @Provides
    @Singleton
    fun provideChecklistItemDao(db: SketchNoteDatabase): ChecklistItemDao = db.checklistItemDao()
}
