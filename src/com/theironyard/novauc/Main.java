package com.theironyard.novauc;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    static HashMap<String, User> usersList = new HashMap<>(); //when the instructions asked for a String, did it mean it wanted "asdf" or actually the String type?

    public static void main(String[] args) throws Exception {

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
                    HashMap log = new HashMap<>();
                    Session session = request.session();
                    User user = session.attribute("user");

                    if(user == null){
                        response.redirect("/");
                    }else {
                        log.put("name", user.name);
                        log.put("password", user.password);
                        log.put("message", user.messageList);
                    }


                    return new ModelAndView(log, "messages.html");
                }), new MustacheTemplateEngine()
        );

//gettinga 500 error when I log in.  I get a null pointer exception (does that mean i have to put "throws exception somewhere?)
        Spark.post("/login", ((request, response) -> {
            String name = request.queryParams("user");
            String password = request.queryParams("password");
            User user = usersList.get(name);
            if (user == null) {
                user = new User(name, password, new ArrayList<Message>()); //created a new user
                usersList.put(name, user); // do i need to put password here too?
                Session session = request.session();
                session.attribute("user", user);
                response.redirect("/messages");
            }else if (password.equals(user.password)) {
                Session session = request.session();
                session.attribute("user", user);
                response.redirect("/messages");
            }
            else {
                response.redirect("/");
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