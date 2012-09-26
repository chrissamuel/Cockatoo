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

  private InetAddress parrotAddress = null;

  public DroneInfo(
    InetAddress parrotAddress) {

    this.parrotAddress = parrotAddress;


    new Thread(new Runnable() {
	public void run() {
           configThread();
        }
    }, "droneConfigThread").start();

  }

  private void configThread() {

    try {
      Socket configSocket = new Socket(parrotAddress, DEFAULT_CONFIGINFO_PORT);
      System.out.println("New configSocket: " + configSocket);

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

  }
}
