import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.List;
import javax.swing.*;

public class Client extends Thread implements ActionListener , ItemListener {

	ChatMessage myObject;
	boolean sendingdone = false, receivingdone = false;
	Scanner scan;
	Socket socketToServer;
	ObjectOutputStream myOutputStream;
	ObjectInputStream myInputStream;
	Frame f;
	TextField tf, tfuname;
	String username;
	TextArea ta;
	Panel topPanel;
	Button ConnectButton, DisconnectButton;
	List tlist;         
	Panel centerPanel; 

	public Client(){	
		
		f = new Frame();
		f.setSize(300,400);
		f.setTitle("Chat Client");
		f.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){
				System.exit(0);
			}
		});

		tfuname = new TextField();
		topPanel = new Panel();
		ConnectButton = new Button("Connect");
		DisconnectButton = new Button("Disconnect");
		DisconnectButton.setEnabled(false);
		topPanel.add(tfuname);
                topPanel.add(ConnectButton);
                topPanel.add(DisconnectButton);
		topPanel.setLayout(new GridLayout());
		tfuname.addActionListener(this);
		ConnectButton.addActionListener(this);
		DisconnectButton.addActionListener(this);

		tlist = new List();
		tlist.add("All");
	        tlist.addActionListener(new MyListActionListener());
		tlist.addItemListener(this);
                
		tf = new TextField();
		tf.setEnabled(false);
		tf.addActionListener(this);
		f.add(tf, BorderLayout.SOUTH);
		ta = new TextArea();
		centerPanel = new Panel();
		centerPanel.add(ta);
		centerPanel.setLayout(new GridLayout(2,1));
                
               
		f.add(topPanel, BorderLayout.NORTH);
		//f.add(ta, BorderLayout.CENTER);
                f.add(centerPanel, BorderLayout.CENTER);
		f.add(tlist, BorderLayout.EAST);
	        f.pack();	
		f.setVisible(true);
	}
	public void actionPerformed(ActionEvent ae){
	     Object src = ae.getSource();
	     if(src == ConnectButton)
	     {
	       try {
	              //myObject = new ChatMessage2();
		      //myObject.setName(tfuname.getText());

		      socketToServer = new Socket("127.0.0.1", 9118);
		    //   socketToServer = new Socket("host_where_server_is_running", 9118);
		      myOutputStream = new ObjectOutputStream(socketToServer.getOutputStream());
		      myInputStream  = new ObjectInputStream(socketToServer.getInputStream());
		      start();
		      tf.setEnabled(true);
		      DisconnectButton.setEnabled(true);
		      ConnectButton.setEnabled(false);
		      //myOutputStream.writeObject(myObject);
	       }
		catch(Exception e) {
		      System.out.println(e.getMessage());	
               }
	     }
	     else if( src == tf) {
		myObject = new ChatMessage();
		myObject.setMessage(tf.getText());
		tf.setText("");
		try{
			myOutputStream.reset();
			myOutputStream.writeObject(myObject);
		}catch(IOException ioe){
			System.out.println(ioe.getMessage());
		}
	     }
	     else if(src == DisconnectButton)
	     {
	       try {
	            //myOutputStream.close();
		    //myInputStream.close();
		    //socketToServer.close();
		    myObject = new ChatMessage();
		    myObject.setMessage("bye");
		    myOutputStream.writeObject(myObject);
		   }
		   catch(IOException ioe) {
		       System.out.println(ioe.getMessage());
		   }
		   catch(Exception e) {
		   	System.out.println(e.getMessage());
		   }
	     }
	}

	class MyListActionListener implements ActionListener {
	    public void actionPerformed(ActionEvent ae)
	    {
	        List list = (List)(ae.getSource());
		if(list == tlist)
		{
		  // DO Something...
		}
	    }
	}


	public void itemStateChanged(ItemEvent e)
	{
	    List list = (List)(e.getItemSelectable());
	    //DO something...
	}

	public void run(){
		System.out.println("Listening for messages from server . . . ");
		try{
	              myObject = new ChatMessage();
		      myObject.setName(tfuname.getText());
		      username = new String(tfuname.getText());
		      myOutputStream.writeObject(myObject);

			while(!receivingdone){
				myObject = (ChatMessage)myInputStream.readObject();          
				if( myObject.getMessage().equals("newuser") || myObject.getMessage().equals("olduser") )
				{
					tlist.add(myObject.getName());
				}
				else if( myObject.getMessage().equals("bye") && !(username.equals(myObject.getName())) )
				{
					System.out.println(username + " removing : " + myObject.getName());
					tlist.remove(myObject.getName());
				}
				else if( myObject.getMessage().equals("bye") && username.equals(myObject.getName()) )
								{
					System.out.println("Client : " + username + " has left the chatroom");
					receivingdone = true;
				}
				else
				{
					ta.append(myObject.getName() + "> " + myObject.getMessage() + "\n");
				}
			}
		}catch(IOException ioe){
			System.out.println("IOE: " + ioe.getMessage());
		}catch(ClassNotFoundException cnf){
			System.out.println(cnf.getMessage());
		}
		finally
		{
		    
		}
	}

	public static void main(String[] arg){
	
		Client c = new Client();

	}
}
