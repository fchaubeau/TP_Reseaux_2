///A Simple Web Server (WebServer.java)

package http.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.nio.file.*;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * 
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 * 
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer {

  /**
   * WebServer constructor.
   */
  protected void start() {
    ServerSocket s;

    System.out.println("Webserver starting up on port 80");
    System.out.println("(press ctrl-c to exit)");
    try {
      // create the main server socket
      s = new ServerSocket(3000);
    } catch (Exception e) {
      System.out.println("Error: " + e);
      return;
    }

    System.out.println("Waiting for connection");
    for (;;) {
      try (Socket remote = s.accept();
      BufferedReader in = new BufferedReader(new InputStreamReader(
        remote.getInputStream()));
        PrintWriter out = new PrintWriter(remote.getOutputStream());
      ){
        // read the data sent. We basically ignore it,
        // stop reading once a blank line is hit. This
        // blank line signals the end of the client HTTP
        // headers.
        String str = ".";
        String request = "";
        while (str != null && !str.equals("")){
          str = in.readLine();
          request += str;
          request += '\n';
          //System.out.println(str);
        }
        System.out.println(request);
        String [] splittedRequest = request.split(" ");
        String method = splittedRequest[0];
        String path = splittedRequest[1];

        switch(method){
          case "GET" : 
            processGetMethod(path, remote, out);
            break;
          default :
          break;
        }

        /*// Basic response
        // Send the response
        // Send the headers
        out.println("HTTP/1.0 200 OK");
        out.println("Content-Type: text/html");
        out.println("Server: Bot");
        // this blank line signals the end of the headers
        out.println("");
        // Send the HTML page
        out.println("<H1>Welcome to the Ultra Mini-WebServer</H2>");
        out.flush();
        // End of basic response*/

        remote.close();
      } catch (Exception e) {
        System.out.println("Error: " + e);
      }
    }
  }

  public void processGetMethod(String path, Socket client, PrintWriter out){
    out.println("HTTP/1.0 200 OK");
    DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date currentDate = Calendar.getInstance().getTime();
    out.println("Date: " + format.format(currentDate));
    out.println("Content-Type: text/html; charset=UTF-8");
    out.println("Content-Encoding: UTF-8");
    // this blank line signals the end of the headers
    out.println("");
    // Send the HTML page
    Path filePath = Paths.get(path);
    if(Files.exists(filePath)){

    }
    out.println("<H1>Welcome to the Ultra Mini-WebServer</H2>");
    out.println("<p>Je l'ai un peu modifie ! </p>");
    out.flush();

  }

  /*private static void GETRequest(String path, Socket client) throws IOException {
    if (path.equals("/")) {
      // Liste toutes les ressources
      String res = "<h1>Liste des ressources : </h1><ul>";
      File directoryPath = new File("./http/ressources");
      String[] files = directoryPath.list();
      for (String file : files) {
          res += "<li><a href=\"/" + file + "\">" + file + "</a>" + "</li>";
      }
      res += "</ul>";
      sendResponse(client, "200 OK", "text/html", res.getBytes());
    }
    else {
        Path filePath = getFilePath(path);
        if (Files.exists(filePath)) {
            // Le fichier existe
            String contentType = guessContentType(filePath);
            sendResponse(client, "200 OK", contentType, Files.readAllBytes(filePath));
        } else {
            // Le fichier n'existe pas : 404
            byte[] notFoundContent = "<h1>Not found :(</h1>".getBytes();
            sendResponse(client, "404 Not Found", "text/html", notFoundContent);
        }
    }
  }*/

  /**
   * Start the application.
   * 
   * @param args
   *            Command line parameters are not used.
   */
  public static void main(String args[]) {
    WebServer ws = new WebServer();
    ws.start();
  }
}
