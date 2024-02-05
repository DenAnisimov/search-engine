package searchengine.utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class SiteConnection {

    private Connection connection;

    public SiteConnection(String path) {
        connection = Jsoup.connect(path)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT " +
                        "5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .referrer("http://www.google.com");
    }

    public Document getDocument() {
        try {
            return connection.get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getStatusCode() {
        try {
            return connection.method(Connection.Method.GET).execute().statusCode();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
