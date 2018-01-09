package com.example.pc_22.coupon2;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by pc-07 on 2018-01-04.
 */

public class Task extends AsyncTask<String, Void, String> {
    public static String ip = "192.168.0.21"; //자신의 IP번호
    public static String sendMsg, receiveMsg;
    String serverip = "http://knjas.or.kr:8083/Learflet/mainCon"; // 연결할 jsp주소



    Task(String sendmsg) {
        this.sendMsg = sendmsg;
    }
    Task(){};

    @Override
    protected String doInBackground(String... strings) {
        try {


            Log.v("hhd", "hello2");
            //String rst = new Task(sendmsg).execute(result,"vision_write").get();
            String str;
            URL url = new URL(serverip);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");
            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

            Log.v("hhd", "hello3");
            if (sendMsg.equals("join")) {
                sendMsg = "join=" + strings[0]+"&joinid="+strings[1]+"&age="+strings[2]+"&gender="+strings[3]+"&interest1="+strings[4]+"&interest2="+strings[5]+"&interest3="+strings[6];
            } else if (sendMsg.equals("login")) {
                sendMsg = "login=" + strings[0];
            }

            osw.write(sendMsg);
            osw.flush();
            Log.v("hhd", "hello4");
            if (conn.getResponseCode() == conn.HTTP_OK) {
                InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(tmp);
                StringBuffer buffer = new StringBuffer();
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
                receiveMsg = buffer.toString();


                Log.v("response",receiveMsg);
            } else {
                Log.i("통신 결과", conn.getResponseCode() + "에러");
            }
            Log.v("hhd", "hello5");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return receiveMsg;
    }

}