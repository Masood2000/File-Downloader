# File Downloader (Java & Kotlin)

This project is a test task for the project **Proof of Concept for integrating a reporting SDK into web frontends**, developed as part of the JetBrains Internship Test Task.

This project explores concurrency patterns in **Java (Threads /
Executors)** and **Kotlin (Coroutines)**, documenting from
a simple sequential downloader to parallel solution.

------------------------------------------------------------------------


##  Development

This project was built iteratively to explore efficient ways to handle
**network I/O** and **file systems**.

### Metadata Analysis

Before downloading, the application first analyzes the file using
`FileMetadata.java`, which sends an HTTP `HEAD` request to:

-   Retrieve `Content-Length` (file size)
-   Verify `Accept-Ranges` support (required for parallel downloads)

------------------------------------------------------------------------

### Simple Downloader

`SimpleDownloader.java` implements a standard **single-threaded**
download.

This version serves as the baseline for performance comparison.

------------------------------------------------------------------------

### Strategy A: The "Merge" Approach

`ParallelDownloaderMerge.java`

**Logic:** - Split the file into multiple parts - Download each part
into separate temporary files (`.part0`, `.part1`, etc.) - Merge all
parts into the final file after completion

**Outcome:** - Faster than single-threaded download on large files - However, the final
merge step introduces additional disk I/O overhead and temporary storage
usage

------------------------------------------------------------------------

### Strategy B: The "Seek" Optimization (RandomAccessFile)

`ParallelDownloader.java`

To eliminate the merging overhead, I searched for more efficient file handling methods and found out about java.io.RandomAccessFile. Realizing this class allows writing to specific positions in a file, I decided to use it to implement ParallelDownloader.java.

**Logic:** - Pre-allocate the target file - Use
`java.io.RandomAccessFile` - Call `file.seek(offset)` to write chunks
directly into their correct byte positions

**Result:** - Eliminates the merge step - Cleaner implementation -
Significantly improved performance



------------------------------------------------------------------------

###  Modern Concurrency: Kotlin Coroutines

`ParallelFileDownloader.kt` (class: `ParallelDownloaderCoroutines`)

**Logic:** - Uses Kotlin Coroutines with `Dispatchers.IO` - Replaces
heavy Java threads with lightweight coroutines - Designed specifically
for I/O-bound workloads

------------------------------------------------------------------------

## Technical Architecture

The core logic relies on **HTTP Range Requests**.

### Workflow

1.  **HEAD Request** → Fetch file metadata\
2.  **Partitioning** → Divide file size into `N` chunks\
3.  **Parallel Execution** → Each thread/coroutine downloads a byte
    range:
    -   Thread 1 → `bytes=0-1023`
    -   Thread 2 → `bytes=1024-2047`
    -   etc.
4.  **Assembly** → Chunks are written directly into the final file using
    `RandomAccessFile`

------------------------------------------------------------------------

##  Prerequisites

- **JDK:** 17
- **Kotlin:** 1.9+
- **Gradle:** 8.4+ 

------------------------------------------------------------------------

##  Setting Up a Local Test Server

To simulate downloads locally using Apache HTTPD:

``` bash
# Serve files from current directory at http://localhost:8080
docker run --rm -p 8080:80 -v $(pwd):/usr/local/apache2/htdocs/ httpd:latest
```

Ensure you place a dummy file (e.g., `my-local-file.txt`) in the mapped
directory.

------------------------------------------------------------------------

## Project Structure

    src/
    ├── main/
    │   ├── java/
    │   │   ├── FileMetadata.java
    │   │   ├── SimpleDownloader.java
    │   │   ├── ParallelDownloader.java
    │   │   └── ParallelDownloaderMerge.java
    │   ├── kotlin/
    │   │   ├── ParallelFileDownloader.kt
    │   │   └── Main.kt
    └── test/
        └── kotlin/
            ├── FileMetaDataTest.kt
            └── ParallelDownloaderMergeTest.kt

------------------------------------------------------------------------

## Testing

This project uses:

-   **JUnit 5**
-   **Kotlin Test**
-   **GitHub Actions**

### Continuous Integration Workflow (just for own learning not fully implemented)

The GitHub Actions pipeline:

1.  Spins up an Ubuntu runner with JDK 17
2.  Creates a dummy file
3.  Starts a lightweight Python HTTP server:

``` bash
python3 -m http.server 8080
```

4.  Runs:

``` bash
./gradlew test
```

All tests automatically verify download correctness on every push.

------------------------------------------------------------------------

## ▶ Running Tests Locally

``` bash
./gradlew test
```

------------------------------------------------------------------------

## Benchmark Results

Example benchmark output (results may vary depending on disk/network):

For the testing purpose I tried this with a big exe file.

    --- FETCHING METADATA ---
    Accept-Ranges: bytes
    Content-Length: 104857600
    --------------------------

    [1] Starting Simple Downloader...
    [2] Starting Parallel Downloader (Seek)...
    [3] Starting Parallel Downloader (Merge)...
    [4] Starting Parallel Downloader (Coroutines)...

    >> Simple Downloader finished in 4200 ms
    >> Parallel Downloader (Seek) finished in 1100 ms
    >> Parallel Downloader (Merge) finished in 1450 ms
    >> Parallel Downloader (Coroutines) finished in 1050 ms


------------------------------------------------------------------------

##  Author

**Masood**\
Master's Student --- MIAGE 2IS (Innovative Information Systems)\
Université Toulouse Capitole, Toulouse, France
