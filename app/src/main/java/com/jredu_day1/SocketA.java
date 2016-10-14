package com.jredu_day1;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dao.UserDemo;
import com.util.UrlUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


public class SocketA extends Activity implements Runnable{
    private String content;
    private TextView tv_msg ;
    private EditText ed_msg;
    private Button btn_msg;
    PrintWriter out = null;
    private BufferedReader in = null;
    private Socket socket;
    String c = "";
    private static final int PORT = 9998;
    private static final String HOST = UrlUtil.BASE;

    public void init(){
        tv_msg = (TextView) findViewById(R.id.s_tv);
        ed_msg = (EditText) findViewById(R.id.s_et);
        btn_msg = (Button) findViewById(R.id.s_bt);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);
        init();

        if(Build.VERSION.SDK_INT > 9){
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            socket = new Socket(HOST,PORT);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf-8"));
            out = new PrintWriter(
                    new BufferedWriter(
                        new OutputStreamWriter(
                                socket.getOutputStream(),"utf-8")),true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        btn_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserDemo userDemo = MyApplaction.getMyApplaction().getUser();
                String msg;
                if(userDemo.getUname().equals("")){
                    msg = "匿名同学："+ed_msg.getText().toString();
                }else {
                    msg = userDemo.getUname() + "：" + ed_msg.getText().toString();
                }
                if(socket.isConnected()){
                    if(!socket.isOutputShutdown()){
                        out.println(msg);
                    }
                }
            }
        });
        new Thread(this).start();
    }
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tv_msg.setText(c);
        }
    };

    @Override
    public void run() {
        while(true){
            if(!socket.isClosed()){
                if(socket.isConnected()){
                    if(!socket.isInputShutdown()){
                        try {
                            if((content = in.readLine())!=null){
                                c = content+"\n"+c;
                                mHandler.sendMessage( mHandler.obtainMessage());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
