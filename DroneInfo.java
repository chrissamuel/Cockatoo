/* DroneInfo.java
 * ~~~~~~~~~~~
 * Please do not remove the following notices.
 * Copyright (c) 2010 by Geekscape Pty. Ltd.
 * License: GPLv3. http://geekscape.org/static/parrot_license.html
 *
 * To Do
 * ~~~~~
 * Make TCP connection to port 5559 from Drone.
 * Parse out config data sent by drone.
 * Update something the rest of the program uses based on that data. :-)
 */

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

public class DroneInfo {

  private static final int DEFAULT_CONFIGINFO_PORT = 5559;

  private ParrotCommunication parrotCommunication = null;

  public DroneInfo(
    ParrotCommunication parrotCommunication) {

    this.parrotCommunication = parrotCommunication;


    new Thread(new Runnable() {
	public void run() {
           configThread();
        }
    }, "droneConfigThread").start();

  }

  private void configThread() {

    try {
      ServerSocket serverSocket = new ServerSocket(DEFAULT_CONFIGINFO_PORT);
      System.out.println("New ServerSocket: " + serverSocket);

      while(true) {
        try {
          Socket socket = serverSocket.accept();

          new SocketHandler(socket);
        }
        catch (IOException ioException) {
          System.err.println("Socket ioException: " + ioException.getMessage());
        }
      }
    }
    catch (Exception exception) {
      System.err.println("Server exception: " + exception.getMessage());
      System.exit(-1);
    }
  }

  private class SocketHandler extends Thread {

    Socket socket = null;

    public SocketHandler(
      Socket socket) {

      this.socket = socket;
      start();
    }

    public void run() {
//    System.out.println("Socket: " + socket);

      try {
        BufferedReader bufferedReader =
            new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String buffer;

        while ((buffer = bufferedReader.readLine()) != null) {
        System.out.println("Data: " + buffer);

//          executeCommand(buffer);
        }
      }
      catch (IOException ioException) {
//      System.out.println("Socket disconnected");
      }
      finally {
        try {
          socket.close();
        }
        catch (IOException ioException) {}

//      System.out.println("Closed socket connection");
      }
    }

    private void executeCommand(
      String buffer) {

      StringTokenizer stringTokenizer = new StringTokenizer(buffer);

      String command = stringTokenizer.nextToken();
      String parameter = null;

      int repeat = 1;

      if (stringTokenizer.hasMoreTokens())  {
        parameter = stringTokenizer.nextToken();

        try {
          repeat = Integer.parseInt(parameter);
        }
        catch (NumberFormatException numberFormatException) {}
      }

      if (command.equalsIgnoreCase("takeoff")) {
        System.out.println("Command: takeoff");

        parrotCommunication.transmitRefCommand(
          ParrotCommunication.BEHAVIOUR_TAKEOFF
        );
      }
      else if (command.equalsIgnoreCase("land")) {
        System.out.println("Command: land");

        parrotCommunication.transmitRefCommand(
          ParrotCommunication.BEHAVIOUR_LAND
        );
      }
      else if (command.equalsIgnoreCase("forward")) {
        System.out.println("Command: forward: " + repeat);
        parrotCommunication.emergencyAbort = false;

        for (int index = 0;  index < repeat;  index ++) {
          if (parrotCommunication.emergencyAbort == true) break;

          parrotCommunication.transmitProgressiveCommand(
            ParrotCommunication.MODE_PROGRESSIVE, 0f, -0.30f, 0f, 0f
          );
        }

        parrotCommunication.transmitProgressiveCommand(
          ParrotCommunication.MODE_HOVER, 0f, 0f, 0f, 0f
        );
      }
      else if (command.equalsIgnoreCase("backward")) {
        System.out.println("Command: backward");
        parrotCommunication.emergencyAbort = false;

        for (int index = 0;  index < repeat;  index ++) {
          if (parrotCommunication.emergencyAbort == true) break;

          parrotCommunication.transmitProgressiveCommand(
            ParrotCommunication.MODE_PROGRESSIVE, 0f, 0.30f, 0f, 0f
          );
        }

        parrotCommunication.transmitProgressiveCommand(
          ParrotCommunication.MODE_HOVER, 0f, 0f, 0f, 0f
        );
      }
      else {
        System.out.println("Unknown server command: " + command);
      }
    }
  }
}
