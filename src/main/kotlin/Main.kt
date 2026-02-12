package com.masood

import FileMetadata
import ParallelDownloader
import ParallelDownloaderCoroutines
import ParallelDownloaderMerge
import SimpleDownloader
import java.io.File
import java.util.*

/***
 * @Author: Masood
 * @Date: 2026-02-11
 */
suspend fun main() {

    val NUM_THREADS: Int = 8
    val url = "http://localhost:8080/my-local-file.txt"

    val date = Date()

    val time = date.toLocaleString().replace(" ", "-").replace(",", "").replace(":", "-")

    val outputFile = File("D:/Learning/jetbrains/Integrating reporting SDK into web frontends/server/downloads/")
    val outputFilePath = outputFile.absolutePath + "/downloaded-file-" + time + ".txt"

    val fileMetadata = FileMetadata()
    val simpleDownloader = SimpleDownloader()
    val parallelDownloader = ParallelDownloader()
    val mergeParallelDownloader = ParallelDownloaderMerge()
    val parallelDownloaderCoroutines = ParallelDownloaderCoroutines()

    // --- METADATA CHECK ---
    println("--- FETCHING METADATA ---")
    fileMetadata.getMetaData(url)
    println("--------------------------\n")

    // --- 1. SIMPLE DOWNLOADER ---
    println("[1] Starting Simple Downloader...")
    val startSimple = System.currentTimeMillis()
    simpleDownloader.downloadFile(url, outputFilePath)
    val endSimple = System.currentTimeMillis()

    // --- 2. PARALLEL DOWNLOADER (SEEK STRATEGY) ---
    println("[2] Starting Parallel Downloader (Seek)...")
    val startSeek = System.currentTimeMillis()
    parallelDownloader.downloadInParallel(url, outputFilePath, NUM_THREADS)
    val endSeek = System.currentTimeMillis()

    // --- 3. PARALLEL DOWNLOADER (MERGE STRATEGY) ---
    println("[3] Starting Parallel Downloader (Merge)...")
    val startMerge = System.currentTimeMillis()
    mergeParallelDownloader.downloadInParallel(url, outputFilePath, NUM_THREADS)
    val endMerge = System.currentTimeMillis()

    // --- 4. PARALLEL DOWNLOADER (COROUTINES) ---
    println("[4] Starting Parallel Downloader (Coroutines)...")
    val startCoroutines = System.currentTimeMillis()
    parallelDownloaderCoroutines.downloadInParallel(url, outputFilePath, NUM_THREADS)
    val endCoroutines = System.currentTimeMillis()

    val durationCoroutines = endCoroutines - startCoroutines

    println("--------------------------------------------------")
    println("All downloaders completed for testing.")


    println(">> Simple Downloader finished in ${endSimple - startSimple} ms\n")
    println(">> Parallel Downloader (Seek) finished in ${endSeek - startSeek} ms\n")
    println(">> Parallel Downloader (Merge) finished in ${endMerge - startMerge} ms\n")
    println(">> Parallel Downloader (Coroutines) finished in $durationCoroutines ms")


}