package hackathonParser;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    public Map<Integer, Map> getData (Map<String, Elements> elements) {
        Map<Integer, Map> data = new HashMap<>();
        String patternLogo = "(?<=background-image: url\\().+(?=\\))";
        String patternAbout = "(?<=href=\").+(?=\"\\s)";

        int otherCount = 0;
        for (int i = 0; i < elements.get("titles").size(); i++) {
            Map<String, String> dataEvent = new HashMap<>();

            for (Map.Entry<String, Elements> pair : elements.entrySet()) {
                String key = pair.getKey();
                Elements value = pair.getValue();
                if (key.equals("logos")) {
                    dataEvent.put("logo", parseUrl(value.get(i), patternLogo));
                    dataEvent.put("url", parseUrl(value.get(i), patternAbout));
                }
                if (key.equals("titles")) {
                    dataEvent.put("title", value.get(i).text());
                }
                if (key.equals("prices")) {
                    String cost = value.get(i).text().toLowerCase();
                    if (cost.contains("бесплатное") || cost.contains("free")) {
                        dataEvent.put("cost", "Участие бесплатно");
                    } else {
                        dataEvent.put("cost", String.format("Цена: %s", cost));
                    }
                }
                if (key.equals("other")) {
                    StringBuilder stringBuilder = new StringBuilder();

                    for (int j = otherCount; j < value.size(); j++) {
                        if (isNumeric(value.get(j).text().charAt(0)) && j > otherCount) {
                            otherCount = j;
                            break;
                        } else {
                            stringBuilder.append(String.format("%s  ", value.get(j).text()));
                        }
                    }
                    dataEvent.put("other", stringBuilder.toString());
                }
            }
            data.put(i, dataEvent);
        }
        return data;
    }

    private String parseUrl(Element element, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(element.toString());

        String url = null;
        while (m.find()) {
            url = "https://it-events.com" + element.toString().substring(m.start(), m.end());
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
