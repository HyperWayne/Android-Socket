
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import org.json.JSONObject;

public class Server_Socket {
	private static Thread th_close;
	private static int serverport = 7777;
	private static ServerSocket serverSocket;
	private static ArrayList<Socket> socketlist=new ArrayList<Socket>();

public static void main(String[] args){
	try {
		serverSocket = new ServerSocket(serverport);
		System.out.println("Server開始執行");
		th_close=new Thread(Judge_Close);
		th_close.start();
		while (!serverSocket.isClosed()) {
			waitNewSocket();
		}
	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	   }
}
private static Runnable Judge_Close=new Runnable(){
		@Override
		public void run() {
		// TODO Auto-generated method stub
		try {
		while(true){
			Thread.sleep(2000);
			for(Socket close:socketlist){
				if(isServerClose(close))	//當該客戶端網路斷線時,從SocketList剔除
					socketlist.remove(close);
				}
			}
		} catch (Exception e) {
				// TODO Auto-generated catch block
		e.printStackTrace();
		}
		}
		};
		private static Boolean isServerClose(Socket socket){//判斷連線是否中斷
		try{
			socket.sendUrgentData(0);	//發送一個字節的緊急數據,默認情況下是沒有開啟緊急數據處理,不影響正常連線
			return false;
		}catch( Exception e){
			return true;
		}finally{}
}
public static void waitNewSocket() {
	try{
	Socket socket = serverSocket.accept();
	createNewThread(socket);
	} catch (Exception e) {
	e.printStackTrace();
	}
}
public static void createNewThread(final Socket socket){

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// 增加新的使用者
				socketlist.add(socket);
					//取得網路輸出串流
				BufferedWriter bw = new BufferedWriter( new OutputStreamWriter(socket.getOutputStream()));
					// 取得網路輸入串流
					BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String tmp;
					JSONObject json_read,json_write;
					json_write = new JSONObject();
					// 當Socket已連接時連續執行
					while (socket.isConnected()) {
						//tmp = br.readLine();
						
						json_write.put("type", "CONNECT\r\n");   
						System.out.println("Luckly"+json_write.toString(4)+"\n");
						
				        //寫入後送出
				        try (OutputStreamWriter out = new OutputStreamWriter(
				            socket.getOutputStream(), StandardCharsets.UTF_8)) {
				            out.write(json_write.toString());
				        }
				        
						// 如果不是空訊息
						//if(tmp!=null){
							//將取到的String抓取{}範圍資料
					//		tmp=tmp.substring(tmp.indexOf("{"), tmp.lastIndexOf("}") + 1);
					//		json_read=new JSONObject(tmp);
							// test
					//		System.out.println(json_read);
							
					//	}else{
					//		socketlist.remove(socket);
					//		break;
					//	}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
}
}