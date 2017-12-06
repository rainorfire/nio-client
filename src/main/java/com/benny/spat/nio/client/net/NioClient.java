package com.benny.spat.nio.client.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class NioClient {
	
	private String host;
	private Integer port;
	
	private static BlockingQueue<ByteBuffer> bufferQueue = new LinkedBlockingQueue<ByteBuffer>();
	
	public NioClient(String host,Integer port) {
		this.host = host;
		this.port = port;
		new Thread(new NioClientSender()).start();
	}
	
	/**
	 * 发送方法
	 * @param byteBuffer
	 * @return
	 */
	public boolean send(byte[] byteArray) {
		try {
			int byteLength = byteArray.length;
			ByteBuffer byteBuffer = ByteBuffer.allocate(byteLength + 4);
			byteBuffer.putInt(byteLength);
			byteBuffer.put(byteArray);
			bufferQueue.put(byteBuffer);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.interrupted();
			return false;
		}
		return true;
	}
	
	public class NioClientSender implements Runnable {
		
		private SocketChannel socketChannel;
		
		public NioClientSender() {
			if(socketChannel == null) {
				try {
					socketChannel = SocketChannel.open();
					socketChannel.connect(new InetSocketAddress(host,port));
					while(!socketChannel.finishConnect()) {
						System.out.println("NIO客户端正在连接服务端...");
					}
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException("NIO客户端正在连接服务端发生IO异常");
				}
			}
		}

		public void run() {
			while(true) {
				try {
					ByteBuffer byteBuffer = bufferQueue.take();
					if(byteBuffer.position() > 0) {
						byteBuffer.flip();
					}
					while(byteBuffer.hasRemaining()) {
						socketChannel.write(byteBuffer);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
					Thread.interrupted();
					break;
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("发送消时发生IO异常");
				}
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		
		NioClient client = new NioClient("127.0.0.1",8080);
		System.out.println("NIO客户端连接服务端完成！");
		File file = new File("C:\\Users\\cyj_m\\Desktop\\寒门枭士.txt");
		FileInputStream in = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));  
		while(true) {
			String readLine = reader.readLine();
			if(readLine != null) {
				client.send(readLine.getBytes());
			}else {
				break;
			}
		}
		
		reader.close();
		
//		String msg1 = "1111不要为我担心!!!!不要为我担心!!!!不要为我担心!!!!不要为我担心!!!!不要为我担心!!!!不要为我担心!!!!不要为我担心!!!!"+System.currentTimeMillis();
//		client.send(msg1.getBytes());
//		
//		
//		String msg2 = "2222在践行自己竞选时的政治承诺上，特朗普从未叫人失望过。继退出《巴黎协定》、大规模减税、退出全球移民协议之后，今天上午，他再次在全球抛下一则重磅消息。白宫发言人称，特朗普将于美东时间周三发布两项声明：承认耶路撒冷为以色列首都，并决定将美驻特拉维夫大使馆搬迁至耶路撒冷。"+System.currentTimeMillis();
//		client.send(msg2.getBytes());
//		
//		String msg3 = "众所周知，中东是世界的“火药桶”，而过去很长一段时间，巴勒斯坦以色列问题都是中东的核心问题，耶路撒冷则是巴以和平谈判中分歧最严重的议题之一，十分敏感。特朗普此举，是出于何意？如这一声明真的如期发表，在其几乎无疑会加剧中东矛盾冲突之下，这一地区又会有何走向？"+System.currentTimeMillis();
//		client.send(msg3.getBytes());
//		
//		String msg4 = "历史"+System.currentTimeMillis();
//		client.send(msg4.getBytes());
	}
}
