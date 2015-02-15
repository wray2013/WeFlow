package com.cmmobi.looklook.common.gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;


public class UDPClient {
    private byte[] buffer = new byte[1024];

    private DatagramSocket ds = null;
    
    private String host = null;
    private int port = 0;

    /**
     * 构造函数，创建UDP客户端
     * @throws Exception
     */
    public UDPClient() throws Exception {

        ds = new DatagramSocket(null);
        ds.setReuseAddress(true);
        ds.bind(new InetSocketAddress(1984));

    }
    
    public void setServerHost(String host, int port){
    	this.host = host;
    	this.port = port;
    }
    
    /**
     * 设置超时时间，该方法必须在bind方法之后使用.
     * @param timeout 超时时间
     * @throws Exception
     * @author <a href="mailto:zhangwei@cmmobi.com">zhangwei</a>
     */
    public final void setSoTimeout(final int timeout) throws Exception {
        ds.setSoTimeout(timeout);
    }

    /**
     * 获得超时时间.
     * @return 返回超时时间
     * @throws Exception
     * @author <a href="mailto:zhangwei@cmmobi.com">zhangwei</a>
     */
    public final int getSoTimeout() throws Exception {
        return ds.getSoTimeout();
    }

    public final DatagramSocket getSocket() {
        return ds;
    }

    /**
     * 向指定的服务端发送数据信息.
     * @param host 服务器主机地址
     * @param port 服务端端口
     * @param bytes 发送的数据信息
     * @return 返回构造后俄数据报
     * @throws IOException
     * @author <a href="mailto:zhangwei@cmmobi.com">zhangwei</a>
     */
    public final DatagramPacket send(final byte[] bytes) throws IOException {
        DatagramPacket dp = new DatagramPacket(bytes, bytes.length, InetAddress
                .getByName(host), port);
        ds.send(dp);
        return dp;
    }

    /**
     * 接收从指定的服务端发回的数据.
     * @param lhost 服务端主机
     * @param lport 服务端端口
     * @return 返回从指定的服务端发回的数据.
     * @throws Exception
     * @author <a href="mailto:zhangwei@cmmobi.com">zhangwei</a>
     */
    public final String receive()
            throws Exception {
        DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
        ds.receive(dp);
        String info = new String(dp.getData(), 0, dp.getLength());
        return info;
    }

    /**
     * 关闭udp连接.
     * @author <a href="mailto:zhangwei@cmmobi.com">zhangwei</a>
     */
    public final void close() {
        try {
            ds.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 测试客户端发包和接收回应信息的方法.
     * 联通地址 125.39.224.104
     * 电信地址 123.150.178.104  
     * 端口是18567
     * @param args
     * @throws Exception
     * @author <a href="mailto:zhangwei@cmmobi.com">zhangwei</a>
     */
    public static void main(String[] args) throws Exception {

        String serverHost = "123.150.178.104";
        int serverPort = 18567;
        UDPClient client = new UDPClient();
        client.setServerHost(serverHost, serverPort);
        client.send(("你好，阿蜜果!").getBytes());
        String info = client.receive();
        System.out.println("服务端回应数据：" + info);
    }
}
