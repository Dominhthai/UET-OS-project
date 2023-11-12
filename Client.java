import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    private Socket cliSocket; // socket 
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String cliID;

    public Client(Socket cliSocket, String cliID) {
        try {
        this.cliSocket = cliSocket; // Note: cliSocket here is server's port!!!
        this.cliID = cliID;
        this.bufferedReader = new BufferedReader(new InputStreamReader(cliSocket.getInputStream()));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(cliSocket.getOutputStream()));
        } catch(IOException e) {
            closeEverything(cliSocket, bufferedReader, bufferedWriter);
        }

    }

    public void sendMsg() {
        try {
            //send the client name
            bufferedWriter.write(cliID);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            //Input from keyboard the client msgs
            Scanner scanner = new Scanner(System.in);
            while(cliSocket.isConnected()) {
                //Input soure clientid(first), destination clientid (second) and msg(third), seperate by a space
                System.out.println("Send to: ");
                String msgToSend = "";
                //String msgToSend = scanner.nextLine();
                msgToSend += scanner.nextLine();
                bufferedWriter.write(cliID + " " + msgToSend); 
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }

        } catch (IOException e) {
            closeEverything(cliSocket, bufferedReader, bufferedWriter);
        }
    }

    /*Listen for msg sent back from a ClientHandler thread*/
    public void msgListener () {
        new Thread(new Runnable() { 
            @Override
            public void run() {
                String msgFromGroupChart;
                while(cliSocket.isConnected()) {
                    try {            
                        msgFromGroupChart = bufferedReader.readLine();
                        //if(msgFromGroupChart.toLowerCase().equals("quit")) cliSocket.close();
                        System.out.println(msgFromGroupChart);
                    } catch (IOException e) {
                        closeEverything(cliSocket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        
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

    public static void main(String[] args) throws IOException{

        String hostname;
        int port;
        if (args.length > 0) {
        hostname = args[0];
        port = Integer.parseInt(args[1]);
        }
        else { // default hostname and port
        hostname = "time.nist.gov";
        port = 13;
        }        

        //Input client ID
      Scanner scanner = new Scanner(System.in);
      System.out.println("Enter username/cliID: ");
      String username = scanner.nextLine();
      Socket socket = new Socket(hostname,port); // Server's port
      Client client = new Client(socket, username);
      client.msgListener();
      client.sendMsg();
    }
}
