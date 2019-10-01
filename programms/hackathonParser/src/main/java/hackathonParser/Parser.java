package hackathonParser;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    public ArrayList<String> parseData (Elements[] elements) {
        ArrayList<String> data = new ArrayList<>();
        StringBuilder stringBuilder;

        for (int i = 0, countInfo = 0; i < elements[1].size(); i++) {
            data.add(parseLogoUrl(elements[0].get(i)));
            data.add(String.format("%s", elements[1].get(i).text()));

            if (elements[2].get(i).text().split("/")[1].contains("Бесплатное")) {
                data.add("Мероприятие бесплатное");
            } else {
                data.add(String.format("Цена %s", elements[2].get(i).text()));
            }

            stringBuilder = new StringBuilder();
            for (int j = countInfo; j < elements[3].size(); j++) {
                /*
                If next element in info it's date - break.
                Next iteration begin in that place.
                */
                if (j > countInfo) {
                    if (isNumeric(elements[3].get(j).text().charAt(0))) {
                        countInfo = j;
                        break;
                    }
                }
                stringBuilder.append(String.format("%s, ",elements[3].get(j).text()));
            }
            data.add(!stringBuilder.toString().equals("") ? stringBuilder.toString().substring(0, stringBuilder.length()-2) : "");
            data.add("");
        }
        return data;
    }

    public ArrayList<String> parseUrl(Elements element) {
        ArrayList<String> dataUrl = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?<=href=\").+(?=\")");
        Matcher matcher = pattern.matcher(element.toString());

        while (matcher.find()) {
            dataUrl.add("https://it-events.com" + element.toString().substring(matcher.start(), matcher.end()));
        }
        dataUrl.forEach(System.out::println);
        return dataUrl;
    }

    public String parseLogoUrl(Element element) {
        Pattern pattern = Pattern.compile("(?<=background-image: url\\().+(?=\\))");
        Matcher matcher = pattern.matcher(element.toString());
        String url = null;
        while (matcher.find()) {
            url = "https://it-events.com" + element.toString().substring(matcher.start(), matcher.end());
        }
        return url;
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
