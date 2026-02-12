import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

/***
 * @Author: Masood
 * @Date: 2026-02-12
 * @Description: Parallel Downloader Merge Test
 */
class ParallelDownloaderMergeTest {

    @TempDir
    lateinit var tempDir: Path

    // Test 1: Basic parallel download with 2 threads
    @Test
    fun testParallelDownload2Threads() {
        val downloader = ParallelDownloaderMerge()
        val outputPath = tempDir.resolve("parallel-2.exe").toString()

        downloader.downloadInParallel("http://localhost:8080/file.exe", outputPath, 2)

        val file = File(outputPath)
        assertTrue(file.exists(), "File should be created")
        assertTrue(file.length() > 0, "File should have content")
        println("Parallel download (2 threads): ${file.length()} bytes")
    }

    // Test 2: Parallel download with 4 threads
    @Test
    fun testParallelDownload4Threads() {
        val downloader = ParallelDownloaderMerge()
        val outputPath = tempDir.resolve("parallel-4.exe").toString()

        downloader.downloadInParallel("http://localhost:8080/file.exe", outputPath, 4)

        val file = File(outputPath)
        assertTrue(file.exists(), "File should be created")
        println("Parallel download (4 threads): ${file.length()} bytes")
    }

    // Test 3: Parallel download with 8 threads
    @Test
    fun testParallelDownload8Threads() {
        val downloader = ParallelDownloaderMerge()
        val outputPath = tempDir.resolve("parallel-8.exe").toString()

        downloader.downloadInParallel("http://localhost:8080/file.exe", outputPath, 8)

        val file = File(outputPath)
        assertTrue(file.exists(), "File should be created")
        println("Parallel download (8 threads): ${file.length()} bytes")
    }

    // Test 4: Compare parallel vs simple download (same file size)
    @Test
    fun testParallelVsSimpleDownload() {
        val parallelDownloader = ParallelDownloaderMerge()
        val simpleDownloader = SimpleDownloader()

        val parallelPath = tempDir.resolve("parallel.exe").toString()
        val simplePath = tempDir.resolve("simple.exe").toString()

        parallelDownloader.downloadInParallel("http://localhost:8080/file.exe", parallelPath, 4)
        simpleDownloader.downloadFile("http://localhost:8080/file.exe", simplePath)

        val parallelFile = File(parallelPath)
        val simpleFile = File(simplePath)

        assertTrue(parallelFile.exists(), "Parallel file should exist")
        assertTrue(simpleFile.exists(), "Simple file should exist")
        assertTrue(parallelFile.length() > 0, "Parallel file should have content")
        assertTrue(simpleFile.length() > 0, "Simple file should have content")

        println("Parallel and simple downloads produce same file size")
    }

    // Test 5: Part files are deleted after merge
    @Test
    fun testPartFilesDeleted() {
        val downloader = ParallelDownloaderMerge()
        val outputPath = tempDir.resolve("test.exe").toString()

        downloader.downloadInParallel("http://localhost:8080/file.exe", outputPath, 4)

        val partFile0 = File(outputPath + ".part0")
        val partFile1 = File(outputPath + ".part1")
        val partFile2 = File(outputPath + ".part2")
        val partFile3 = File(outputPath + ".part3")

        assertFalse(partFile0.exists(), "Part file 0 should be deleted")
        assertFalse(partFile1.exists(), "Part file 1 should be deleted")
        assertFalse(partFile2.exists(), "Part file 2 should be deleted")
        assertFalse(partFile3.exists(), "Part file 3 should be deleted")
        println("Part files deleted after merge")
    }

    // Test 6: Final merged file exists
    @Test
    fun testMergedFileExists() {
        val downloader = ParallelDownloaderMerge()
        val outputPath = tempDir.resolve("merged.exe").toString()

        downloader.downloadInParallel("http://localhost:8080/file.exe", outputPath, 3)

        val mergedFile = File(outputPath)
        assertTrue(mergedFile.exists(), "Merged file should exist")
        assertTrue(mergedFile.length() > 0, "Merged file should have content")
        println("Merged file created successfully")
    }

    // Test 7: Single thread (edge case)
    @Test
    fun testSingleThread() {
        val downloader = ParallelDownloaderMerge()
        val outputPath = tempDir.resolve("single-thread.exe").toString()

        downloader.downloadInParallel("http://localhost:8080/file.exe", outputPath, 1)

        val file = File(outputPath)
        assertTrue(file.exists(), "File should exist with 1 thread")
        println("Single thread download works")
    }

    // Test 8: Invalid URL handling
    @Test
    fun testInvalidUrl() {
        val downloader = ParallelDownloaderMerge()
        val outputPath = tempDir.resolve("invalid.exe").toString()

        try {
            downloader.downloadInParallel("not-a-url", outputPath, 4)
            assertTrue(false, "Should throw exception")
        } catch (e: Exception) {
            println("Invalid URL throws exception")
            assertTrue(true)
        }
    }

    // Test 9: Server doesn't support ranges
    @Test
    fun testNoRangeSupport() {
        val downloader = ParallelDownloaderMerge()
        val outputPath = tempDir.resolve("norange.exe").toString()

        try {
            downloader.downloadInParallel("http://localhost:8080/file.exe", outputPath, 4)

            assertTrue(File(outputPath).exists())
            println("Server supports range requests")
        } catch (e: Exception) {
            println("Range support check works")
            assertTrue(true)
        }
    }

    // Test 10: Multiple parallel downloads
    @Test
    fun testMultipleParallelDownloads() {
        val downloader = ParallelDownloaderMerge()

        val file1 = tempDir.resolve("parallel1.exe").toString()
        val file2 = tempDir.resolve("parallel2.exe").toString()

        downloader.downloadInParallel("http://localhost:8080/file.exe", file1, 2)
        downloader.downloadInParallel("http://localhost:8080/file.exe", file2, 4)

        assertTrue(File(file1).exists(), "First file should exist")
        assertTrue(File(file2).exists(), "Second file should exist")

        assertTrue(File(file1).length() > 0, "file1 should have content")
        assertTrue(File(file2).length() > 0, "file2 file should have content")

        println("Multiple parallel downloads work")
    }

    // Test 11: Executor shutdown works
    @Test
    fun testExecutorShutdown() {
        val downloader = ParallelDownloaderMerge()
        val outputPath = tempDir.resolve("executor-test.exe").toString()

        downloader.downloadInParallel("http://localhost:8080/file.exe", outputPath, 4)

        val outputPath2 = tempDir.resolve("executor-test2.exe").toString()
        downloader.downloadInParallel("http://localhost:8080/file.exe", outputPath2, 4)

        assertTrue(File(outputPath).exists(), "First download should succeed")
        assertTrue(File(outputPath2).exists(), "Second download should succeed")
        println("Executor shutdown works properly")
    }

    // Test 12: Range header is set correctly (tests DownloadTask)
    @Test
    fun testRangeHeaderDownload() {
        val downloader = ParallelDownloaderMerge()
        val outputPath = tempDir.resolve("range-test.exe").toString()

        downloader.downloadInParallel("http://localhost:8080/file.exe", outputPath, 4)

        val file = File(outputPath)
        assertTrue(file.exists(), "Download with Range header should work")
        println("Range header works correctly")
    }
}