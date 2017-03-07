package com.novauc;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    static HashMap<String, ArrayList<xxMessagesxx>> mess = new HashMap<>();
    static HashMap m = new HashMap();
    static User user;
    static xxMessagesxx y;

    public static void main(String[] args) {

        Spark.init();

        Spark.get("/", ((request, response) -> {

            Session session = request.session();
            String name = session.attribute("userName");
            User user = m.get(name);

            HashMap m = new HashMap<>();
            if (user == null) {
                return new ModelAndView(m, "index.html"); //might need to create a index.html
            } else {
                return new ModelAndView(user, "messages.html");
            }
        }), new MustacheTemplateEngine());


        Spark.post("/createUser", ((request, response) -> {
                    String name = request.queryParams("createUser");
                    String password = request.queryParams("password");
                    String ymessage = request.queryParams("ymessage");
                    user = new User(name, password, ymessage);

            if (user == null) {
                user = new User(name,password, ymessage);
                m.put(name, user);

                m.put("name", user.name);
                m.put("name", user.password);
                m.put("name", user.ymessage);
            }


            Session session = request.session();
            session.attribute("userName", name);

                    response.redirect("/");
                    return "";

                })
        );

        Spark.post("/createMessage", ((request, response) -> {
                    String ymessage = request.queryParams("createMessage");
                    y = new xxMessagesxx(ymessage);
                    ArrayList<xxMessagesxx> a = new ArrayList<>();
                    a.add(y);
                    mess.put(user.name, a);
                    int x = 2;

                    response.redirect("/");

                    return "";

                })
        );
    }
}







