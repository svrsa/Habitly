package com.example.habitly.data.local

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS study_sessions (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                durationMinutes INTEGER NOT NULL,
                completedAt INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }
}
