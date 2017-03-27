package com.theironyard.novauc;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.HashMap;

public class Main {

    static User user;
    static HashMap<String, User> users = new HashMap<>();

    public static void main(String[] args) {

        Spark.init();

        Spark.get("/", ((request, response) ->  {
            HashMap m = new HashMap(); // m is only for the model; this is not where users are going.

            Session session = request.session();
            String userName = session.attribute("userName");
            User user = users.get(userName);

            if(user == null){
                return new ModelAndView(m, "index.html");
            } else {
                m.put("messages",users.get(userName).userMessages);
                return new ModelAndView(m, "messages.html");
            }


        }), new MustacheTemplateEngine()

        );

        Spark.post("/create-user",
                ((request, response) -> {

        String name = request.queryParams("LoginName");
        Session session = request.session();


        String password = request.queryParams("LoginPassword");

                if (!users.containsKey(name)){
                    session.attribute("userName", name);
                    users.put(name, new User(name, password));
                } else {
                    if(users.get(name).password.equals(password)){
                        session.attribute("userName", name);
                    }
                }

            response.redirect("/");
            return "Welcome!";

        })
        );

        Spark.post("/create-message", ((request, response) -> {
            Session session = request.session();
            String userName = session.attribute("userName");


            String message = request.queryParams("messageText");
            users.get(userName).userMessages.add(message);

            response.redirect("/");
            return  "";


        })
        );

        Spark.post("/delete-messages", ((request, response) -> {
            Session session = request.session();
            String userName = session.attribute("userName");

            String message = request.queryParams("deleteMessage");
            users.get(userName).userMessages.remove(message);

            response.redirect("/");
            return "";


        })
        );

        Spark.post("/edit-messages", ((request, response) -> {
            Session session = request.session();
            String userName = session.attribute("userName");
            String message = request.queryParams("newMessage");
            String old = request.queryParams("oldMessage");
            int i = users.get(userName).userMessages.indexOf(old);
            users.get(userName).userMessages.set(i, message);


            response.redirect("/");
            return "";

        })
        );


            Spark.post("/logout", ((request, response) -> {

                        Session session = request.session();
                        session.invalidate();
                        response.redirect("/");
                        return "";
                    })
            );
        }
}




