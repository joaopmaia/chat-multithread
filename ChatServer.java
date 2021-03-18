
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class ChatServer
{  public static void main(String[] args ) 
   {  
      ArrayList<ChatHandler> AllHandlers = new ArrayList<ChatHandler>();
		
      try 
      {  ServerSocket s = new ServerSocket(9118);
         
         for (;;)
         {  Socket incoming = s.accept( );
            new ChatHandler(incoming, AllHandlers).start();
         }   
      }
      catch (Exception e) 
      {  System.out.println(e);
      } 
   } 
}

class ChatHandler extends Thread
{  public ChatHandler(Socket i, ArrayList<ChatHandler> h) 
   { 
   		incoming = i;
		handlers = h;
		handlers.add(this);
		try{
			in = new ObjectInputStream(incoming.getInputStream());
			out = new ObjectOutputStream(incoming.getOutputStream());
		}catch(IOException ioe){
				System.out.println("Could not create streams.");
		}
   }


	public synchronized void sendCoordinates(){
	
		ChatHandler left = null;
		for(ChatHandler handler : handlers){

                        if(handler == this)
                        continue;

			ChatMessage cm = new ChatMessage();
			try{
				handler.out.writeObject(cm);
                        }
			catch(IOException ioe){
				//one of the other handlers hung up
				left = handler; // remove that handler from the arraylist
			}
		}
		handlers.remove(left);
        }

	public synchronized void broadcast(){
	
		ChatHandler left = null;
		for(ChatHandler handler : handlers){
			ChatMessage cm = new ChatMessage();
			cm.setName(uname);
			cm.setMessage(myObject.getMessage());
			try{
				handler.out.writeObject(cm);
                                if ( cm.getMessage().equals("bye")!= true )
                                {
                                  if(handler.appending)
                                  {
				    handler.aout.writeObject(cm);
                                  }
                                  else
                                  {
                                    handler.fout.writeObject(cm);
                                  }
                                }
                                
				System.out.println("Writing to handler outputstream: " + cm.getMessage());
				System.out.println("Thread: " + currentThread().toString());
			}catch(IOException ioe){
				//one of the other handlers hung up
				left = handler; // remove that handler from the arraylist
			}
		}
		handlers.remove(left);
		
		if(myObject.getMessage().equals("bye")){ // my client wants to leave
			done = true;	
			handlers.remove(this);
			System.out.println("Removed handler. Number of handlers: " + handlers.size());
		}
		System.out.println("Number of handlers: " + handlers.size());
   }

	public void adduser() throws IOException, ClassNotFoundException, EOFException, StreamCorruptedException{
           try {
		myObject = (ChatMessage)in.readObject();
		uname = new String(myObject.getName());
		System.out.println("Newuser: " + uname);


                File f = new File(uname + ".txt");
                if(f.exists()&&  !f.isDirectory())
                {
                   aout = new AppendingObjectOutputStream(new FileOutputStream(uname + ".txt", true));
                   appending = true;
                   System.out.println("Created AppendingObjectOutputStream");
                }
                else
                {
		    fout = new ObjectOutputStream(new FileOutputStream( uname + ".txt"));
                   System.out.println("Created ObjectOutputStream");
		}

                SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                Date today = Calendar.getInstance().getTime();
                String reportDate = df.format(today);
                
                ChatMessage cm = new ChatMessage();
                cm.setMessage(reportDate);
               
                if(this.appending)
                {
                    aout.writeObject(cm);
                }
                else
                {
                   fout.writeObject(cm);
                }
		
                fin = new ObjectInputStream(new FileInputStream( uname + ".txt"));
                
                ChatMessage mc;
 
		while(true)
                {
                    mc = (ChatMessage)fin.readObject();
		    this.out.writeObject(mc);
                    System.out.println("writing history message back to client\n");
		}
             }
             catch(EOFException eofe)
             {
                 System.out.println("EOF Exception occurred...");
                 fin.close();
             }
             catch(StreamCorruptedException sce)
             {
                System.out.println("Stream corrupted exception");
             }
   }

   public synchronized void announce() {
   		
		ChatHandler left = null;
		for(ChatHandler handler : handlers){
		       if(handler == this)
		       	  continue;
			ChatMessage cm = new ChatMessage();
			cm.setName(uname);
			cm.setMessage("newuser");
			try{
				handler.out.writeObject(cm);
				System.out.println("Writing to handler outputstream: " + cm.getMessage());
			}catch(IOException ioe){
				//one of the other handlers hung up
				left = handler; // remove that handler from the arraylist
			}
		}
		handlers.remove(left);
   }

   public synchronized void senduserlist() {
   
		ChatHandler left = null;
		for(ChatHandler handler : handlers){
		       if(handler == this)
		       	  continue;
			ChatMessage cm = new ChatMessage();
			cm.setName(handler.uname);
			cm.setMessage("olduser");
			try{
				this.out.writeObject(cm);
				System.out.println("Writing to handler outputstream: " + cm.getMessage());
			}catch(IOException ioe){
				//one of the other handlers hung up
				left = handler; // remove that handler from the arraylist
			}
		}
		handlers.remove(left);
   }

   public void run()
   {  
		try{ 
		        adduser();
			announce();
			senduserlist();

			while(!done){
				myObject = (ChatMessage)in.readObject();
                System.out.println("Message read: " + myObject.getMessage());
				broadcast();
                             
			}			    
		} catch (IOException e){  
			if(e.getMessage().equals("Connection reset")){
				System.out.println("A client terminated its connection.");
			}else{
				System.out.println("Problem receiving: " + e.getMessage());
			}
		}catch(ClassNotFoundException cnfe){
			System.out.println(cnfe.getMessage());
		}
		finally{
			handlers.remove(this);
		}
   }
   
   ChatMessage myObject = null;
   private Socket incoming;

   boolean done = false;
   ArrayList<ChatHandler> handlers;

   ObjectOutputStream out;
   ObjectInputStream in;

   String uname;   

   ObjectInputStream fin;
   ObjectOutputStream fout;
   AppendingObjectOutputStream aout;
   boolean appending = false;

   Integer count;
}

class AppendingObjectOutputStream extends ObjectOutputStream {

  public AppendingObjectOutputStream(OutputStream out) throws IOException{
    super(out);
  }

  @Override
  protected void writeStreamHeader() throws IOException {
    // do not write a header, but reset:
    reset();
  }

}

