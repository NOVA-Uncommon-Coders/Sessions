package com.theironyard.novauc;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.HashMap;

public class Main {

    public static HashMap<String, User> userAccess = new HashMap<>();

    public static void main(String[] args) {

        Spark.init();

        Spark.get("/", ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("userName");
                    HashMap hashLocal = new HashMap();


                    System.out.println("fresh page");
                    if(!userAccess.containsKey(name)) {
                        return new ModelAndView(hashLocal, "index.html");
                    } else {
                        hashLocal.put("aVector", userAccess.get(name).getAvector());
                        return new ModelAndView(hashLocal, "messages.html");
                    }
                }),
                new MustacheTemplateEngine()
        );

        Spark.post("/create-user", ((request, response) -> {
                    System.out.println("accessed create user");
                    String nomDeGuerre = request.queryParams("createUser");
                    String password = request.queryParams("createPassword");

                    Session session = request.session();



                    if (userAccess.containsKey(nomDeGuerre)) {
                        if (userAccess.get(nomDeGuerre).getPassword().equals(password)) {
                            System.out.println("password good, redirecting");
                            session.attribute("userName", nomDeGuerre);
                            //response.redirect("/");
                        }
                    }else{
                        session.attribute("userName", nomDeGuerre);
                        userAccess.put(nomDeGuerre, new User(nomDeGuerre, password));
                    }
                    response.redirect("/");
                    return "";
                })
        );

        Spark.post("/create-message", (((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("userName");
                    System.out.println("accessed create message");
                    String handwritten = request.queryParams("createMessage");
                    userAccess.get(name).getAvector().add(handwritten);
                    response.redirect("/");
                    return "";
                }))
        );

        Spark.post("/delete-message", ((request, response) -> {
            Session session = request.session();
            String name = session.attribute("userName");
            String something = request.queryParams("deleteMessage");
            //userAccess.remove(name, handwritten);
            return "";
        }));

        Spark.post("/logout", ((request, response) -> {
            Session session = request.session();
            session.invalidate();
            response.redirect("/");
            return "";
        }));
    }
}
