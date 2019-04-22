package me.goddragon.teaseai.utils.libraries.ripme.ripper.rippers;

import me.goddragon.teaseai.utils.libraries.ripme.ripper.AbstractHTMLRipper;
import me.goddragon.teaseai.utils.libraries.ripme.utils.Http;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebtoonsRipper extends AbstractHTMLRipper {
    private Map<String,String> cookies = new HashMap<String,String>();

    public WebtoonsRipper(URL url) throws IOException {
        super(url);
    }

    @Override
    public String getHost() {
        return "webtoons";
    }

    @Override
    public String getDomain() {
        return "www.webtoons.com";
    }

    @Override
    public boolean canRip(URL url) {
        Pattern pat = Pattern.compile("https?://www.webtoons.com/[a-zA-Z-_]+/[a-zA-Z_-]+/([a-zA-Z0-9_-]*)/[a-zA-Z0-9_-]+/\\S*");
        Matcher mat = pat.matcher(url.toExternalForm());
        return mat.matches();
    }


    @Override
    public String getAlbumTitle(URL url) throws MalformedURLException {
        Pattern pat = Pattern.compile("https?://www.webtoons.com/[a-zA-Z-_]+/[a-zA-Z_-]+/([a-zA-Z0-9_-]*)/[a-zA-Z0-9_-]+/\\S*");
        Matcher mat = pat.matcher(url.toExternalForm());
        if (mat.matches()) {
            return getHost() + "_" + mat.group(1);
        }

        return super.getAlbumTitle(url);
    }

    @Override
    public String getGID(URL url) throws MalformedURLException {
        Pattern pat = Pattern.compile("https?://www.webtoons.com/[a-zA-Z]+/[a-zA-Z]+/([a-zA-Z0-9_-]*)/[a-zA-Z0-9_-]+/\\S*");
        Matcher mat = pat.matcher(url.toExternalForm());
        if (mat.matches()) {
            return mat.group(1);
        }
        throw new MalformedURLException("Expected URL format: http://www.webtoons.com/LANG/CAT/TITLE/VOL/, got: " + url);
    }


    @Override
    public List<String> getURLsFromPage(Document doc) {
        List<String> result = new ArrayList<String>();
        for (Element elem : doc.select("div.viewer_img > img")) {
            String origUrl = elem.attr("data-url");
            String[] finalUrl = origUrl.split("\\?type");
            result.add(finalUrl[0]);
        }
        return result;
    }

    @Override
    public void downloadURL(URL url, int index) {
        addURLToDownload(url, getPrefix(index), "", this.url.toExternalForm(), cookies);
    }

    @Override
    public Document getFirstPage() throws IOException {
        Response resp = Http.url(url).response();
        cookies = resp.cookies();
        return Http.url(url).get();
    }

    @Override
    public Document getNextPage(Document doc) throws IOException {
        // Find next page
        String nextUrl = "";
        Element elem = doc.select("a.pg_next").first();
            nextUrl = elem.attr("href");
            if (nextUrl.equals("") || nextUrl.equals("#")) {
                throw new IOException("No more pages");
            }
            return Http.url(nextUrl).get();
        }
}
