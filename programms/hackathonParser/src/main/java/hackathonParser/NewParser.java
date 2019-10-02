package hackathonParser;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewParser {
    int count = 0;

    public HashMap<Integer, HashMap> getData (Map<String, Elements> elements) {
        Map<Integer, HashMap> data = new HashMap<>();

        for (int i = 0; i < elements.get("titles").size(); i++) {
            Map<String, String> dataEvent = new HashMap<>();

            for (Map.Entry<String, Elements> pair : elements.entrySet()) {
                String key = pair.getKey();
                Elements value = pair.getValue();

                if (key.equals("logos")) {
                    dataEvent.put("logo", parseLogoUrl(value.get(count)));
                }
                if (key.equals("titles")) {
                    dataEvent.put("title", value.get(count).text());
                }
                if (key.equals("costs")) {

                }
            }
        }

        return null;
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

}
