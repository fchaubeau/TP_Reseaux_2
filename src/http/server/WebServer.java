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
import java.io.*;

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
        }
        System.out.println(request);
        String [] splittedRequest = request.split(" ");
        String method = splittedRequest[0];
        String path = "../" + splittedRequest[1];

        switch(method){
          case "GET" : 
            processGetMethod(path, remote);
            break;
          case "POST" :
            int contentLength= 0;
            for(int i = 0; i < splittedRequest.length; i++){
              if(splittedRequest[i].contains("Content-Length")){
                String strContentLength = splittedRequest[i+1];
                strContentLength = strContentLength.substring(0,strContentLength.length()-2);
                contentLength = Integer.parseInt(strContentLength);
              }
            }
            char [] buffer = new char[contentLength];
            int charsIn = in.read(buffer, 0, contentLength);
            StringBuilder data = new StringBuilder(charsIn);
            data.append(buffer, 0, charsIn);
            String strData = data.toString(); 
            processPostMethod(strData,remote);
            break;
          default :
          break;
        }

        out.flush();
        remote.close();
      } catch (Exception e) {
        System.out.println("Error: " + e);
      }
    }
  }

  public void processGetMethod(String path, Socket client) throws IOException{
    Path filePath = Paths.get(path);
    System.out.println(filePath);
    if(Files.exists(filePath)){
      String contentType = Files.probeContentType(filePath);
      sendResponse(client, "200 OK", contentType, Files.readAllBytes(filePath));
    } else {
      byte[] notFound = "<p>Error 404, page not found<p>".getBytes();
      sendResponse(client,"404 Not Found", "text/html", notFound);
    }
  }

  public void processPostMethod(String data, Socket client) throws IOException{
    if(data != null){
      System.out.println(data);
      sendResponse(client,"200 OK", "text/html", ("<p>Data Received by server<p/>").getBytes());
    }
    else{
      sendResponse(client,"200 OK", "text/html", ("<p>Empty Post method received<p/>").getBytes());
    }
    
  }

  protected void sendResponse(Socket client, String code, String type, byte[] content){
    try{
      PrintWriter out = new PrintWriter(client.getOutputStream());
      DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
      Date currentDate = Calendar.getInstance().getTime();
      String goodDate = format.format(currentDate).toString();
      out.print("HTTP/1.0 ");
      out.println(code);
      out.println("Date:" + goodDate);
      out.println("Content-Type:" + type);
      out.println("");
      out.println(new String(content));
      System.out.print("HTTP/1.0 ");
      System.out.println(code);
      System.out.println(type);
      System.out.println(goodDate);
      System.out.println("");
      System.out.println(new String(content));
      out.flush();
    } catch (Exception e) {
      System.out.println("Error: " + e);
    }

  }

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
