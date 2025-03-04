package com.mycompany.movie.CLI;



import com.mycompany.movie.Book.Customer;
import com.mycompany.movie.Hall.Hall;
import com.mycompany.movie.Movies.Movie;
import com.mycompany.movie.Movies.ScreenTime;
import com.mycompany.movie.Movies.MovieLibrary;
import com.mycompany.movie.Movies.enGenre;

import java.lang.String;
import java.text.ParseException;
import java.util.Scanner;
import java.util.Date;
import java.util.ArrayList;

public class Manager {
    //!TODO
    //todo: Add Packages
    //todo: Move class Receipt to class Order (Composition)
    //todo: Move class seats to class hall (Composition)
    //todo: Make sure not including all the library for one class
    //todo: Remove confirmMsg() from Order (Check it here)
    //todo: Make getPrice() In Order public (In Case Confirm Message here not in Order)

    static public ArrayList<Hall> halls = new ArrayList<Hall>();


    public Manager (Scanner scanObj) throws ParseException {
        initializeLibrary();
        start(scanObj);
    }

    public static void initializeLibrary () {

    }


    public void start (Scanner scanObj) throws ParseException {
        System.out.print("Enter your name: ");
        String name = scanObj.next();

        java.lang.System.out.print("Enter your age: ");
        short age = scanObj.nextShort();

        var customer = new Customer(name, age);

        java.lang.System.out.println("Welcome " + customer.getName());

        Menu.mainMenu(scanObj, customer);
    }
}
