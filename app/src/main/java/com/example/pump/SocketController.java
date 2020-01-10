package com.example.pump;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.net.UnknownHostException;



public class SocketController  extends AsyncTask<Void, Void, String> {
    String rawData;
    Socket socket;
    String response;
    PrintWriter printWriterl;
    Context context;
    SQLManager sqlManager;

    String InternalIP;
    int Internalport = -1;

    String ExternalIP;
    int Externalport = -1;
    int timeout = 5000;
    boolean noConnection = false;
    long timeElapsedMilliseconds = 0;



    public SocketController(Context context, String rawData, String IP, int port, boolean isInternal){ //used to test the connection
        this.rawData = rawData;
        this.context = context;
        if(isInternal == true){
            this.InternalIP = IP;
            this.Internalport = port;
        }else{
            this.ExternalIP = IP;
            this.Externalport = port;
        }
   }


    public static Handler UIHandler; //Invokes the UI Thread

    public SocketController(Context context, String rawData){

        this.rawData = rawData;
        this.context = context;
        sqlManager = new SQLManager(context);

        Cursor path = sqlManager.getPath();

        if(path.getCount()>0){
            path.moveToNext();
            if(path.getString(0) != null && path.getString(1) != null){
                InternalIP = path.getString(0);
                Internalport = Integer.parseInt(path.getString(1));
            }

            if(path.getString(2) != null && path.getString(3) != null){
                ExternalIP = path.getString(2);

                Externalport = Integer.parseInt(path.getString(3));
            }

        }

    }

    public SocketController(Context context, String rawData, int SocketTimeout){
        this.rawData = rawData;
        this.context = context;
        sqlManager = new SQLManager(context);

        Cursor path = sqlManager.getPath();

        if(path.getCount()>0){
            path.moveToNext();
            if(path.getString(0) != null && path.getString(1) != null){
                InternalIP = path.getString(0);
                Internalport = Integer.parseInt(path.getString(1));
            }

            if(path.getString(2) != null && path.getString(3) != null){
                ExternalIP = path.getString(2);
                Externalport = Integer.parseInt(path.getString(3));
            }
            this.timeout = SocketTimeout;
        }

    }

    private static void runOnUI(Runnable runnable) { //Runs code to invoke the main thread
        UIHandler.post(runnable);
    }

