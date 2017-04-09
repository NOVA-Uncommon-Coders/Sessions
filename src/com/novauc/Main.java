package com.novauc;

import spark.*;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    static HashMap<String, ArrayList<xxMessagesxx>> mess = new HashMap<>();
    static User user;
    static xxMessagesxx y;

    public static void main(String[] args) {

        Spark.init();


        Spark.get("/", ((request, response) -> {

            Session session = request.session();
            String name = session.attribute("userName");
            HashMap m = new HashMap<>();

            if (user == null) {
                return new ModelAndView(m, "index.html"); //might need to create a index.html
            } else {
                m.put("name", user.name);
                m.put("m", mess.get(user.name));
                return new ModelAndView(m, "messages.html");
            }
        }), new MustacheTemplateEngine());


        Spark.post("/createUser", ((request, response) -> {
                    String name = request.queryParams("user");
                    String password = request.queryParams("password");
                    user = new User(name, password, new ArrayList<>());

                    if (user == null) {
                        user = new User(name, password, new ArrayList<>());

                    }

                    Session session = request.session();
                    session.attribute("userName", name);

                    response.redirect("/");
                    return "";

                })
        );

        Spark.post("/createMessage", ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("userName");
                    String ymessage = request.queryParams("createMessage");
                    y = new xxMessagesxx(ymessage);
                    user.messages.add(y);
                    mess.put(user.name, user.messages);

                    response.redirect("/");
                    return "";

                })
        );

        Spark.post("/delete-Message", ((Request request, Response response) -> {
            Session session = request.session();
            session.attribute("userName");
            mess.get(user.name);
            int id = Integer.valueOf(request.queryParams("ID"));
            mess.get(user.name).remove(id - 1);

            response.redirect("/");
            return "";
        }));

        Spark.post("/editMessage", ((request, response) -> {
            Session session = request.session();
            session.attribute("userName");
            mess.get(user.name);
            int id = Integer.valueOf(request.queryParams("messageID"));
            mess.get(user.name).get(id - 1).ymessage = request.queryParams("newMessage");

            response.redirect("/");
            return "";
        }));
    }
}







