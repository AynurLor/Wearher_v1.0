package ru.school21;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    private static String city_;
    private static String current_date_;
    private static Document page_;
    private static final Queue<String> real_temp_ = new ArrayDeque<>();
    private static final Queue<String> feel_temp_ = new ArrayDeque<>();
    private static final Queue<String> probability_ = new ArrayDeque<>();
    private static final Queue<String> pressure_ = new ArrayDeque<>();
    private static final Queue<String> wind_ = new ArrayDeque<>();
    private static final Queue<String> humidity_ = new ArrayDeque<>();
    private static final Pattern pattern_temp = Pattern.compile("\\d+");

    public Parser(String city) {
        assert city.isEmpty();
        city_ = city.toLowerCase();
    }

    public static void parsePage() throws IOException {
        String url = "https://world-weather.ru/pogoda/russia/" + city_ + "/";
        page_ = Jsoup.parse(new URL(url), 3000);
    }

    private static String getDateFromString(String line) throws Exception {
        Matcher matcher = pattern_temp.matcher(line);
        if (matcher.find()) {
            return matcher.group();
        }
        throw new Exception("Can't extract date from string!");
    }

    public static void currentWeather() throws Exception {

        parsePage();
        if (page_.toString().isEmpty()) {
          throw new Exception("Page is empty!");
        }
        weatherData();

    }

    private static void weatherData() throws Exception{
        Element table_weather = page_.select("table[class=weather-today]").get(1);
        Elements real_temp = table_weather.select("td[class=weather-temperature]");
        Elements feel_temp = table_weather.select("td[class=weather-feeling]");;
        Elements probability = table_weather.select("td[class=weather-probability]");
        Elements pressure = table_weather.select("td[class=weather-pressure]");
        Elements wind = table_weather.select("td[class=weather-wind]");
        Elements humidity = table_weather.select("td[class=weather-humidity]");
        Element current_date = page_.select("div[class=numbers-month]").first();
        for (Integer item : Arrays.asList(0,1,2,3)) {
            real_temp_.add(real_temp.get(item).text().split("°")[0]);
            feel_temp_.add(feel_temp.get(item).text().split("°")[0]);
            probability_.add(probability.get(item).text().split("%")[0]);
            wind_.add(getDateFromString(wind.get(item).text()));
            pressure_.add(getDateFromString(pressure.get(item).text()));
            humidity_.add(humidity.get(item).text().split("%")[0]);
        }
        assert current_date != null;
        current_date_ = getDateFromString(current_date.text());
    }

    private static void checkData() throws Exception {
        if (real_temp_.isEmpty() || feel_temp_.isEmpty() ||
            probability_.isEmpty() || wind_.isEmpty() ||
            pressure_.isEmpty() || humidity_.isEmpty()) {
            throw new Exception("Data for weather is empty!");
        }
    }

    public static void printWeather() throws Exception {
        ArrayList<String> periods = new ArrayList<>(Arrays.asList("Night", "Morning",
                                                                  "Day", "Evening"));
        String left_align_format = "| %-7s | %-15s | %-13s | %-29s | %-18s | %-14s | %-12s |%n";
        checkData();

        System.out.format("+---------+-----------------+%n");
        System.out.format("| %-7s | %-15s |%n", city_, "Date = " + current_date_);
        System.out.format("+---------+-----------------+---------------+-------------------------------+--------------------+----------------+--------------+%n");
        System.out.format("| Period  | Temperature ^C  | Feels like ^C | Probability  of precipitation | Pressure mm of mr. | Wind speed m/s | Air humidity |%n");
        System.out.format("+---------+-----------------+---------------+-------------------------------+--------------------+----------------+--------------+%n");

        for (String period : periods) {
            System.out.format(left_align_format, period, real_temp_.poll().toString(),
                    feel_temp_.poll().toString(), probability_.poll().toString(),
                    pressure_.poll().toString(), wind_.poll().toString(),
                    humidity_.poll()).toString();
        }
        System.out.format("+---------+-----------------+---------------+-------------------------------+--------------------+----------------+--------------+%n");


    }
}
