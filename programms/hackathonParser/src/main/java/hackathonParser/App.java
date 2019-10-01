package hackathonParser;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import org.jsoup.Jsoup;

public class App
{
    public static void main( String[] args ) throws IOException {
        Document doc = Jsoup.connect("https://it-events.com/hackathons")
                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                .referrer("http://www.google.com")
                .get();

        Elements[] elements = {
                doc.select(".event-list-item__image"), //First only pictures
                doc.select(".event-list-item__title"), //Second only title
                doc.select(".event-list-item__type"),  //costs
                doc.select(".event-list-item__info"),  //info
        };

        Parser parser = new Parser();
        GUI gui = new GUI(parser.parseData(elements), parser.parseUrl(elements[1]));
        gui.getjFrame().setVisible(true);
    }
}
