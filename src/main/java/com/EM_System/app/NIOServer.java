package com.EM_System.app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.*;
import com.EM_System.XmlParser;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;


public class NIOServer {

    public static final ThreadPoolExecutor executor
            = new ThreadPoolExecutor (8, 8, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    static final String SERVER_IP = "127.0.0.1";
    public static final int SERVER_PORT = 12345;
    public static final char REQUEST_END_CHAR = '#';

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
//            System.out.println("Listening on：" + serverPort);
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
                                    String recv = new String(baos.toByteArray()).toUpperCase();
                                    // ############# 业务处理 结束 ############

                                    // 业务处理结果返回将数据添加到key中
                                    baos.close();
                                    key.attach(recv);
                                    key.interestOps(SelectionKey.OP_WRITE);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else if (key.isWritable()) {
                        key.cancel();
                        SocketChannel channel = (SocketChannel) key.channel();
                        String recv = (String) key.attachment();

                        channel.write(ByteBuffer.wrap(recv.getBytes()));

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

    public static void main(String[] args) throws InterruptedException {
        NIOServer server = new NIOServer();
        try {
            server.startServer(SERVER_IP, SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

