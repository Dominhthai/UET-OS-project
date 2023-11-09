import java.io.*;
import java.net.*;

public class Server {

    ServerSocket serverSocket;
    Socket socket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {

            while (true) {
                socket = serverSocket.accept();//Since now, var socket is the server socket
                Thread multicli = new Thread(new ClientHandler(socket)); // create a new stream for each client connection, associated with 1 only server port
                multicli.start();
                /* 
                // Establish input and output streams
                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                // Receive command from client
                String command = br.readLine();
                if (!command.equals("DOWNLOAD")) {
                    System.out.println("Invalid command received.");
                    socket.close();
                    continue;
                }

                // Receive file name from client
                String filename = br.readLine();
                File file = new File(filename);
                if (!file.exists()) {
                    // Send error message to client if file does not exist
                    bw.write("ERROR\n");
                    bw.flush();
                    socket.close();
                    continue;
                }

                // Send file to client
                bw.write(filename + " " + file.length() + "\n");
                bw.flush();
                byte[] fileData = new byte[(int)file.length()];
                FileInputStream fis = new FileInputStream(file);
                fis.read(fileData);
                fis.close();
                os.write(fileData);
                os.flush();

                // Wait for end of session notification from client
                String endOfSession = br.readLine();
                if (!endOfSession.equals("END")) {
                    System.out.println("Invalid end of session notification received.");
                }

                // Close connection
                socket.close();
                */
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeServer(){
        try {

            if (serverSocket != null) {
                serverSocket.close();
            }

            if (socket != null) {
                socket.close();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        int port = 1234;//DEFAULT PORT
        if (args.length > 0) {
            try {
            port = Integer.parseInt(args[0]);
            if (port < 0 || port >= 65536) {
            System.out.println("Port must between 0 and 65535");
            return;
            }
            }
            catch (NumberFormatException ex) {
            // use default port
            }

        ServerSocket serverSocket = new ServerSocket(port); // assign port
        System.out.println("Server listening on port " + port + " ...");
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
}
