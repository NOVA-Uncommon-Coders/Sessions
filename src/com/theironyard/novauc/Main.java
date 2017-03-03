package com.theironyard.novauc;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    static HashMap<String, User> users = new HashMap<>();
    static ArrayList<Form> messages = new ArrayList<>();

    public static void main(String[] args) {
        Spark.init();



        Spark.get("/", ((request, response) -> {
                    HashMap m = new HashMap();
                    Session session = request.session();
                    String name = session.attribute("userName");
                    User user = users.get(name);



                    if (user == null) {
                        return new ModelAndView(m, "index.html");
                    } else {
                        m.put("name", user.name);
                        return new ModelAndView(m, "home.html");
                    }
                }),
                new MustacheTemplateEngine()
        );


        Spark.post(
                "/login",
                ((request, response) -> {
                    String name = request.queryParams("loginName");
                    User user = users.get(name);
                    if (user == null) {
                        user = new User(name);
                        users.put(name, user);
                    }

                    Session session = request.session();
                    session.attribute("userName", name);

                    response.redirect("/");
                    return "";
                })
        );



        Spark.post(
                "/create-message",
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("userName");
                    User user = users.get(name);
                    if (user == null) {
                        throw new Exception("User is not logged in");
                    }

                    String formMessage = request.queryParams("formMessage");
                    String formDelete = request.queryParams("formDelete");
                    String formEdit = request.queryParams("formEdit");


                    Form form = new Form(formMessage,formDelete,formEdit);

                    user.forms.add(form);

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

