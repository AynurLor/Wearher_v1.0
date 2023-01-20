package ru.school21;

import java.io.IOException;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome!, this is the weather forecast.\n" +
                            "Please enter your city");
        String city = sc.nextLine();
        Parser pogoda = new Parser(city);
        pogoda.currentWeather();
        pogoda.printWeather();
    }
}
