package com.theironyard.novauc;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;

import java.util.HashMap;

public class Main {

    public static HashMap<String, User> globalGps = new HashMap<>();

    public static void main(String[] args) {

        Spark.init();

        Spark.get("/", (((request, response) -> {
            Session saison = request.session();
            String nombre = saison.attribute("userName");
            User user = globalGps.get(nombre);

            HashMap localGps = new HashMap();
            if (user == null) {
                return new ModelAndView(localGps, "index.html");
            }
            else {
                return new ModelAndView(user, "messages.html");
            }

                }))
        );

        //Spark.post("/create-user");
        //Spark.post("/create-message");
        //Spark.post("/delete-message");
        //Spark.post("/edit-message");

    }
}
