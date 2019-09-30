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

        Elements names = doc.select(".event-list-item__title");
        Elements costs = doc.select(".event-list-item__type");
        Elements info = doc.select(".event-list-item__info");

        GUI gui = new GUI(parse(names, costs, info));
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

    private static ArrayList<String> parse (Elements names, Elements costs, Elements info) {
        ArrayList<String> events = new ArrayList<>();
        StringBuilder stringBuilder;

        for (int i = 0, countInfo = 0; i < names.size(); i++) {
            events.add(String.format("%s", names.get(i).text()));

            if (costs.get(i).text().split("/")[1].contains("Бесплатное")) {
                events.add("Мероприятие бесплатное");
            } else {
                events.add(String.format("Цена %s", costs.get(i).text()));
            }

            stringBuilder = new StringBuilder();
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
                stringBuilder.append(String.format("%s, ",info.get(j).text()));
            }
            events.add(!stringBuilder.toString().equals("") ? stringBuilder.toString().substring(0, stringBuilder.length()-2) : "");
        }
        return events;
    }
}
