/***
 * @Author: Masood
 * @Date: 2026-02-11
 * @Description: Simple Downloader class in java
 */

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SimpleDownloader {

    public void downloadFile(String fileURL, String outputPath) throws Exception {

        URL url = new URL(fileURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (InputStream in = connection.getInputStream();
             FileOutputStream out = new FileOutputStream(outputPath)) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        System.out.println("Download complete: " + outputPath);
    }


}

