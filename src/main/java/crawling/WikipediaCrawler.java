package crawling;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

public class WikipediaCrawler {
    private static final String RANDOM_FEATURED_ARTICLE_URL = "http://tools.wmflabs.org/magnustools/randomarticle.php?lang=en&project=wikipedia&categories=Featured+articles&d=0";
    private static final String WIKIPEDIA_API_URL = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles=";

    public static String getHTML() throws Exception {
        final URL randomArticleUrl = new URL(RANDOM_FEATURED_ARTICLE_URL);
        final HttpURLConnection conn = (HttpURLConnection) randomArticleUrl.openConnection();
        conn.setRequestMethod("GET");
        final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        final String redirect = rd.readLine();
        if (redirect == null) {
            System.err.println("Could not load random featured article");
            return "";
        }
        final String featuredArticleUrlString = redirect.substring(redirect.indexOf("url="),
                redirect.lastIndexOf("'"));
        final String featuredArticleTitle = featuredArticleUrlString.substring(
                featuredArticleUrlString.lastIndexOf("/") + 1);
        //System.out.println(featuredArticleTitle);

        final StringBuilder result = new StringBuilder();
        final URL featuredArticleUrl = new URL(WIKIPEDIA_API_URL + featuredArticleTitle);
        final HttpURLConnection conn2 = (HttpURLConnection) featuredArticleUrl.openConnection();
        conn2.setRequestMethod("GET");
        final BufferedReader rd2 = new BufferedReader(new InputStreamReader(conn2.getInputStream()));
        String line = rd2.readLine();
        while (line != null) {
            result.append(line);
            line = rd2.readLine();
        }
        rd2.close();

        return result.toString();
    }

    public static String getTopicSentence() throws Exception {
        final JsonParser parser = new JsonParser();
        final String content = getHTML();
        final JsonObject json = parser.parse(content).getAsJsonObject();
        //System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(content)));

        final Set<Entry<String, JsonElement>> pages = json.getAsJsonObject("query").getAsJsonObject("pages").entrySet();
        final String introduction = pages.iterator().next().getValue().getAsJsonObject().get("extract").getAsString();
        final int indexOfParagraph = introduction.indexOf("\n");
        final String firstParagraph;
        if (indexOfParagraph != -1) {
            firstParagraph = introduction.substring(0, indexOfParagraph);
        } else {
            firstParagraph = introduction;
        }
        return firstParagraph;
    }

    public static void main(String[] args) throws Exception {
        final Set<String> topicSentences = new HashSet<>();
        while (topicSentences.size() < 100) {
            final String topicSentence = getTopicSentence();
            final boolean newTopicSentence = topicSentences.add(topicSentence);
            if (newTopicSentence) {
                System.out.println(topicSentence);
            }
        }
    }
}
