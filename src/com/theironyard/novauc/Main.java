package com.theironyard.novauc;
import java.util.ArrayList;
import java.util.HashMap;
import spark.*;
import spark.template.mustache.MustacheTemplateEngine;

public class Main {

    static MyMessages messes;
    static User user;
    static HashMap<String, ArrayList<MyMessages>> ms = new HashMap<>();

    public static void main(String[] args) {
        Spark.init();
        Spark.get("/", ((request, response) -> {
            Session session = request.session();
            String name = session.attribute("usersName");
            HashMap hm = new HashMap<>();
            if (user == null) {
                return new ModelAndView(hm, "home.html");
            } else {
                hm.put("name", user.name);
                hm.put("hm", ms.get(user.name));
                return new ModelAndView(hm, "messages.html");
        }}), new MustacheTemplateEngine());

        Spark.post("/createaUser", ((request, response) -> {
            String name = request.queryParams("user");
            String password = request.queryParams("password");
            user = new User(new ArrayList<>(), name, password);

            if (user == null) {
                user = new User(new ArrayList<>(), name, password);}
            Session session = request.session();
            session.attribute("usersName", name);
            response.redirect("/");
            return "";
        }));

        Spark.post("/createaMessage", ((request, response) -> {
            Session session = request.session();
            String name = session.attribute("usersName");
            String mo = request.queryParams("createaMessage");
            messes = new MyMessages(mo);
            user.messages.add(messes);
            ms.put(user.name, user.messages);
            response.redirect("/");
            return "";
        }));

        Spark.post("/deleteaMessage", ((Request request, Response response) -> {
            Session session = request.session();
            session.attribute("usersName");
            ms.get(user.name);
            int id = Integer.valueOf(request.queryParams("ID#"));
            ms.get(user.name).remove(id - 1);
            response.redirect("/");
            return "";
        }));

        Spark.post("/editaMessage", ((request, response) -> {
            Session session = request.session();
            session.attribute("usersName");
            ms.get(user.name);
            int id = Integer.valueOf(request.queryParams("message#"));
            ms.get(user.name).get(id - 1).mo = request.queryParams("newMessage");
            response.redirect("/");
            return "";
        }));
    }
}