    @Override
    protected String doInBackground(Void... voids) {


        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {

            int retry = 0;

            boolean pass = true; //used to check if the connection was stable
            if (InternalIP != null && Internalport != -1) {
                while (retry < 5) {
                    try {


                        noConnection = false;
                        this.socket = new Socket();
                        this.socket.connect(new InetSocketAddress(InternalIP, Internalport), 50);

                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        printWriterl = new PrintWriter(socket.getOutputStream());
                        printWriterl.write(rawData);
                        printWriterl.flush();//Sends the message
                        long startTime = System.nanoTime();

                        response = URLDecoder.decode(in.readLine(), "UTF-8");

                        long endTime = System.nanoTime();
                        long timeElapsed = endTime - startTime;
                        timeElapsedMilliseconds = timeElapsed / 1000000;

                        if (timeElapsedMilliseconds < 1) {
                            timeElapsedMilliseconds = 1;
                        }

                        if (response == null) {
                            noConnection = true;
                            retry = retry + 1;
                            //return "Server Not Running";
                        } else {

                    /*if (response.equals("SSH-2.0-OpenSSH_7.2p2 Ubuntu-4ubuntu2.8")) {
                        noConnection = true;
                        return "Server Not Running";
                    }
    */
                            if (response.contains("SSH-2.0-OpenSSH") || response.contains("RFB 003.008")) {
                                noConnection = true;
                                retry = retry + 1;
                                //return "Server Not Running";
                            }
                            //States there was no issue connecting to the server


                            if (noConnection == false) {

                                return response;

                            }

                        }

                    } catch (ConnectException ce) {
                        pass = false;
                        retry = retry + 1;

                    } catch (Exception e) {
                        pass = false;
                        retry = retry + 1;
                    }
                }
            }else{
                pass = false;
                retry = retry + 1;
            }



            if(pass == false && ExternalIP != null && Externalport != -1) {
                try {
                    this.socket = new Socket();
                    //this.socket.setSoTimeout(2000);
                    this.socket.connect(new InetSocketAddress(ExternalIP, Externalport), 20000);

                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    printWriterl = new PrintWriter(socket.getOutputStream());
                    printWriterl.write(rawData);
                    printWriterl.flush();//Sends the message

                    long startTime = System.nanoTime();
                    response = in.readLine();
                    long endTime = System.nanoTime();

                    long timeElapsed = endTime - startTime;
                    timeElapsedMilliseconds = timeElapsed / 1000000;

                    if (response == null) {
                        noConnection = true;
                        return "Server Not Running";
                    } else {

                    /*if (response.equals("SSH-2.0-OpenSSH_7.2p2 Ubuntu-4ubuntu2.8")) {
                        noConnection = true;
                        return "Server Not Running";
                    }
                    */

                        if (response.contains("SSH-2.0-OpenSSH") || response.contains("RFB 003.008")) {
                            noConnection = true;
                            return "Server Not Running";
                        }

                        if (response.equals("STOP")) { //Closes the connection / not working yet
                            socket.close();
                        }
                        //States there was no issue connecting to the server
                        noConnection = false;
                        return response;
                    }

                } catch (SocketTimeoutException s) { //If connection timed out, use setsoTimeOut
                    noConnection = true;
                    return "Server Not Running";
                } catch (UnknownHostException e) { //When the server does not exist

                    noConnection = true;
                    return "Server Not Running";

                } catch (ConnectException exception) { //Can't connect, maybe it's not running

                    noConnection = true;
                    return "Server Not Running";
                } catch (IOException e) {

                    noConnection = true;
                    return "Server Not Running";
                }
            }else {
                return "Server Not Running"; //No External Server attached
            }

        }


        else if(ExternalIP != null && Externalport != -1) {
            try {
                this.socket = new Socket();
                //this.socket.setSoTimeout(2000);
                this.socket.connect(new InetSocketAddress(ExternalIP, Externalport), 20000);

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                printWriterl = new PrintWriter(socket.getOutputStream());
                printWriterl.write(rawData);
                printWriterl.flush();//Sends the message

                long startTime = System.nanoTime();
                response = in.readLine();
                long endTime = System.nanoTime();

                long timeElapsed = endTime - startTime;
                timeElapsedMilliseconds = timeElapsed / 1000000;

                if (response == null) {
                    noConnection = true;
                    return "Server Not Running";
                } else {

                    /*if (response.equals("SSH-2.0-OpenSSH_7.2p2 Ubuntu-4ubuntu2.8")) {
                        noConnection = true;
                        return "Server Not Running";
                    }
                    */

                    if (response.contains("SSH-2.0-OpenSSH") || response.contains("RFB 003.008")) {
                        noConnection = true;
                        return "Server Not Running";
                    }

                    if (response.equals("STOP")) { //Closes the connection / not working yet
                        socket.close();
                    }
                    //States there was no issue connecting to the server
                    noConnection = false;
                    return response;
                }

            } catch (SocketTimeoutException s) { //If connection timed out, use setsoTimeOut
                noConnection = true;
                return "Server Not Running";
            } catch (UnknownHostException e) { //When the server does not exist

                noConnection = true;
                return "Server Not Running";

            } catch (ConnectException exception) { //Can't connect, maybe it's not running

                noConnection = true;
                return "Server Not Running";
            } catch (IOException e) {

                noConnection = true;
                return "Server Not Running";
            }
        }else {
            return "Server Not Running"; //No External Server attached
        }

        //return "Server Not Running";
    }



    public String getPingTime(){

        String time = String.valueOf(timeElapsedMilliseconds);
        if(timeElapsedMilliseconds == 0){

            timeElapsedMilliseconds = timeElapsedMilliseconds;
            timeElapsedMilliseconds = timeElapsedMilliseconds;
        }
        if(noConnection == true){
            return "0";
        }
        return time;
    }
}