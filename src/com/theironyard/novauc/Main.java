package com.theironyard.novauc;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.HashMap;

public class Main {

    public static HashMap<String, User> globalGps = new HashMap<>();
    public static User usage;

    public static void main(String[] args) {

        Spark.init();

        Spark.get("/", ((request, response) -> {
            Session saison = request.session();
            String nombre = saison.attribute("userName");
            User user1 = globalGps.get(nombre);

            HashMap localGps = new HashMap();
            if (user1 == null) {
                return new ModelAndView(localGps, "index.html");
            }
            else {
                return new ModelAndView(user1, "messages.html");
            }
                }),
                new MustacheTemplateEngine()
        );

        Spark.post("/create-user", ((request, response) -> {
            String nom = request.queryParams("loginName");
            User user2 = globalGps.get(nom);
            if (user2 == null) {
                user2 = new User(nom, usage.getVectorCatalog());
                globalGps.put(nom, user2);
            }

            Session season = request.session();
            season.attribute("userName", nom);

            response.redirect("/");
            return "";
        })
        );

        //Spark.post("/create-message");
        //Spark.post("/delete-message");
        //Spark.post("/edit-message");

    }
}
