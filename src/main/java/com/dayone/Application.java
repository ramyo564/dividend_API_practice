package com.dayone;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

//@SpringBootApplication
public class Application {
    public static void main(String[] args) throws IOException {
//        SpringApplication.run(Application.class, args);

        Connection connection = Jsoup.connect(
                "https://finance.yahoo.com/quote/COKE/history?period1=99100800&period2=1707264000&interval=1mo&filter=history&frequency=1mo&includeAdjustedClose=true");
        Document document = connection.get();
        Elements elementsByAttributeValue =
                document.getElementsByAttributeValue("data-test", "historical-prices");
        Element ele = elementsByAttributeValue.get(0);

        Element tbody = ele.children().get(1);
        for (Element e : tbody.children()){
            String txt = e.text();
            if (!txt.endsWith("Dividend")){
                continue;
            }
            String[] splits = txt.split(" ");
            String month = splits[0];
            int day = Integer.valueOf(splits[1].replace(",", ""));
            int year = Integer.valueOf(splits[2]);
            String dividend = splits[3];

            System.out.println(year + "/" + month + "/" + day +" -> " + dividend);
        }
    }
}
