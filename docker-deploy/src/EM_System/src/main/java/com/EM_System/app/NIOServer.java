package com.EM_System.app;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.*;
import com.EM_System.XmlParser;
import com.EM_System.pojo.Account;
import com.EM_System.pojo.Request;
import com.EM_System.pojo.Result;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;


public class NIOServer {

    public static final ExecutorService executor = Executors.newCachedThreadPool();
    final InetAddress addr;
    final String SERVER_IP;
    public static final int SERVER_PORT = 12345;
    public static final char REQUEST_END_CHAR = '#';


    public NIOServer() throws UnknownHostException {
        this.addr = InetAddress.getLocalHost();
        this.SERVER_IP = addr.getHostAddress();
    }

    public void startServer(String serverIP, int serverPort) throws IOException, InterruptedException {

        // 1. Create ServerSocketChannel
        ServerSocketChannel serverChannel = ServerSocketChannel.open();

        // 2. bind server IP and Port
        serverChannel.bind(new InetSocketAddress(serverIP, serverPort));

        // 3. set the configuration to be nonblocking
        serverChannel.configureBlocking(false);

        // 4. Create selector
        Selector selector = Selector.open();

        /**
         * SelectionKey.OP_ACCEPT get datagram </br>
         */
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        long startTime=System.currentTimeMillis(); 
        while (true) {
            // System.out.println("Listening on：" + serverPort);
            // 6.Join here, until qualified channel appeared
            if (selector.select() <= 0) {
                long endTime=System.currentTimeMillis();
                System.out.println((endTime - startTime)+"ms");
                continue;
            }

            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) { // Iterate the I/O channel
                SelectionKey key = keys.next();
                // The current channel is clear.
                keys.remove();

                try {
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel channel = server.accept();
                        channel.configureBlocking(false);
                        channel.register(selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        key.interestOps(SelectionKey.OP_CONNECT);

                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    SocketChannel channel = (SocketChannel) key.channel();
                                    ByteBuffer buffer = ByteBuffer.allocate(65535);
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    int len = 0;
                                    while (true) {
                                        buffer.clear();
                                        len = channel.read(buffer);
                                        if (len < 1)
                                            break;
                                        buffer.flip();
                                        while (buffer.hasRemaining()) {
                                            baos.write(buffer.get());
                                        }
                                    }
                                    String recv = new String(baos.toByteArray()).toLowerCase();
                                    recv = recv.substring(recv.indexOf(System.lineSeparator()) + 1);
                                    baos.flush();
                                    baos.reset();
                                    XmlParser parser = new XmlParser();
                                    Request req = parser.parse(new ByteArrayInputStream(recv.getBytes()));
                                    if (req == null) {
                                        System.out.println("Malformed request");
                                        key.cancel();
                                        baos.close();
                                        return;
                                    }
                                    ArrayList<Result> res;
                                    RequestExecutor executor = new RequestExecutor();
                                    res = req.exec(executor);
                                    parser.CreateXmlResponse(baos, res);
                                    StringBuffer sb = new StringBuffer();
                                    String ans = new String(baos.toByteArray());
                                    sb.append(ans.length() + System.lineSeparator());
                                    sb.append(ans);
                                    String resp = sb.toString();
                                    baos.close();
                                    key.attach(resp);
                                    key.interestOps(SelectionKey.OP_WRITE);
                                } catch (IOException | TransformerException | ParserConfigurationException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else if (key.isWritable()) {
                        key.cancel();
                        SocketChannel channel = (SocketChannel) key.channel();
                        String resp = (String) key.attachment();
                        channel.write(ByteBuffer.wrap(resp.getBytes()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
             
        }
    }

    public static void main(String[] args) throws InterruptedException, UnknownHostException {
        NIOServer server = new NIOServer();
        try {
            server.startServer(server.SERVER_IP, SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
/*
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;



public class NIOServer {

    public static final ExecutorService executor = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        try{
            ServerSocket serverSocket = new ServerSocket(12345);
            Socket socket = null;
            while(true){
                socket = serverSocket.accept();
        				Task task = new Task(socket);
                executor.execute(task);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

}*/
