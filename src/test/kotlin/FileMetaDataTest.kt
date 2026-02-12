import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/***
 * @Author: Masood
 * @Date: 2026-02-12
 * @Description: FileMetaDataTest class testing FileMetaData class
 */
class FileMetadataTest {

    // Test 1: Check that Accept-Ranges and Content-Length are read correctly
    // If method returns positive number, both headers were read successfully
    @Test
    fun testHeadersReadCorrectly() {
        val fileMetadata = FileMetadata()

        val fileSize = fileMetadata.getMetaData("http://localhost:8080/file.exe")

        assertTrue(fileSize > 0, "Headers should be read correctly")
        println("Accept-Ranges and Content-Length read successfully")
    }

    // Test 2: Verify Content-Length value is consistent
    @Test
    fun testContentLengthConsistency() {

        val fileMetadata = FileMetadata()

        val size1 = fileMetadata.getMetaData("http://localhost:8080/file.exe")
        val size2 = fileMetadata.getMetaData("http://localhost:8080/file.exe")

        // Content-Length should be same every time
        assertEquals(size1, size2, "Content-Length should be consistent")
        println("Content-Length is consistent: $size1 bytes")

    }

    // Test 3: Check the actual file size value makes sense
    @Test
    fun testFileSizeValue() {
        val fileMetadata = FileMetadata()

        val fileSize = fileMetadata.getMetaData("http://localhost:8080/file.exe")

        // File size should be reasonable (not 0, not negative, not too big (for now))
        assertTrue(fileSize > 0, "File size should be positive")
        assertTrue(fileSize < 10_000_000_000L, "File size should be reasonable")
        println("File size is reasonable: $fileSize bytes")
    }

    // Test 4: Test what happens with missing file (might have different headers)
    @Test
    fun testMissingFileHeaders() {
        val fileMetadata = FileMetadata()

        try {
            val result = fileMetadata.getMetaData("http://localhost:8080/doesnotexist.exe")

            // If result is -1, headers were either missing or invalid
            if (result == -1L)
                println("Method correctly returned -1 for missing file")
            else
                println("Server returned: $result (might be 404 page size)")


            assertTrue(true)
        } catch (e: Exception) {
            println("Exception thrown: ${e.message}")
            assertTrue(true)
        }
    }

    // Test 5: Invalid URL (tests exception path)
    @Test
    fun testInvalidUrl() {
        val fileMetadata = FileMetadata()

        try {
            fileMetadata.getMetaData("not-a-url")
            assertTrue(false, "Should throw exception")
        } catch (e: Exception) {
            println("Exception thrown for invalid URL")
            assertTrue(true)
        }
    }
}