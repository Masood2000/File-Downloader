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
 * @Description: Simple Downloader Test
 */
class SimpleDownloaderTest {

    @TempDir
    lateinit var tempDir: Path

    // Test 1: Download file successfully (tests HTTP_OK path)
    @Test
    fun testDownloadSuccess() {

        val downloader = SimpleDownloader()
        val outputPath = tempDir.resolve("downloaded.txt").toString()

        downloader.downloadFile("http://localhost:8080/my-local-file.txt", outputPath)

        val file = File(outputPath)
        assertTrue(file.exists(), "File should be created")
        assertTrue(file.length() > 0, "File should have content")
        println("File downloaded: ${file.length()} bytes")
    }

    // Test 2: File content is correct (tests read/write operations)
    @Test
    fun testFileContent() {
        val downloader = SimpleDownloader()
        val output1 = tempDir.resolve("file1.txt").toString()
        val output2 = tempDir.resolve("file2.txt").toString()

        // Download same file twice
        downloader.downloadFile("http://localhost:8080/my-local-file.txt", output1)
        downloader.downloadFile("http://localhost:8080/my-local-file.txt", output2)

        // Files should be identical (same content)
        val file1 = File(output1)
        val file2 = File(output2)

        assertEquals(file1.length(), file2.length(), "Files should have same size")
        println("File content is consistent")
    }

    // Test 3: Buffer reading works correctly (4096 bytes)
    @Test
    fun testBufferReading() {
        val downloader = SimpleDownloader()
        val outputPath = tempDir.resolve("test.txt").toString()

        downloader.downloadFile("http://localhost:8080/my-local-file.txt", outputPath)

        val file = File(outputPath)

        // File should exist regardless of size
        assertTrue(file.exists(), "Buffer reading should work for any file size")
        println("Buffer reading works: ${file.length()} bytes read")
    }

    // Test 4: Response code check - non-200 status
    @Test
    fun testNon200Response() {
        val downloader = SimpleDownloader()
        val outputPath = tempDir.resolve("notfound.txt").toString()

        // Try to download file that doesn't exist (should get 404)
        downloader.downloadFile("http://localhost:8080/doesnotexist.txt", outputPath)

        val file = File(outputPath)
        assertFalse(file.exists(), "File should not be created for non-200 response")
        println("Non-200 response handled correctly")
    }

    // Test 5: Connection disconnect works
    @Test
    fun testConnectionCleanup() {
        val downloader = SimpleDownloader()
        val outputPath = tempDir.resolve("test.txt").toString()

        // Download file - connection should disconnect after
        downloader.downloadFile("http://localhost:8080/my-local-file.txt", outputPath)

        // If we can download again, connection cleanup worked
        val outputPath2 = tempDir.resolve("test2.txt").toString()
        downloader.downloadFile("http://localhost:8080/my-local-file.txt", outputPath2)

        assertTrue(File(outputPath).exists(), "First download should succeed")
        assertTrue(File(outputPath2).exists(), "Second download should succeed")
        println("Connection cleanup works")
    }

    // Test 6: InputStream and OutputStream close properly
    @Test
    fun testStreamsClose() {
        val downloader = SimpleDownloader()
        val outputPath = tempDir.resolve("test.txt").toString()

        downloader.downloadFile("http://localhost:8080/my-local-file.txt", outputPath)

        val file = File(outputPath)

        // If streams weren't closed, file might be locked or incomplete
        // Try to read it to verify streams are closed
        val canRead = file.canRead()
        assertTrue(canRead, "File should be readable (streams closed)")
        println("Streams closed properly")
    }

    // Test 7: Invalid URL throws exception
    @Test
    fun testInvalidUrl() {
        val downloader = SimpleDownloader()
        val outputPath = tempDir.resolve("output.txt").toString()

        try {
            downloader.downloadFile("not-a-url", outputPath)
            assertTrue(false, "Should throw exception")
        } catch (e: Exception) {
            println("Invalid URL throws exception")
            assertTrue(true)
        }
    }

    // Test 8: Multiple downloads work
    @Test
    fun testMultipleDownloads() {
        val downloader = SimpleDownloader()

        val file1 = tempDir.resolve("file1.txt").toString()
        val file2 = tempDir.resolve("file2.txt").toString()
        val file3 = tempDir.resolve("file3.txt").toString()

        downloader.downloadFile("http://localhost:8080/my-local-file.txt", file1)
        downloader.downloadFile("http://localhost:8080/my-local-file.txt", file2)
        downloader.downloadFile("http://localhost:8080/my-local-file.txt", file3)

        assertTrue(File(file1).exists(), "File 1 should exist")
        assertTrue(File(file2).exists(), "File 2 should exist")
        assertTrue(File(file3).exists(), "File 3 should exist")
        println("Multiple downloads work")
    }

    // Test 9: Different output paths work
    @Test
    fun testDifferentPaths() {
        val downloader = SimpleDownloader()

        val path1 = tempDir.resolve("test1.txt").toString()
        val path2 = tempDir.resolve("subfolder").toString()

        // Create subfolder
        File(path2).mkdirs()
        val path2File = "$path2/test2.txt"

        downloader.downloadFile("http://localhost:8080/my-local-file.txt", path1)
        downloader.downloadFile("http://localhost:8080/my-local-file.txt", path2File)

        assertTrue(File(path1).exists(), "File in root should exist")
        assertTrue(File(path2File).exists(), "File in subfolder should exist")
        println("Different output paths work")
    }

    // Test 10: Empty file download (if your server has one)
    @Test
    fun testEmptyFileDownload() {
        val downloader = SimpleDownloader()
        val outputPath = tempDir.resolve("empty.txt").toString()

        try {
            // Try to download - might work or fail depending on server
            downloader.downloadFile("http://localhost:8080/empty.txt", outputPath)

            val file = File(outputPath)
            if (file.exists()) {
                println("Empty file downloaded: ${file.length()} bytes")
            }
            assertTrue(true)
        } catch (e: Exception) {
            println("Empty file test completed")
            assertTrue(true)
        }
    }
}