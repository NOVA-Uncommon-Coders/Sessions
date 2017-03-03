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
                    String nomDeGuerre = request.queryParams("createUser");
                    String password = request.queryParams("createPassword");

                    Session session = request.session();



                    if (userAccess.containsKey(nomDeGuerre)) {
                        if (userAccess.get(nomDeGuerre).getPassword().equals(password)) {
                            session.attribute("userName", nomDeGuerre);
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
                    String handwritten = request.queryParams("createMessage");
                    userAccess.get(name).getAvector().add(handwritten);
                    response.redirect("/");
                    return "";
                }))
        );

        Spark.post("/edit-message", ((request, response) -> {
            Session session = request.session();
            String name = session.attribute("userName");
            String editText = request.queryParams("editMessageT");
            String editNumber = request.queryParams("editMessageN");
            userAccess.get(name).getAvector().set((Integer.valueOf(editNumber)-1), editText );
            response.redirect("/");
            return "";
        }));

        Spark.post("/delete-message", ((request, response) -> {
            Session session = request.session();
            String name = session.attribute("userName");
            String deleteRequest = request.queryParams("deleteMessage");
            userAccess.get(name).getAvector().remove((Integer.valueOf(deleteRequest) -1));
            response.redirect("/");
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
