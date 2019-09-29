package hackathonParser;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Jsoup;

public class App
{
    public static void main( String[] args ) throws IOException {
        Document doc = Jsoup.connect("https://it-events.com/hackathons")
                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                .referrer("http://www.google.com")
                .get();

        ArrayList<String> events = new ArrayList<>();
        Elements names = doc.select(".event-list-item__title");
        Elements costs = doc.select(".event-list-item__type");
        Elements info = doc.select(".event-list-item__info");

        for (int i = 0, countInfo = 0; i < names.size(); i++) {
            StringBuilder stringBuilder = new StringBuilder();

            for (int j = countInfo; j < info.size(); j++) {
                /*
                If next element in info it's date - break.
                Next iteration begin in that place.
                */
                if (j > countInfo) {
                    if (isNumeric(info.get(j).text().charAt(0))) {
                        countInfo = j;
                        break;
                    }
                }
                stringBuilder.append(info.get(j).text() + ", ");
            }
            events.add(String.format("%s,%s, %s ",
                    names.get(i).text(),
                    costs.get(i).text().split("/")[1],
                    stringBuilder.toString().trim().substring(0,stringBuilder.toString().length()-2)));
        }

        GUI gui = new GUI(events);
        gui.setVisible(true);
    }

    private static boolean isNumeric(char charAt) {
        try {
            double d = Double.parseDouble(String.valueOf(charAt));
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
