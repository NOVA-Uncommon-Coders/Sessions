package com.theironyard.novauc;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;

public class Main {
    static User user;

    //I'll ask mike later if this is a hashmap only meant for holding sessions

    static HashMap<String,User> users = new HashMap<>();

    public static void main(String[] args) {
        Spark.staticFileLocation("/templates");
        Spark.init();

        /**
         * index and message .html
         * try to get some css in the code today*/

        //boolean sessionLogged = true;

        Spark.get("/",
                ((request, response) -> {

                    HashMap m = new HashMap();

                    Session session = request.session();
                    String name = session.attribute("userName");
                    User user = users.get(name);

                    //the first page is ready to recieve and redirect to the correct location if you are not
                    //currently logged in you will be thrown into the login menu
                    if (user == null) {
                        return new ModelAndView(m, "login.html");
                    }
                    //if you are logged in or have a previous session open you will get to this path
                    //to be redirected to the messages page
                    else {
                        return new ModelAndView(user, "messages.html");
                    }
                }),
                //this fills the messages page with your specific message history
                new MustacheTemplateEngine()
        );

        Spark.post("/login",
                ((request, response) -> {
                    String loginName = request.queryParams("loginName");
                    String password = request.queryParams("password");
//                    boolean checkValid = users.containsKey(loginName);

//                    Session session = request.session();
//                    String name = session.attribute("userName");
//                    User user = users.get(name);
//                    //this catches an empty username box to prompt them into the create menu
//                    //the next few blocks are ment as catchers for the incorrect login scenerio
//                    if (user==null) {
//                        response.redirect("/login");
//                    }
                    if (user.getUserName().equalsIgnoreCase(loginName) && !user.getPassword().equals(password)) {
                        response.redirect("/wrong-pass");
                        return"";
                    }
                    if (!user.getUserName().equalsIgnoreCase(loginName)) {
                        response.redirect("/wrong-pass");
                        return "";
                    }
                    if (user.getUserName().equalsIgnoreCase(loginName) && user.getPassword().equals(password)) {
                        //this is the block to start a session//place the attribute into the hashmap later on
                        Session session = request.session();
                        session.attribute("userName", loginName);
                        response.redirect("/");
                        return "";
                    }
                    //AGAIN this I believe starts the current session
                    Session session = request.session();
                    session.attribute("userName", loginName);
                    response.redirect("/");
                    return "";
                })
        );

        Spark.post("/create-user",
                ((request, response) -> {
                    String username = request.queryParams("createName");
                    String password = request.queryParams("password");
                    users.put(username,new User(username,password));
                    response.redirect("/");
                    return "";

                    /**this is the block for checking session is active or not*/
//                    Session session=request.session();
//                    String name = session.attribute("userName");
//                    User user = users.get(name);
                })
        );
        Spark.post("/wrong-pass",
                ((request, response) -> {
                    String username = request.queryParams("username");
                    String password = request.queryParams("password");

                    if (user.getUserName().equalsIgnoreCase(username) && user.getPassword().equals(password)) {

                        Session session = request.session();
                        session.attribute("userName", username);
                        response.redirect("/");
                        return "";
                    }
                    response.redirect("/wrong-pass");
                    return "";
                })

        );
        Spark.post("/create-message",
                ((request, response) -> {
                    Session session=request.session();
                    String name = session.attribute("userName");
                    User user = users.get(name);
                    if(user == null){
                    throw new Exception("User is not logged in");
                    }
                    String post = request.queryParams("blogPost");
                    Date currentTime=new Date();
                    long timeOfPost = ((Integer)LocalTime.now().getMinute()) - session.creationTime();
                    String createdDate = Long.toString(timeOfPost);
                    Messages message = new Messages(post,user.getUserName(),createdDate);
                    user.getPosts().add(message);
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

