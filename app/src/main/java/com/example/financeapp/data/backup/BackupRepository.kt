package com.example.financeapp.data.backup

import java.io.InputStream
import java.io.OutputStream

interface BackupRepository {
    suspend fun exportBackup(outputStream: OutputStream)
    suspend fun importBackup(inputStream: InputStream)
}
