import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

//this class is meant to support class Server in term of taking up the receiving and responding clients
//Play role as a "swmall" server
class ClientHandler implements Runnable {

    //List include the clients
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    private Socket socket; // socket passed from server class
    private BufferedReader br;
    private BufferedWriter bw;
    private String userID;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.userID = br.readLine(); // the first msg from the client would be the user name

            //add to list
            clientHandlers.add(this);

            //Verify the login by userID successful
            broadcastMsg ("Server: " + userID + " has entered the chat\n");
           System.out.println("Server: " + userID + " is online\n");
            
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    //Handdle the msg from clients
    //send msg to certain client
    public void run() {
        String msgFromClient;
        
        while (socket.isConnected()) {
              try {
            msgFromClient  = br.readLine();
            broadcastMsgClient(msgFromClient);
        } catch (IOException | NullPointerException e) {
            removeClientHandler();
            closeEverything(socket, br, bw);
            break;
        }
        }
        
        removeClientHandler();
    }

    //Used for login handle
    public void broadcastMsg(String msg) {
        for (ClientHandler clientHandlers : clientHandlers) { 
            try {
                //send verified connection msg to required clients
                //String[]arrayMsg = msg.split(" ");// the String msg is not 'purified'(include both msg + userID) 
                if (!clientHandlers.userID.equals(userID)) {
                        clientHandlers.bw.write(msg);
                        clientHandlers.bw.newLine();
                        clientHandlers.bw.flush();
                }
            } catch (IOException | NullPointerException e) {
                closeEverything(socket, br, bw);
            }
        }
    }

    //Used for send mesg to specific client
    //First broadcast msg to all clients, then check for the expected client
    public void broadcastMsgClient(String msg) {
    	//send verified connection msg to required clients
        String[]arrayMsg = msg.split(" ", 3);/*The String msg is not 'purified'(include both 
        					source(1)+destination userID(2) and msg(3)) */
        
    	//Check the destination userID if exist or not
    	int is_exist = 0;
        for (ClientHandler clientHandlers : clientHandlers) { 
            try {
                //String array 3 parameters
                if (clientHandlers.userID.equals(arrayMsg[1])) {
                    //Write msg to that specific client
                        clientHandlers.bw.write("Msg sent from " + arrayMsg[0] + ": " + arrayMsg[2]);
                        clientHandlers.bw.newLine();
                        clientHandlers.bw.flush();
                        is_exist ++;
                }
                //Server Remove one client if requested					
        	else if(arrayMsg[1].toLowerCase().equals("server")||
        		arrayMsg[2].toLowerCase().equals("quit"))
        	{
        		clientHandlers.bw.write(arrayMsg[2]);
                        clientHandlers.bw.newLine();
                        clientHandlers.bw.flush();
        		removeClientHandler();
        	}
            } catch (IOException e) {
                closeEverything(socket, br, bw);
            }
        }
        
        //destination clientID not exist
        if(is_exist == 0)
        {
        	for(ClientHandler clientHandlers : clientHandlers)
        	{
        		try 
        		{
        			if (clientHandlers.userID.equals(arrayMsg[0]))
        			{
        			clientHandlers.bw.write("No client named " + arrayMsg[1] + " ever existing!");
                		clientHandlers.bw.newLine();
                		clientHandlers.bw.flush();
        			}
        		} catch (IOException e) {
                		closeEverything(socket, br, bw);
            		}
        		
        	}
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMsg("Server: " + userID + " has left the chat");
        System.out.println("Server: " + userID + " is offline");
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {

        removeClientHandler();
        
        try {
             if (socket != null) {
            socket.close();
        }
        if (bufferedReader  != null) {
            bufferedReader.close();
        }
        if (bufferedWriter != null) {
            bufferedWriter.close();
        }
         } catch (IOException e) {
            e.printStackTrace();
         }
       
    }
}
