import kotlinx.coroutines.*
import java.io.IOException
import java.io.InputStream
import java.io.RandomAccessFile
import java.net.HttpURLConnection
import java.net.URL

/***
 * @Author: Masood
 * @Date: 2026-02-12
 * @Description: Parallel Downloader in Kotlin using basic Coroutine syntax
 */
class ParallelDownloaderCoroutines {

    suspend fun downloadInParallel(fileUrl: String, destinationPath: String, numberOfCoroutines: Int) = coroutineScope {

        val metadata = FileMetadata()
        val fileSize = withContext(Dispatchers.IO) {
            metadata.getMetaData(fileUrl)
        }

        if (fileSize == -1L) {
            println("Download failed: Invalid content length or ranges not supported.")
            return@coroutineScope
        }

        println("File Size: $fileSize bytes")

        val partSize = fileSize / numberOfCoroutines
        val jobs = mutableListOf<Job>()

        for (i in 0 until numberOfCoroutines) {

            val startByte = i * partSize
            val endByte: Long

            if (i == numberOfCoroutines - 1) endByte = fileSize - 1
            else endByte = startByte + partSize - 1


            val job = launch(Dispatchers.IO) {
                downloadChunk(fileUrl, destinationPath, startByte, endByte)
            }
            jobs.add(job)
        }

        jobs.joinAll()
        println("Parallel download complete.")
    }


    private fun downloadChunk(fileUrl: String, destinationPath: String, startByte: Long, endByte: Long) {
        var connection: HttpURLConnection? = null
        var inputStream: InputStream? = null
        var file: RandomAccessFile? = null

        try {
            val url = URL(fileUrl)
            connection = url.openConnection() as HttpURLConnection

            val range = "bytes=$startByte-$endByte"
            connection.setRequestProperty("Range", range)

            inputStream = connection.inputStream

            file = RandomAccessFile(destinationPath, "rw")
            file.seek(startByte)

            val buffer = ByteArray(4096)

            var bytesRead = inputStream.read(buffer)

            while (bytesRead != -1) {
                file.write(buffer, 0, bytesRead)
                bytesRead = inputStream.read(buffer)
            }

            println("Finished chunk: $range")

        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (file != null) file.close()
                if (inputStream != null) inputStream.close()
                if (connection != null) connection.disconnect()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}