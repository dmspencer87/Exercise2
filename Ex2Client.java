/************************************************************************************
 *	file: ChatClient.java
 *	author: Daniel Spencer
 *	class: CS 380 - computer networks
 *
 *	assignment: Exercise 2
 *	date last modified: 10/12/2017
 *
 *	purpose: To use java.util.zep.CRC32 to generate an error code for 100 bytes
 *
 *
 ************************************************************************************/
import java.io.*;
import java.util.zip.CRC32;
import java.net.Socket;

public class Ex2Client{

    public static void main(String[] args) throws IOException{
        int[] fullByte  = new int[100];
        byte[] byteArray = new byte[100];

        try (Socket socket = new Socket("18.221.102.182", 38102)) {
            System.out.println("Connected to server.");
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            PrintStream out = new PrintStream(socket.getOutputStream());

            System.out.println("Received byte:");
            for(int index = 0; index < fullByte.length; index++){
                int firstHalf = in.read();
                int secondHalf = in.read();

                fullByte[index] = (firstHalf<<4)|secondHalf;
                System.out.printf("%02X", fullByte[index]);
                //System.out.printf("%X", Integer.parseInt(fullByte[i], 2));
                if((index+1)%10==0){
                    System.out.println();
                }
                byteArray[index] = (byte)fullByte[index];
                //byteArray[i] = (byte) Integer.parseInt(fullByte[i], 2);
            }
            CRC32 crc = new CRC32();
            crc.update(byteArray);
            long err = crc.getValue();
            System.out.printf("Generated CRC32: %08X.\n", err);
            byte[] bytes = new byte[4];

            for(int index = bytes.length-1; index >= 0; --index){
                long ones = 255;
                bytes[index] = (byte)(err & ones);
                err>>=8;
            }
            out.write(bytes);

            if(in.read() == 1){
                System.out.println("Response good.");
            }

        }
        System.out.println("Disconnected from server.");
    }

}