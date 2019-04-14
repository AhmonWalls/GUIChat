import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server2{
	static final int PORT = 8001;
	static ArrayList<ObjectOutputStream> listOOS = new ArrayList<ObjectOutputStream>();

	public static void main(String[] args){
		ServerSocket serverSocket = null;
		Socket socket = null;
		System.out.println("Server has Started!");
		try{
			serverSocket = new ServerSocket(PORT);
		}catch(IOException e){e.printStackTrace();}

		while(true){
			try{
				socket = serverSocket.accept();
				System.out.println("New Client Connected");
			}catch(IOException e){System.out.println(e);}
			new ClientHandleThread(socket,listOOS).start();
		}
	}

}

class ClientHandleThread extends Thread{
	protected Socket socket;
	ArrayList<ObjectOutputStream> listOOS;
	public ClientHandleThread(Socket clientSocket, ArrayList<ObjectOutputStream> listOOS){
		this.socket = clientSocket;
		this.listOOS = listOOS;
	}

	@Override
	public void run(){
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;

		try{
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
			listOOS.add(oos);
		}catch(IOException e){System.out.println(e);}

		try{
		while(true){
			String message = (String)ois.readObject();
			for(int i = 0; i < listOOS.size(); i++){
				listOOS.get(i).writeObject(message);
			}
		}
		}catch(Exception e){System.out.println(e);}

	}

}