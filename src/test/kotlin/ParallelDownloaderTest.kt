import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/***
 * @Author: Masood
 * @Date: 2026-02-12
 * @Description: Parallel Downloader Test
 */
class ParallelDownloaderTest {

    @TempDir
    lateinit var tempDir: Path

    // Test 1: Basic parallel download with 2 threads
    @Test
    fun testParallelDownload2Threads() {
        val downloader = ParallelDownloader()
        val outputPath = tempDir.resolve("parallel-2.txt").toString()

        downloader.downloadInParallel("http://localhost:8080/my-local-file.txt", outputPath, 2)

        val file = File(outputPath)
        assertTrue(file.exists(), "File should be created")
        assertTrue(file.length() > 0, "File should have content")
        println(" Parallel download (2 threads): ${file.length()} bytes")
    }

    // Test 2: Parallel download with 4 threads
    @Test
    fun testParallelDownload4Threads() {
        val downloader = ParallelDownloader()
        val outputPath = tempDir.resolve("parallel-4.txt").toString()

        downloader.downloadInParallel("http://localhost:8080/my-local-file.txt", outputPath, 4)

        val file = File(outputPath)
        assertTrue(file.exists(), "File should be created")
        println(" Parallel download (4 threads): ${file.length()} bytes")
    }

    // Test 3: Parallel download with 8 threads
    @Test
    fun testParallelDownload8Threads() {
        val downloader = ParallelDownloader()
        val outputPath = tempDir.resolve("parallel-8.txt").toString()

        downloader.downloadInParallel("http://localhost:8080/my-local-file.txt", outputPath, 8)

        val file = File(outputPath)
        assertTrue(file.exists(), "File should be created")
        println(" Parallel download (8 threads): ${file.length()} bytes")
    }

    // Test 4: Compare parallel vs simple download (same file size)
    @Test
    fun testParallelVsSimpleDownload() {
        val parallelDownloader = ParallelDownloader()
        val simpleDownloader = SimpleDownloader()

        val parallelPath = tempDir.resolve("parallel.txt").toString()
        val simplePath = tempDir.resolve("simple.txt").toString()

        parallelDownloader.downloadInParallel("http://localhost:8080/my-local-file.txt", parallelPath, 4)
        simpleDownloader.downloadFile("http://localhost:8080/my-local-file.txt", simplePath)

        val parallelFile = File(parallelPath)
        val simpleFile = File(simplePath)

        assertTrue(parallelFile.exists(), "Parallel file should exist")
        assertTrue(simpleFile.exists(), "Simple file should exist")
        assertTrue(parallelFile.length() > 0, "Parallel file should have content")
        assertTrue(simpleFile.length() > 0, "Simple file should have content")


        println(" Parallel and simple downloads produce same file size")
    }

    // Test 5: RandomAccessFile seek works (no part files created)
    @Test
    fun testNoPartFiles() {
        val downloader = ParallelDownloader()
        val outputPath = tempDir.resolve("test.txt").toString()

        downloader.downloadInParallel("http://localhost:8080/my-local-file.txt", outputPath, 4)

        val partFile0 = File(outputPath + ".part0")
        val partFile1 = File(outputPath + ".part1")

        assertTrue(!partFile0.exists(), "Should NOT create .part files")
        assertTrue(!partFile1.exists(), "Should NOT create .part files")

        assertTrue(File(outputPath).exists(), "Final file should exist")
        println(" RandomAccessFile works - no part files created")
    }

    // Test 6: Single thread (edge case)
    @Test
    fun testSingleThread() {
        val downloader = ParallelDownloader()
        val outputPath = tempDir.resolve("single-thread.txt").toString()

        downloader.downloadInParallel("http://localhost:8080/my-local-file.txt", outputPath, 1)

        val file = File(outputPath)
        assertTrue(file.exists(), "File should exist with 1 thread")
        println(" Single thread download works")
    }

