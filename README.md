# Reporting SDK – Proof of Concept

This project is a test task for the project Proof of Concept for integrating a reporting SDK into web frontends, developed as part of the JetBrains Internship Test Task.

## Tech Stack
- Kotlin
- Gradle
- JDK 17

## Project Structure
- `src/main/kotlin` – main implementation (Kotlin)
- `src/main/java` – main implementation (Java)
- `src/test/kotlin` – tests

## How to Run
```bash
./gradlew test



# Simple Tests for File Downloader

## What This Is

Very simple Kotlin tests for  FileMetadata and SimpleDownloader classes.

## Important: Start Server First Before Testing!

You may start a web server locally using the following docker command:

docker run --rm -p 8080:80 -v /path/to/your/local/directory:/usr/local/apache2/htdocs/ httpd:latest

Then you can access files from  local directory via localhost:8080. For example:

http://localhost:8080/file.exe

Before running tests, make sure server is running:

```
http://localhost:8080
```

The tests will use your real server to download files.

## How to Run

```bash
./gradlew test
```

## What Gets Tested

### FileMetadataTest
- ✅ Download valid file from  server
- ✅ Handle invalid URL
- ✅ Handle wrong server

### SimpleDownloaderTest
- ✅ Download file successfully
- ✅ Handle 404 (file not found)
- ✅ Handle invalid URL
- ✅ Download multiple files

## Project Structure

```
src/
  test/
    kotlin/
      FileMetadataTest.kt
      SimpleDownloaderTest.kt
build.gradle.kts
```

## Notes

- Tests use `http://localhost:8080/file.exe` from the server
- Make sure `file.exe` exists on the server before running tests
- Tests download to temporary directories (auto-cleaned)


## If Tests Fail

1. Check your server is running on port 8080
2. Check file.exe exists on your server
3. Check no firewall blocking localhost

