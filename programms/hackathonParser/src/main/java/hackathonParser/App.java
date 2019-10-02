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
        elements.put("logos",doc.select(".event-list-item__image"));
        elements.put("titles",doc.select(".event-list-item__title"));
        elements.put("costs",doc.select(".event-list-item__type"));
        elements.put("info",doc.select(".event-list-item__info"));

//        Elements[] elements = {
//                doc.select(".event-list-item__image"), //First only pictures
//                doc.select(".event-list-item__title"), //Second only title
//                doc.select(".event-list-item__type"),  //costs
//                doc.select(".event-list-item__info"),  //info
//        };

        NewParser parser = new NewParser();
        GUI gui = new GUI(parser.getData(elements));
        gui.getjFrame().setVisible(true);
    }
}
