package com.benny.spat.nio.client.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NioClient {
	
	public static void main(String[] args) throws IOException {
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.connect(new InetSocketAddress(8080));
		while(!socketChannel.finishConnect()) {
			System.out.println("NIO客户端正在连接服务端...");
		}
		System.out.println("NIO客户端连接服务端完成！");
		ByteBuffer byteBuffer = ByteBuffer.allocate(512);
		byteBuffer.put(("不要为我担心！！！"+System.currentTimeMillis()).getBytes());
		byteBuffer.flip();
		while(byteBuffer.hasRemaining()) {
			socketChannel.write(byteBuffer);
		}
		try {
			Thread.sleep(5000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
