package com.example.myapplication.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Migration จากเวอร์ชัน 1 ไป 2
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS inspection_table (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                product_id INTEGER NOT NULL,
                inspection_date INTEGER NOT NULL,
                inspection_notes TEXT,
                FOREIGN KEY(product_id) REFERENCES product_table(id) ON DELETE CASCADE
            )
        """)
    }
}

