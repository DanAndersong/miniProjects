package hackathonParser;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;


public class App
{
    public static void main( String[] args ) throws IOException {
        Document doc = Jsoup.connect("https://it-events.com/hackathons")
                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                .referrer("http://www.google.com")
                .get();

        Map<String, Elements> elements = new HashMap<>();
        elements.put("logos",  doc.select(".event-list-item__image"));
        elements.put("titles", doc.select(".event-list-item__title"));
        elements.put("prices", doc.select(".event-list-item__type" ));
        elements.put("other",   doc.select(".event-list-item__info" ));
        GUI gui = new GUI(new Parser().getData(elements));
        gui.getjFrame().setVisible(true);
    }
}
