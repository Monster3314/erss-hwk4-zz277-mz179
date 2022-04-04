package com.EM_System.app;

import com.EM_System.XmlParser;
import com.EM_System.pojo.Request;
import com.EM_System.pojo.Result;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Task implements Runnable {
    private Socket socket;

    public Task(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        InputStream inputStream= null;
        try {
            inputStream = this.socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
        BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
        String temp=null;
        StringBuilder sb = new StringBuilder();
        while(true){
            try {
                if ((temp = bufferedReader.readLine()) == null) break;
                System.out.println(temp);
                sb.append(temp);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        System.out.println(sb.toString());
        XmlParser parser = null;
        try {
            parser = new XmlParser();
        } catch (ParserConfigurationException | TransformerConfigurationException e) {
            e.printStackTrace();
        }
        Request req = parser.parse(new ByteArrayInputStream(sb.toString().getBytes()));
        if (req == null) {
            System.out.println("Malformed request");
            return;
        }
        ArrayList<Result> res;
        RequestExecutor executor = null;
        try {
            executor = new RequestExecutor();
        } catch (IOException e) {
            e.printStackTrace();
        }
        res = req.exec(executor);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            parser.CreateXmlResponse(baos, res);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        sb.delete(0, sb.length());
        String ans = new String(baos.toByteArray());
        System.out.println(ans.length());
        sb.append(ans.length() + System.lineSeparator());
        sb.append(ans);
        String resp = sb.toString();
        OutputStream outputStream= null;//获取一个输出流，向服务端发送信息
        try {
            outputStream = this.socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter printWriter=new PrintWriter(outputStream);//将输出流包装成打印流
        printWriter.print(resp);
        printWriter.flush();
        try {
            socket.shutdownOutput();//关闭输出流
        } catch (IOException e) {
            e.printStackTrace();
        }


        //关闭相对应的资源
        try {
            baos.close();
            printWriter.close();
            outputStream.close();
            bufferedReader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}