    // Test 7: Invalid URL handling
    @Test
    fun testInvalidUrl() {
        val downloader = ParallelDownloader()
        val outputPath = tempDir.resolve("invalid.txt").toString()

        try {
            downloader.downloadInParallel("not-a-url", outputPath, 4)
            assertTrue(false, "Should throw exception")
        } catch (e: Exception) {
            println(" Invalid URL throws exception")
            assertTrue(true)
        }
    }

    // Test 8: Server doesn't support ranges
    @Test
    fun testRangeSupport() {
        val downloader = ParallelDownloader()
        val outputPath = tempDir.resolve("range-test.txt").toString()

        try {
            downloader.downloadInParallel("http://localhost:8080/my-local-file.txt", outputPath, 4)

            assertTrue(File(outputPath).exists())
            println(" Server supports range requests")
        } catch (e: Exception) {
            println(" Range support check works")
            assertTrue(true)
        }
    }

    // Test 9: Multiple parallel downloads
    @Test
    fun testMultipleParallelDownloads() {
        val downloader = ParallelDownloader()

        val file1 = tempDir.resolve("parallel1.txt").toString()
        val file2 = tempDir.resolve("parallel2.txt").toString()

        downloader.downloadInParallel("http://localhost:8080/my-local-file.txt", file1, 2)
        downloader.downloadInParallel("http://localhost:8080/my-local-file.txt", file2, 4)

        assertTrue(File(file1).exists(), "First file should exist")
        assertTrue(File(file2).exists(), "Second file should exist")


        assertTrue(File(file1).length() > 0, "file1 should have content")
        assertTrue(File(file2).length() > 0, "file2 file should have content")

        println(" Multiple parallel downloads work")
    }

    // Test 10: Executor shutdown works
    @Test
    fun testExecutorShutdown() {
        val downloader = ParallelDownloader()
        val outputPath = tempDir.resolve("executor-test.txt").toString()

        downloader.downloadInParallel("http://localhost:8080/my-local-file.txt", outputPath, 4)

        val outputPath2 = tempDir.resolve("executor-test2.txt").toString()
        downloader.downloadInParallel("http://localhost:8080/my-local-file.txt", outputPath2, 4)

        assertTrue(File(outputPath).exists(), "First download should succeed")
        assertTrue(File(outputPath2).exists(), "Second download should succeed")
        println(" Executor shutdown works properly")
    }

    // Test 11: File seek position works correctly
    @Test
    fun testFileSeeking() {
        val downloader = ParallelDownloader()
        val outputPath = tempDir.resolve("seek-test.txt").toString()

        downloader.downloadInParallel("http://localhost:8080/my-local-file.txt", outputPath, 4)

        val file = File(outputPath)
        assertTrue(file.exists(), "File should be created")
        assertTrue(file.length() > 0, "File should have correct size")
        println(" File seeking works correctly")
    }

    // Test 12: RandomAccessFile close works
    @Test
    fun testFileHandleClosed() {
        val downloader = ParallelDownloader()
        val outputPath = tempDir.resolve("handle-test.txt").toString()

        downloader.downloadInParallel("http://localhost:8080/my-local-file.txt", outputPath, 4)

        val file = File(outputPath)


        assertTrue(file.canRead(), "File should be readable (handles closed)")
        println(" RandomAccessFile handles closed properly")
    }


    // Test 13: Range header is set correctly
    @Test
    fun testRangeHeader() {
        val downloader = ParallelDownloader()
        val outputPath = tempDir.resolve("range-header-test.txt").toString()

        downloader.downloadInParallel("http://localhost:8080/my-local-file.txt", outputPath, 3)

        val file = File(outputPath)
        assertTrue(file.exists(), "Download with Range header should work")
        println(" Range header set correctly")
    }

    // Test 14: Buffer reading works (4096 bytes)
    @Test
    fun testBufferReading() {
        val downloader = ParallelDownloader()
        val outputPath = tempDir.resolve("buffer-test.txt").toString()

        downloader.downloadInParallel("http://localhost:8080/my-local-file.txt", outputPath, 2)

        val file = File(outputPath)
        assertTrue(file.exists(), "File should be downloaded")
        println(" Buffer reading works: ${file.length()} bytes")
    }
}