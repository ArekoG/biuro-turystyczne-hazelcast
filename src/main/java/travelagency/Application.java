package travelagency;

import travelagency.menu.Menu;

import java.io.IOException;
import java.text.ParseException;

public class Application {
    public static void main(String[] args) throws ParseException, IOException {

        Menu menu = new Menu();
        menu.start();
    }
}
