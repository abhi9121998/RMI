package rmi_server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

public class RMI_server{
    private Socket socket;
    private ServerSocket serverSocket;
    private DataInputStream input;
    private DataOutputStream out;
    
    public RMI_server()
    {
        socket=null;
        serverSocket=null;
        input=null;
        out=null;
    }
    RMI_server(int port)
    {
        try
        {
            serverSocket=new ServerSocket(port);
            System.out.println("Server started");
            
            System.out.println("Waiting for a client");
            
            socket=serverSocket.accept();
            System.out.println("Client started");
            
            input=new DataInputStream(socket.getInputStream());
            out=new DataOutputStream(socket.getOutputStream());
        }
        catch(IOException i)
        {
            System.out.println("Exception : "+i);            
        }
    }
    
    void invoke(String message)
    {
        String[] message_array = message.split("!");
        String className = message_array[0];
	//String methodType = message_array[1];
	String methodName = message_array[2];
	int numOfargs = Integer.parseInt(message_array[3]);
	String error = "";
        
        try
        {
            Class<?> anonymous = Class.forName(className);
            Class<?> parameterTypes[]=null;
            Object parameterValue[]=null;
            
            if (numOfargs>0)
            {
		parameterTypes = new Class[numOfargs];
		parameterValue = new Object[numOfargs];
		int i = 4;
		int j = 0;
		while (i<message_array.length) 
                {
                    String type = message_array[i];
                    //System.out.println(type.equals("int"));
                    if(type.equals("int"))
                    {
                        parameterTypes[j] = int.class;
                        parameterValue[j] = Integer.parseInt(message_array[i + 1]);
                    }
                    else if(type.equals("float"))
                    {
                        parameterTypes[j] = float.class;
			parameterValue[j] = Float.parseFloat(message_array[i + 1]);
                    }
                    else if(type.equals("double"))
                    {
                        parameterTypes[j] = double.class;
                        parameterValue[j] = Double.parseDouble(message_array[i + 1]);
                    }
                    else if(type.equals("char"))
                    {
                        parameterTypes[j] = char.class;
			parameterValue[j] = message_array[i + 1].charAt(0);
                    }
                    else if(type.equals("String"))
                    {
                        parameterTypes[j] = String.class;
			parameterValue[j] = message_array[i + 1];
                    }
                    else
                    {
                        out.writeUTF("Method type is invalid.");
                    }
                    /*switch(type)
                    {
                        case "int" : {
                                        parameterTypes[j] = int.class;
                                        parameterValue[j] = Integer.parseInt(message_array[i + 1]);
                                    }
                        case "float" : {
                                        parameterTypes[j] = float.class;
					parameterValue[j] = Float.parseFloat(message_array[i + 1]);
                                      }
                        case "double" : {
                                        parameterTypes[j] = double.class;
                                        parameterValue[j] = Double.parseDouble(message_array[i + 1]);
                                       }
                        case "boolean" : {
                                        parameterTypes[j] = boolean.class;
					parameterValue[j] = Boolean.parseBoolean(message_array[i + 1]);
                                        }
                        case "char" :   {
					parameterTypes[j] = char.class;
					parameterValue[j] = message_array[i + 1].charAt(0);
                                       }
                        case "String" : {
                                        parameterTypes[j] = String.class;
					parameterValue[j] = message_array[i + 1];
                                       }
                        default : {out.writeUTF("Method type is invalid.");}
                    }*/
                    i+=2;
                    j++;
		}
            }
            Object obj = anonymous.newInstance();
	    Method method = obj.getClass().getMethod(methodName, parameterTypes);
	    out.writeUTF(method.invoke(obj, parameterValue) + "");
        }
        catch(IOException i)
        {
            error+="Invalid message format.";
        }
        catch(ClassNotFoundException c)
        {   
            error+="Class not found.";    
        }
        catch(NumberFormatException n)
        {
            error+="Number of arguments is not correct";
        }
        catch(IllegalAccessException i)
        {
            error+=i;
        }
        catch(InstantiationException i)
        {
            error+="class cannot be instantiatized";            
        }
        catch(NoSuchMethodException n)
        {
            error+= "no such method exists";
        }
        catch(InvocationTargetException i)
        {
            error+= "invalid agrument format";
        }
        if (error != "") 
        {
            try 
            {
		out.writeUTF(error);
            }
            catch(IOException e) 
            {
		System.out.println("error cannot be audited");
            }
	}
    }
    public static void main(String[] args) 
    {
        RMI_server server=new RMI_server(25);
        try
        {
            String message = server.input.readUTF();
            System.out.println("Received message : "+message);
            server.invoke(message);
        }
        catch(IOException i)
        {
            System.out.println("Exception : "+ i);
        }
    }   
}