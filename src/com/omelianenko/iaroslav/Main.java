package com.omelianenko.iaroslav;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Converter converter = new Converter();

        List<String> datesList1 = new ArrayList<>();
        datesList1.add("2022-01-25T08:00:00");
        datesList1.add("2022-01-25T08:00:00");
        datesList1.add("2022-01-25T09:00:00");
        datesList1.add("2022-01-25T09:30:00");
        datesList1.add("2022-01-26T08:00:00");
        datesList1.add("2022-01-26T08:30:00");
        datesList1.add("2022-01-26T09:00:00");
        datesList1.add("2022-01-26T09:30:00");

        List<String> datesList2 = new ArrayList<>();
        datesList2.add("2022-01-24T19:53:00");
        datesList2.add("2022-01-24T19:54:00");
        datesList2.add("2022-01-24T19:55:00");
        datesList2.add("2022-01-24T19:56:00");
        datesList2.add("2022-01-24T19:57:00");
        datesList2.add("2022-01-24T19:58:00");
        datesList2.add("2022-01-24T19:59:00");
        datesList2.add("2022-01-24T20:00:00");
        datesList2.add("2022-01-24T20:01:00");
        datesList2.add("2022-01-24T20:02:00");

        try {
            System.out.println(converter.convert(datesList2));
        } catch (DatesToCronConvertException e) {
            e.printStackTrace();
        }

        System.out.println(converter.getImplementationInfo());
    }
}


