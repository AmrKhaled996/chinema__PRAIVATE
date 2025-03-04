package com.mycompany.movie;
import java.util.Scanner;
import com.mycompany.movie.CLI.Manager;
import com.mycompany.movie.Movies.MovieLibrary;
import java.text.ParseException;
public class Main {
    public static void main (String[] args) throws ParseException {
        Scanner scanObj = new Scanner(System.in);
        MovieLibrary lib=new MovieLibrary(Database.listMoviesWithScreenTimes());
        for (var movie: MovieLibrary.getMovies()) {
        System.out.println(movie.getID()+"  "+movie.getTitle());
            
        }
        Manager cli = new Manager(scanObj);
        cli.start(scanObj);

    }
}
