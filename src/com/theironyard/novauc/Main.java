package com.theironyard.novauc;

import spark.*;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

import static sun.plugin2.util.PluginTrace.init;

public class Main {
    static HashMap<String, User> users = new HashMap<>();


    public static void main(String[] args) {

        Spark.init();

        Spark.get(
                "/",
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("userName");
                    User user = users.get(name);



                    HashMap m = new HashMap<>();
                    if (user == null ) {
                        return new ModelAndView(m, "index.html");
                    } else {
                        m.put("name",user.name);
                        m.put("message",user.messages);
                        return new ModelAndView(user, "home.html");
                    }
                }),
                new MustacheTemplateEngine()
        );
        Spark.post(
                "/login",
                ((request, response) -> {
                    String name = request.queryParams("loginName");
                    String password =request.queryParams("passWord");
                    User user = users.get(name);

                    if (user == null) {
                        user = new User(name,password);
                        users.put(name, user);

                    }
                    if(password.equalsIgnoreCase(user.password)){

                    }
                    Session session = request.session();
                    session.attribute("userName", name);



                    response.redirect("/");
                    return "";
                })
        );

        Spark.post("/create-message", ((request, response) -> {
            Session session = request.session();
            String name = session.attribute("userName");
            User user = users.get(name);
            String password = session.attribute("passWord");
            User user1 = users.get(password);
            if (user == null && user1 == null) {
                        throw new Exception("User is not logged in");
            }
                String messageText = request.queryParams("messageText");

            Message message = new Message(messageText);
            user.messages.add(message);
            response.redirect("/");
            return "";


        })


        );

        Spark.post("/delete",((request, response) ->{
            Session session = request.session();
            String name = session.attribute("userName");
            User user = users.get(name);
            String deleteMessage = request.queryParams("deleteMessage");
            int b = Integer.parseInt(deleteMessage);
            user.messages.remove(b -1);
            response.redirect("/");
            return "";

                })
        );



        Spark.post("/edit-message",((request, response) ->{
            Session session = request.session();
            String name = session.attribute("userName");
            User user = users.get(name);
            String updateMessage = request.queryParams("updateMessage");
            int b = Integer.parseInt(updateMessage);
            user.messages.get(b -1);
            user.messages.remove(b -1);

            String editMessage = request.queryParams("editMessage");
            Message message = new Message(editMessage);
            user.messages.add(message);
            response.redirect("/");
            return "";


                })

        );


        Spark.post(
                "/logout",
                ((request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                })
        );
        }

    }
