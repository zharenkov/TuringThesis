package crawling;

import com.google.common.base.Charsets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import demo.SimplificationDemo;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

public class WikipediaCrawler {
    private static final String FEATURED_ARTICLE_URLS_FILENAME = "featured_article_urls.txt";
    private static final String WIKIPEDIA_API_URL = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles=";

    public static String getHTML(String url) throws Exception {
        final StringBuilder result = new StringBuilder();
        final URL featuredArticleUrl = new URL(WIKIPEDIA_API_URL + url);
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

    public static String getTopicSentence(String url) throws Exception {
        final JsonParser parser = new JsonParser();
        final String content = getHTML(url);
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
        final StringBuilder result = new StringBuilder();
        final ClassLoader classLoader = SimplificationDemo.class.getClassLoader();
        final URL resource = classLoader.getResource(FEATURED_ARTICLE_URLS_FILENAME);
        if (resource == null) {
            System.err.println("Cannot load topic sentences");
            return;
        }
        final Scanner scanner = new Scanner(new File(resource.getFile()));
        while (scanner.hasNext()) {
            final String url = scanner.nextLine();
            final String topicSentence = getTopicSentence(url);
            System.out.println(topicSentence);
            result.append(topicSentence);
            result.append("\n");
        }
        final File outputFile = new File("output/wikipedia_featured_articles/first_paragraphs.txt");
        FileUtils.writeStringToFile(outputFile, result.toString(), Charsets.UTF_8.name());
    }
}
