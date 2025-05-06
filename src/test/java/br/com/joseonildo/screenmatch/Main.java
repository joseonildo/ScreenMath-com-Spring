package br.com.joseonildo.screenmatch;

import java.time.*;
import java.time.format.*;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        // create a formatter with Locale.ENGLISH
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM uuuu", Locale.ENGLISH);

        // create a LocalDate object
        LocalDate lt = LocalDate.parse("31 Dec 2018", formatter);

        // print result
        System.out.println("LocalDate : " + lt.toString());
    }
}