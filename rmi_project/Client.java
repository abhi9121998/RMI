package rmi_project;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client 
{
    
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream out;
    private String message;

    public Client() 
    {
        socket=null;
        input=null;
        out=null;
        message="";
    }
    
    
    Client(String address, int port)
    {
        this();
        try
        {
            socket = new Socket(address, port);
            
            out=new DataOutputStream(socket.getOutputStream());
            input = new DataInputStream(socket.getInputStream());
            System.out.println("Connected");
        }
        catch(UnknownHostException e)
        {
            System.out.println("Error : "+e);
        }
        catch(IOException e)
        {
            System.out.println("Error : "+e);
        }
    }
    
    private void getInput()
    {
        Scanner s=new Scanner(System.in);;
        System.out.print("Enter class name : ");
        message+=s.next()+"!";
        System.out.print("Enter return type of method : ");
        message+=s.next()+"!";
        System.out.print("Enter name of method : ");
        message+=s.next()+"!";
        System.out.print("Enter number of parameters of method : ");
        int no_param=s.nextInt();
	message+=no_param+"!";
        
        while(no_param>0) 
        {
            System.out.print("Enter type of argument : ");
            String argument = s.next();

            System.out.print("Enter value of argument : ");
            String value = s.next();

            message+=argument+"!"+value+"!";
            no_param--;
	}
        //message+="rmi_server.Helper!int!addition!2!int!2!int!2!";
    }
    
    public static void main(String[] args)
    {
        Client RP = new Client("127.0.0.1", 25);
        
        try
        {
            RP.getInput();
            RP.out.writeUTF(RP.message);
            System.out.println("Answer : " + RP.input.readUTF());
        }
        catch(IOException e)
        {
            System.out.println("Exception : "+e);
        }
    }
}