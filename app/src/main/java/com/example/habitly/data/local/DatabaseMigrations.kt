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

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            "ALTER TABLE study_tasks ADD COLUMN priority TEXT NOT NULL DEFAULT 'MEDIUM'"
        )
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            "ALTER TABLE study_sessions ADD COLUMN planEntryId INTEGER DEFAULT NULL"
        )
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS study_plans (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                taskId INTEGER NOT NULL,
                plannedDate INTEGER NOT NULL,
                blockDurationMinutes INTEGER NOT NULL,
                plannedBlocks INTEGER NOT NULL,
                completedBlocks INTEGER NOT NULL DEFAULT 0,
                createdAt INTEGER NOT NULL,
                FOREIGN KEY(taskId) REFERENCES study_tasks(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        connection.execSQL(
            "CREATE INDEX IF NOT EXISTS index_study_plans_taskId ON study_plans(taskId)"
        )
        connection.execSQL(
            "CREATE INDEX IF NOT EXISTS index_study_plans_plannedDate ON study_plans(plannedDate)"
        )
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS study_evidence (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                sessionId INTEGER NOT NULL,
                imagePath TEXT NOT NULL,
                note TEXT NOT NULL DEFAULT '',
                createdAt INTEGER NOT NULL,
                FOREIGN KEY(sessionId) REFERENCES study_sessions(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        connection.execSQL(
            "CREATE INDEX IF NOT EXISTS index_study_evidence_sessionId ON study_evidence(sessionId)"
        )
        connection.execSQL(
            "CREATE INDEX IF NOT EXISTS index_study_evidence_createdAt ON study_evidence(createdAt)"
        )
    }
}
