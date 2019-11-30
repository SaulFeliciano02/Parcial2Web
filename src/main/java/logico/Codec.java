package logico;

import services.UrlServices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Codec {

    private Long counter;
    private Map<Long, String> indexToUrl;
    private Map<String, Long> urlToIndex;
    private String base62;

    public Codec() {
        counter = 1L;
        indexToUrl = new HashMap<>();
        urlToIndex = new HashMap<>();
        base62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    }

    // Encodes a URL to a shortened URL.
    public String encode(String longUrl) {
        System.out.println("El valor de longUrl es: " + longUrl);
        if (urlToIndex.containsKey(longUrl)) {
            System.out.println("http://tinyurl.com/"+base62Encode(urlToIndex.get(longUrl)));
            return "http://tinyurl.com/"+base62Encode(urlToIndex.get(longUrl));
        }
        else {
            indexToUrl.put(Long.parseLong(Integer.toString(Controladora.getInstance().getMisUrls().size()+1)), longUrl);
            List<Url> allUrls = new UrlServices().getAllUrls();
            long id = Long.parseLong(allUrls.get(allUrls.size()-1).getUrlIndexada()) + 1;
            urlToIndex.put(longUrl, id);

            Url url = new Url(longUrl);
            url.setUrlIndexada("/shorty.com/"+urlToIndex.get(longUrl));
            System.out.println("El valor de longUrl en este punto es: " + urlToIndex.get(longUrl));
            url.setUrlBase62("/shorty.com/"+base62Encode(urlToIndex.get(longUrl)));
            Controladora.getInstance().getMisUrls().add(url);
            String url2 = base62Encode(urlToIndex.get(longUrl));
            System.out.println(urlToIndex.get(longUrl));
            System.out.println("/shorty.com/"+base62Encode(urlToIndex.get(longUrl)));
            return url2;
        }
    }

    // Decodes a shortened URL to its original URL.
    public String decode(String shortUrl) {
        String base62Encoded = shortUrl.substring(shortUrl.lastIndexOf("/") + 1);
        long decode = 0;
        for(int i = 0; i < base62Encoded.length(); i++) {
            decode = decode * 62 + base62.indexOf("" + base62Encoded.charAt(i));
        }
        return indexToUrl.get(decode);
    }

    private String base62Encode(long value) {
        StringBuilder sb = new StringBuilder();
        while (value != 0) {
            sb.append(base62.charAt((int)(value % 62)));
            value /= 62;
        }
        return sb.reverse().toString();
    }
}
