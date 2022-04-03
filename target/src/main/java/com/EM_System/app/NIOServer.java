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

    public static final ThreadPoolExecutor executor
            = new ThreadPoolExecutor (8, 8, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
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

        while (true) {
            // System.out.println("Listening on：" + serverPort);
            // 6.Join here, until qualified channel appeared
            if (selector.select() <= 0) {
                continue;
            }

            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) { // Iterate the I/O channel
                SelectionKey key = keys.next();
                // The current channel is clear.
                keys.remove();

                try {
                    // 判断事件类型，做对应的处理
                    if (key.isAcceptable()) {
                        // 取得可以操作的channel, 调用accept完成三次握手，返回与客户端可以通信的channel，并设置为非阻塞
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel channel = server.accept();
                        channel.configureBlocking(false);
                        System.out.println("Handling requests：" + channel.getRemoteAddress());
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

                                    // ############# 业务处理开始 ############
                                    // 英文字符串转大写
                                    String recv = new String(baos.toByteArray()).toLowerCase();

                                    System.out.println(recv);
                                    baos.flush();
                                    baos.reset();
                                    // ############# 业务处理 结束 ############
                                    XmlParser parser = new XmlParser();
                                    Request req = parser.parse(new ByteArrayInputStream(recv.getBytes()));
                                    ArrayList<Result> res;
                                    RequestExecutor executor = new RequestExecutor();
                                    res = req.exec(executor);
                                    parser.CreateXmlResponse(baos, res);
                                    StringBuffer sb = new StringBuffer();
                                    String ans = new String(baos.toByteArray());
                                    sb.append(ans.length() + "\n");
                                    sb.append(ans);
                                    String resp = sb.toString();
                                    System.out.println(resp);
                                    // 业务处理结果返回将数据添加到key中
                                    baos.close();
                                    key.attach(resp);
                                    key.interestOps(SelectionKey.OP_WRITE);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (SAXException e) {
                                    e.printStackTrace();
                                } catch (TransformerException e) {
                                    e.printStackTrace();
                                } catch (ParserConfigurationException e) {
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

