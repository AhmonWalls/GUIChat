import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.event.EventHandler;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import java.util.ArrayList;

public class GUIClient extends Application{

	@Override
	public void start(Stage primaryStage){
		BorderPane mainPane = new BorderPane();
		VBox chatDisplay = new VBox();
		HBox bottom = new HBox(15);

		TextField nameTextField = new TextField();
		TextField msgTextField = new TextField();
		nameTextField.setPromptText("Display Name");
		msgTextField.setPromptText("Enter Message");
		Button sendButton = new Button("Send");
		bottom.getChildren().add(nameTextField);
		bottom.getChildren().add(msgTextField);
		bottom.getChildren().add(sendButton);

		mainPane.setCenter(chatDisplay);
		mainPane.setBottom(bottom);
		Scene scene = new Scene(mainPane, 600,300);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Chat Room Client");
		primaryStage.show();

		Platform.runLater(new Runnable(){
			@Override
			public void run(){
				Text connectingText = new Text("Connecting to server...");
				chatDisplay.getChildren().add(connectingText);
			}
		});


		//Server Stuff
		Socket socket = null;
		try{
			socket = new Socket("localhost",8001);
		}catch(Exception ex){System.out.println(ex);}

		Platform.runLater(new Runnable(){
			@Override
			public void run(){
				Text connectedText = new Text("Connected!");
				chatDisplay.getChildren().add(connectedText);
			}
		});
		OutputThread ot1 = new OutputThread(socket,sendButton,nameTextField,msgTextField);
		InputThread it1 = new InputThread(socket,chatDisplay);

		Thread t1 = new Thread(it1);
		Thread t2 = new Thread(ot1);
		t2.start();
		t1.start();


	}


	public static void main(String[] args){
		GUIClient.launch();
	}
}

class InputThread implements Runnable{
	Socket socket = null;
	VBox chatDisplay = null;
	public InputThread(Socket socket, VBox chatDisplay){
		this.socket = socket;
		this.chatDisplay = chatDisplay;
	}


	@Override
	public void run(){
		try{
			ObjectInputStream inputFromServer = new ObjectInputStream(socket.getInputStream());
			while(true){
				System.out.println("before read object");
				String message = "In Run: " + (String)inputFromServer.readObject();
				System.out.println("After read object");
				System.out.println(message);
				Platform.runLater(new Runnable(){
					@Override
					public void run(){
						System.out.println("Inner run");
						Text messageText = new Text(message);
						chatDisplay.getChildren().add(messageText);
					}
				});
			}
		}catch(Exception ex){System.out.println(ex);}
	}

}

class OutputThread implements Runnable{
	Socket socket = null;
	Button sendButton = null;
	TextField nameTextField = null;
	TextField msgTextField = null;
	String aMessage = "";
	boolean sendAMessage = true;
	ObjectOutputStream outputToServer = null;
	public OutputThread(Socket socket, Button sendButton, TextField nameTextField, TextField msgTextField){
		this.socket = socket;
		this.sendButton = sendButton;
		this.nameTextField = nameTextField;
		this.msgTextField = msgTextField;

		try{
					this.outputToServer = new ObjectOutputStream(socket.getOutputStream());
		}catch(Exception ex){System.out.println(ex);}

	}

	public void newMessage(String messageToSend){
		this.aMessage = messageToSend;
	}

	@Override
	public void run(){
		/*try{
			ObjectOutputStream outputToServer = new ObjectOutputStream(socket.getOutputStream());
		}catch(Exception ex){System.out.println(ex);}*/


		sendButton.setOnAction(e ->{
			String name = "";
			String messageToSend = "";
			name = nameTextField.getText();
			messageToSend = name + "> " + msgTextField.getText();
			System.out.println(this.aMessage);
			newMessage(messageToSend);
			sendAMessage = true;
			try{
			outputToServer.writeObject(messageToSend);
			}catch(Exception ex){System.out.println(ex);}
			System.out.println("Send button Clicked");
			System.out.println(this.aMessage);
		});

	/*
		try{
			ObjectOutputStream outputToServer = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("Before while");
			while(true){
				System.out.println("Before if");
				if(!aMessage.equals("")){
					System.out.println("Before write");
					outputToServer.writeObject(this.aMessage + "\n");
					System.out.println("After Write");
					sendAMessage = false;
					aMessage = "";

				}

			}

		}catch(Exception ex){System.out.println(ex);} */
	}
}
