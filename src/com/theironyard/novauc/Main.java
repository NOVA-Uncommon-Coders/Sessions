package com.theironyard.novauc;

        import com.sun.tools.internal.xjc.Messages;
        import org.h2.tools.Server;
        import spark.ModelAndView;
        import spark.Session;
        import spark.Spark;
        import spark.template.mustache.MustacheTemplateEngine;

        import java.sql.*;
        import java.util.ArrayList;
        import java.util.HashMap;



public class Main {

    static int idnumber =0;

    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, user_name VARCHAR, password VARCHAR)");
        stmt.execute("CREATE TABLE IF NOT EXISTS posts (id IDENTITY, postId INT, text VARCHAR, user_name VARCHAR)");
    }

//    public static int lengthOfTable(Connection conn, String currentUser) throws SQLException{
//        int id=1;
//        PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM posts INNER JOIN users ON posts.user_name = users.user_name WHERE users.user_name = ?");
//        stmt.setString(1, currentUser);
//        ResultSet results = stmt.executeQuery();
//        if (results.next()) {
//            id = results.getInt("COUNT(*)")+1;
//        }
//        return id;
//    }

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


    public static void insertMessage(Connection conn, String text,String userName) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO posts VALUES (NULL, ?, ?, ?)");
        stmt.setInt(1,idnumber);
        stmt.setString(2, text);
        stmt.setString(3, userName);
        idnumber++;
        stmt.execute();
    }

    public static void deleteMessage(Connection conn,int id) throws SQLException{
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM posts WHERE posts.postId = ?");
        stmt.setInt(1, id);
        stmt.execute();
    }

    public static ArrayList<Posts> selectAllMessages(Connection conn,String currentUser) throws SQLException {
        ArrayList<Posts> messages = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM posts INNER JOIN users ON posts.user_name = users.user_name WHERE users.user_name = ?");
        stmt.setString(1,currentUser);
        ResultSet results = stmt.executeQuery();
        while (results.next()) {
            int id = Integer.valueOf(results.getString("posts.postId"));
            String name = results.getString("users.user_name");
            String text = results.getString("posts.text");
            Posts message = new Posts(id, name, text);
            messages.add(message);
        }
        return messages;
    }


    public static void main(String[] args) throws SQLException {

        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);

        Spark.staticFileLocation("/templates");
        Spark.init();

        Spark.get("/",
                ((request, response) -> {
                    HashMap m = new HashMap();
                    Session session = request.session();
                    String name = session.attribute("userName");
                    if (name == null) {
                        return new ModelAndView(m, "login.html");
                    }
                    User user = selectUser(conn,name);
                    ArrayList<Posts> msgs = selectAllMessages(conn,user.getUserName());
                    m.put("posts", msgs);
                    m.put("userName", user.getUserName());
                    return new ModelAndView(m, "posts.html");

                }),
                new MustacheTemplateEngine()
        );

        Spark.post("/login",
                ((request, response) -> {
                    String loginName = request.queryParams("loginName");
                    String password = request.queryParams("password");

                    if(loginName==null||password==null){
                        response.redirect("/");
                        return"";
                    }
                    insertUser(conn,loginName,password);
                    Session session = request.session();
                    session.attribute("userName",loginName);
                    response.redirect("/");
                    return "";
                })
        );



        Spark.post("/delete-entry",
                (((request, response) -> {
                    int pnumber = Integer.valueOf(request.queryParams("postId")) ;
                    deleteMessage(conn,pnumber);
                    idnumber=0;
                    response.redirect("/");
                    return "";
                }))
        );

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
