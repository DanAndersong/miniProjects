package hackathonParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class App
{
    public static void main( String[] args ) throws IOException {
//        GUI gui = new GUI();
        System.out.println("test");
//        Document doc = Jsoup.connect("https://it-events.com/hackathons")
//                .userAgent("Chrome/4.0.249.0 Safari/532.5")
//                .referrer("http://www.google.com")
//                .get();
//
//        List<String> events = new ArrayList<>();
//        Elements names = doc.select(".event-list-item__title");
//        Elements costs = doc.select(".event-list-item__type");
//        Elements info = doc.select(".event-list-item__info");
//
//        for (int i = 0, countInfo = 0; i < names.size(); i++) {
//            StringBuilder stringBuilder = new StringBuilder();
//
//            for (int j = countInfo; j < info.size(); j++) {
//                /*
//                If next element in info it's date - break.
//                Next iteration begin in that place.
//                */
//                if (j > countInfo) {
//                    if (isNumeric(info.get(j).text().charAt(0))) {
//                        countInfo = j;
//                        break;
//                    }
//                }
//                stringBuilder.append(info.get(j).text() + " ");
//            }
//            events.add(String.format("%s%s %s ",names.get(i).text(), costs.get(i).text().split("/")[1], stringBuilder.toString()));
//        }
//
//        events.forEach(System.out::println);
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
