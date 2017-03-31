package com.theironyard.novauc;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    static HashMap<String, User> usersList = new HashMap<>(); //when the instructions asked for a String, did it mean it wanted "asdf" or actually the String type?

    public static void main(String[] args) {

        Spark.init();

        Spark.get("/",
                ((request, response) -> {
                    HashMap log = new HashMap<>();
                    return new ModelAndView(log, "login.html");
                }
                ), new MustacheTemplateEngine()
        );

        Spark.get("/messages",
                ((request, response) -> {

                    Session session = request.session();
                    String name = session.attribute("user");
                    System.out.println(name);
                    User user = usersList.get(name);

                    HashMap log = new HashMap<>();
                    log.put("name", user.name);
                    log.put("password", user.password);
                    log.put("message", user.messageList);
                    return new ModelAndView(log, "messages.html");
                }), new MustacheTemplateEngine()
        );


        Spark.post("/login", ((request, response) -> {
                    //I want this route to handle:
                    //query the user for their credentials
                    //if a valid user and password are entered correctly, create a session then reroute them to messages.html.
                    //if they are a returning
                    String name = request.queryParams("user");
                    String password = request.queryParams("password");
                    User user = usersList.get(name);
                    if (password.equals(user.password)) {
                        Session session = request.session();
                        session.attribute("user", name);
                        response.redirect("/home");
                    }
                    return null;
                })
        );

        Spark.post("/create-user",
                ((request, response) -> {
                    String name = request.queryParams("user");
                    String password = request.queryParams("password");
                    User user = new User(name, password, new ArrayList<Message>());
                    usersList.put(name, user);
                    Session session = request.session();
                    session.attribute("user", name);
                    response.redirect("/messages");
                    return "";

                }
                )
        );

        Spark.post("/create-message",
                ((request, response) -> {
                    //get session out of request
                    Session session = request.session();
                    //get 'user' attribute from session that we stored earlier (in either /login or /create-user)
                    String name = session.attribute("user");
                    //get user object from the userslist hashmap using 'name' as the key
                    User user = usersList.get(name);


                    //get the text of our message from the request parameters
                    String message = request.queryParams("message");
                    //use the text of our message to create a new message object
                    Message m = new Message(message);
                    //store our message in the user object's messagelist
                    user.messageList.add(m);


                    //send user back to /messages
                    response.redirect("/messages");
                    return "";

                })

        );

        Spark.post("/logout",
                ((request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";


                })
        );

        Spark.post("/delete", ((request, response) -> {

                    Session session = request.session();
                    String name = session.attribute("user");
                    User user = usersList.get(name);

                    String m = request.queryParams("delete");
                    int i = Integer.parseInt(m) - 1;   //i converted a string to an integer here so i could delete the message
                    user.messageList.remove(i);
                    //send user back to /messages
                    response.redirect("/messages");
                    return "";


                })
        );

    }
}