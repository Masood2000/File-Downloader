import java.net.HttpURLConnection;
import java.net.URL;

/***
 * @Author: Masood
 * @Date: 2026-02-11
 * @Description: FileMetaData class for getting the metadata of a file
 *              which we want to download.
 */
public class FileMetadata {

    public long getMetaData(String _url) throws Exception {

        URL url = new URL(_url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("HEAD");
        //connection.connect();

        String acceptRanges = connection.getHeaderField("Accept-Ranges");
        long contentLength = connection.getHeaderFieldLong("Content-Length", -1);

        System.out.println("Accept-Ranges: " + acceptRanges);
        System.out.println("Content-Length: " + contentLength);

        if (!"bytes".equals(acceptRanges)) {
            System.out.println("Warning: Server does not support range requests.");
        }

        if (contentLength <= 0) {
            return -1;
        }

        connection.disconnect();

        return contentLength;
    }
}
