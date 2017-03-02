package com.theironyard.novauc;

import jodd.json.JsonParser;
import jodd.json.JsonSerializer;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static HashMap<String, User> userAccounts = new HashMap<>();
    public static boolean loggedIn = false;
    public static File f = new File("post.json");
    public static FileWriter fw;
    public static Scanner scanner;

    public static void main(String[] args) throws IOException {
        jasonRead();
        Spark.staticFileLocation("/styles");
        Spark.init();
        Spark.get(
                "/",
                ((request, response) -> {
                    Session session = request.session();
                    String loginName = session.attribute("userName");
                    return new ModelAndView(userAccounts,"index.html" );
                }),
                new MustacheTemplateEngine()
        );
        Spark.get(
                "/home.html",
                ((request, response) -> {
                    Session session = request.session();
                    String loginName = session.attribute("userName");
                    if (loggedIn) {
                        HashMap<String, ArrayList<String>> m= new HashMap();
                        m.put("post", userAccounts.get(loginName).getPosts());
                        return new ModelAndView(m, "home.html");
                    } else {
                        return new ModelAndView(userAccounts, "index.html");
                    }
                }),
                new MustacheTemplateEngine()
        );
        Spark.get(
                "/registration.html",
                ((request, response) -> {

                    Session session = request.session();
                    String loginName = session.attribute("userName");
                    return new ModelAndView(userAccounts, "registration.html");
                }),
                new MustacheTemplateEngine()
        );
        Spark.get(
                "/viewall.html",
                ((request, response) -> {

                    Session session = request.session();
                    String loginName = session.attribute("userName");
                    String post = "";
                    HashMap<String, String>m = new HashMap<>();
                    for(User user: userAccounts.values()){
                        post += String.format("<hr/><h4>Post by: <b>%s</b></h4>", user.getName());
                        int i = 1;
                        for(String message: user.getPosts()){
                            post+= String.format("<p>%s. %s</p>",i ,message );
                            i++;
                        }
                    }
                    m.put("post", post);
                    return new ModelAndView(m, "viewall.html");
                }),
                new MustacheTemplateEngine()
        );
        Spark.get(
                "/logout.html",
                ((request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    loggedIn = false;
                    return new ModelAndView(userAccounts, "index.html");
                }),
                new MustacheTemplateEngine()
        );
        Spark.post(
                "/login",
                ((request, response) -> {
                    Session session = request.session();
                    String name = request.queryParams("loginName");
                    String password = request.queryParams("password");
                    if (userAccounts.containsKey(name)){
                        if (userAccounts.get(name).getPassword().equals(password)){
                            loggedIn = true;
                            session.attribute("userName", name);
                            response.redirect("home.html");
                        } else {
                            response.redirect("/");
                        }
                    } else {
                        response.redirect("/");

                    }
                    return "";
                })
        );
        Spark.post(
                "/registration",
                ((request, response) -> {
                    Session session = request.session();
                    String loginName = session.attribute("userName");
                    String name = request.queryParams("registrationName");
                    String password = request.queryParams("registrationPassword");
                    if(userAccounts.containsKey(name) || name.equals("") || password.equals("")){
                        response.redirect("/");
                    } else {
                        userAccounts.put(name, new User(name, password));
                        response.redirect("/");
                    }
                    return "";
                })
        );
        Spark.post(
                "/add",
                ((request, response) -> {
                    Session session = request.session();
                    String loginName = session.attribute("userName");
                    String post = request.queryParams("message");
                    if (post.length() > 140 || post.equals("")){
                        response.redirect("/home.html");
                    } else {
                        userAccounts.get(loginName).getPosts().add(post);
                        jasonWrite();
                        response.redirect("/home.html");
                    }
                    return "";
                })
        );
        Spark.post(
                "/delete",
                ((request, response) -> {
                    Session session = request.session();
                    String loginName = session.attribute("userName");
                    String post = request.queryParams("post");
                    userAccounts.get(loginName).getPosts().remove(post);
                    jasonWrite();
                    response.redirect("/home.html");
                    return "";
                })
        );
        Spark.post(
                "/edit",
                ((request, response) -> {
                    Session session = request.session();
                    String loginName = session.attribute("userName");
                    String post = request.queryParams("editPost");
                    String oldPost = request.queryParams("oldPost");
                    userAccounts.get(loginName).getPosts().set(userAccounts.get(loginName).getPosts().indexOf(oldPost), post);
                    jasonWrite();
                    response.redirect("/home.html");
                    return "";
                })
        );
    }
    public static void jasonRead() throws IOException{
         scanner = new Scanner(f);
         scanner.useDelimiter("\n");
         JsonParser parser = new JsonParser();
         String contents;
         while(scanner.hasNext()){
             contents = scanner.next();
             User user = parser.parse(contents, User.class);
             userAccounts.put(user.getName(), user);
         }

    }
    public static void jasonWrite() throws IOException{
        String json;
        JsonSerializer serializer = new JsonSerializer();
        serializer.include("posts");
        fw = new FileWriter(f);

        for(Map.Entry<String, User>entry: userAccounts.entrySet()){
            json = serializer.serialize(entry.getValue());
            fw.append(json);
            fw.append("\n");
        }
        fw.close();
    }
}
