package com.EM_System.app;

import com.EM_System.XmlParser;
import com.EM_System.pojo.Request;
import com.EM_System.pojo.Result;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
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
        BufferedReader br = new BufferedReader(inputStream);
        int length = Integer.parseInt(br.readline());
        byte[] data = new byte[length];
        try {
            inputStream.read(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String recv = new String(data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XmlParser parser = null;
        try {
            parser = new XmlParser();
        } catch (ParserConfigurationException | TransformerConfigurationException e) {
            e.printStackTrace();
        }
        Request req = parser.parse(new ByteArrayInputStream(recv.getBytes()));
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
        try {
            baos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        baos.reset();
        try {
            parser.CreateXmlResponse(baos, res);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
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
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}