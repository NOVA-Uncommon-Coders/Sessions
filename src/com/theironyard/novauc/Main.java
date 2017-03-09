package com.theironyard.novauc;

import org.h2.tools.Server;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
//    static User user;
//    static HashMap<String,User> users = new HashMap<>();

    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, user_name VARCHAR, password VARCHAR)");
        stmt.execute("CREATE TABLE IF NOT EXISTS messages (id IDENTITY, postId INT, text VARCHAR, user_name VARCHAR)");
    }

    public static int lengthOfTable(Connection conn, String currentUser) throws SQLException{
        int id=1;
        PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM messages INNER JOIN users ON messages.user_name = users.user_name WHERE users.user_name = ?");
        stmt.setString(1, currentUser);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            id = results.getInt("COUNT(*)")+1;
        }
        return id;
    }

    public static void insertUser(Connection conn, String name, String password) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES (NULL, ?, ?)");
        stmt.setString(1, name);
        stmt.setString(2, password);
        stmt.execute();
    }

    public static User selectUser(Connection conn, String name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE user_name = ?");
        stmt.setString(1, name);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            int id = results.getInt("id");
            String password = results.getString("password");
            return new User(id, name, password);
        }
        return null;
    }
    public static boolean userNameInUse(Connection conn, String name)throws SQLException{
        boolean thereIsAlready=false;
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE user_name = ?");
        stmt.setString(1, name);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            String userName = results.getString("user_name");
            if(userName.equalsIgnoreCase(name)){
                thereIsAlready=true;
            }

        }
        return thereIsAlready;
    }

    public static void insertMessage(Connection conn, String text,String userName) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO messages VALUES (NULL, ?, ?, ?)");
        stmt.setInt(1,lengthOfTable(conn,userName));
        stmt.setString(2, text);
        stmt.setString(3, userName);
        stmt.execute();
    }

    public static Messages selectMessage(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM messages INNER JOIN users ON messages.user_name = users.user_name WHERE messages.id = ?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            String name = results.getString("users.user_name");
            String text = results.getString("messages.text");
            return new Messages(id,text,name);
        }
        return null;
    }

    public static void deleteMessage(Connection conn,int id) throws SQLException{
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM messages WHERE messages.id = ?");
        stmt.setInt(1, id);
        stmt.execute();
    }

    public static ArrayList<Messages> selectAllMessages(Connection conn,String currentUser) throws SQLException {
        ArrayList<Messages> messages = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM messages INNER JOIN users ON messages.user_name = users.user_name WHERE users.user_name = ?");
        stmt.setString(1,currentUser);
        ResultSet results = stmt.executeQuery();
        while (results.next()) {
            int id = Integer.valueOf(results.getString("messages.postId"));
            String name = results.getString("users.user_name");
            String text = results.getString("messages.text");
            Messages message = new Messages(id, name, text);
            messages.add(message);
        }
        return messages;
    }
//
//    public static ArrayList<Messages> selectReplies(Connection conn, int replyId) throws SQLException {
//        ArrayList<Messages> messages = new ArrayList<>();
//        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM messages INNER JOIN users ON messages.user_id = users.id WHERE messages.reply_id = ?");
//        stmt.setInt(1, replyId);
//        ResultSet results = stmt.executeQuery();
//        while (results.next()) {
//            String text = results.getString("messages.text");
//            String name = results.getString("users.name");
//            Messages message = new Messages(id, replyId, name, text);
//            messages.add(message);
//        }
//        return messages;
//    }

    public static void main(String[] args) throws SQLException {

        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);

        Spark.staticFileLocation("/templates");
        Spark.init();

        Spark.get("/",
                ((request, response) -> {
                    HashMap m = new HashMap();

                    //This is the new sessions login that uses the database instead of the array
                    Session session = request.session();
                    String name = session.attribute("userName");
                    if (name == null) {
                        return new ModelAndView(m, "login.html");
                    }
                    User user = selectUser(conn,name);
                    ArrayList<Messages> msgs = selectAllMessages(conn,user.getUserName());
                    m.put("messages", msgs);
                    m.put("userName", user.getUserName());
                    return new ModelAndView(m, "messages.html");

                }),
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
//                    //the next few blocks are meant as catchers for the incorrect login scenario
//                    if (user==null) {
//                        response.redirect("/login");
//                    }
                    if(loginName==null||password==null){
                        response.redirect("/");
                        return"";
                    }
                    if(!userNameInUse(conn,loginName)){
                        //send boolean for already in use
                        //HTML TRY ANOTHER NAME
                        response.redirect("/");
                        return "";
                    }
                    User user=selectUser(conn,loginName);

                    if(user.getUserName().equalsIgnoreCase(loginName) && user.getPassword().equals(password)) {

                        Session session = request.session();
                        session.attribute("userName", loginName);
                        response.redirect("/");
                        return "";
                    }
                    response.redirect("/login");
                    return "";
                })
        );

        Spark.post("/delete-entry",
                (((request, response) -> {
                    int someId = Integer.valueOf(request.queryParams("postId")) ;
                    deleteMessage(conn,Integer.valueOf(request.queryParams("postId")));
                    response.redirect("/");
                    return "";
                }))
                );

        Spark.post("/create-user",
                ((request, response) -> {
                    String username = request.queryParams("createName");
                    String password = request.queryParams("password");

                    if(userNameInUse(conn,username)){
                        //send boolean for already in use
                        //HTML TRY ANOTHER NAME
                        response.redirect("/");
                        return "";
                    }
                    insertUser(conn,username,password);
                    response.redirect("/");
                    return "";
                })
        );
//        Spark.post("/wrong-pass",
//                ((request, response) -> {
//                    String username = request.queryParams("username");
//                    String password = request.queryParams("password");
//
//                    if (user.getUserName().equalsIgnoreCase(username) && user.getPassword().equals(password)) {
//                        Session session = request.session();
//                        session.attribute("userName", username);
//                        response.redirect("/");
//                        return "";
//                    }
//                    response.redirect("/wrong-pass");
//                    return "";
//                })
//
//        );

        Spark.post("/create-message",
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("userName");
                    User user = selectUser(conn,name);
                    if (user == null) {
                        throw new Exception("User is not logged in");
                    }

                    String post = request.queryParams("blogPost");
                    insertMessage(conn,post,user.getUserName());

                    response.redirect("/");
                    return "";
                })
                );
//        Spark.get("/?editId={{id}}",
//                ((request, response) -> {
//                    Session session=request.session();
//                    String name = session.attribute("userName");
//                    User user = users.get(name);
//                    if(user == null){
//                        throw new Exception("User is not logged in");
//                    }
//                    String id = request.queryParams("id");
//                    int postId=Integer.valueOf(id);
//                    user.getPosts().remove(postId);
//                    response.redirect("/create-message");
//                    return "";
//                })
//        );
//        Spark.get("/?deleteId={{id}}",
//                ((request, response) -> {
//                    Session session=request.session();
//                    String name = session.attribute("userName");
//                    User user = users.get(name);
//                    if(user == null){
//                        throw new Exception("User is not logged in");
//                    }
//                    String id = request.queryParams("deleteId");
//                    int postId=Integer.valueOf(id);
//                    user.getPosts().remove(postId);
//                    response.redirect(request.headers("Referer"));
//                    return "";
//                })
//        );
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

