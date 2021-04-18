//import com.sun.security.ntlm.Server;

import java.io.*;
import java.net.*;
import java.nio.Buffer;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;
import javax.xml.crypto.Data;

public class ftpserver extends Thread {
        private Socket connectionSocket;
        int port;
        int count = 1;
        int turn = 1;
        char disc = 'R';
        char[][] board = new char[6][7];

        public ftpserver(Socket connectionSocket) {
            this.connectionSocket = connectionSocket;
        }


        public void run() {
            if (count == 1)
                System.out.println("User connected" + connectionSocket.getInetAddress());
            count++;

            try {
                processRequest();

            } catch (Exception e) {
                System.out.println(e);
            }

        }

        private void processRequest() throws Exception {
            String fromClient;
            String clientCommand;
            String clientFileName;
            byte[] data;
            String frstln;

            while (true) {
                if (count == 1)
                    System.out.println("User connected" + connectionSocket.getInetAddress());
                count++;

                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

                fromClient = inFromClient.readLine();

                System.out.println(fromClient);
                StringTokenizer tokens = new StringTokenizer(fromClient);

                frstln = tokens.nextToken();
                port = Integer.parseInt(frstln);
                clientCommand = tokens.nextToken();
                System.out.println(clientCommand);

		
		        if (clientCommand.equals("QUIT")){
			        connectionSocket.close();
			        System.out.println("\nConnection is closed.");
			        break;
			
		        }
		        else
                {
                    placeDiscAt(parseInt(clientCommand));

                    //updates client with current board
                    Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                    ObjectOutputStream out = new ObjectOutputStream(dataSocket.getOutputStream());
                    out.writeObject(board);

                    checkWinner(parseInt(clientCommand));
                    if (isBoardFull()){
                        System.out.println("\n Board is full - Game is a tie");
                    }
                    prepareNextTurn();
                }
            }
        }
        private initBoard(){
                for (int row = 0; row < board.length; row++){
                    for (int col = 0; col < board[0].length; col++){
                        board[row][col] = ' ';
                    }
                }
        }
        private void placeDiscAt(col){
                if (!isValidMove(col)){
                    return;
                }
                int row = findRow(col);

                board[row][col] = disc;
        }
        private void prepareNextTurn(){
                if (turn == 1){
                    turn = 2;
                }
                else {
                    turn = 1;
                }
                if (disc == 'R'){
                    disc = 'Y';
                }
                else {
                    disc = 'R';
                }
        }
        private boolean isBoardFull(){
                for (int row = 0; row < board.length; row++)
                {
                    for (int col = 0; col < board[0].length; col++)
                    {
                        if (board[row][col] == ' ')
                        {
                            return false;
                        }
                    }
                }
                return true;
        }
        //Do not change disc before checking winner
        private char checkWinner(col){
                int rowMove = 0;
                int colMove = 0;
                int row = findRow(col);

                for (int i = -1; i < 2; i++){
                    for (int j = -1; j < 2; j++){
                        if (row + i < 0 || row + i >= board.length || col + j < 0 || col + j >= board[0].length || (i == 0 && j == 0)){
                            continue;
                        }
                        if (board[row + i][col + j] == disc)
                        {
                            rowMove = row + i;
                            colMove = col + j;

                            if (rowMove + i < 0 || rowMove + i >= board.length || colMove + j < 0 || colMove + j >= board[0].length)
                            {
                                continue;
                            }

                            if (board[rowMove + i][colMove + j] == disc)
                            {
                                rowMove += i;
                                colMove += j;

                                if (rowMove + i < 0 || rowMove + i >= board.length || colMove + j < 0 || colMove + j >= board[0].length)
                                {
                                    continue;
                                }

                                if (board[rowMove + i][colMove + j] == disc)
                                {
                                    return disc;
                                }
                            }
                        }
                    }
                }
                return ' ';


        }
        private int findRow(col){
                for (int row = 0; row < board.length; row++){
                    if (board[row][col] != ' '){
                        return row - 1;
                    }
                }
        }
        private boolean isValidMove(col){
                if (findRow(col) < 0){
                    return false;
                }
                return true;
        }

}
	

