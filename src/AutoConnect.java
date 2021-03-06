import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpResponse;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.net.ServerSocket;
import java.net.BindException;
import java.io.IOException;

public class AutoConnect {
	
	private static ServerSocket listenerSocket1;
	private static ServerSocket listenerSocket2;


	public static void main(String[] args) {
		
		Long 	INTVAL  	= 9*60*1000L;
		Long 	STOP    	= 60*1000L;
		Long 	RESTART		= 3*3600*1000L;
		String 	userName 	= "";
		String 	userPwd  	= "";
		Date 	startTime	= new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		int listenerSocketFlagNum = 0;

		/* Onlyone instance */
		try{
		     listenerSocket1 = new ServerSocket(20004);
		} catch(BindException e) {
		      listenerSocketFlagNum = 1;
	    } catch(final IOException e) {
		      System.exit(1);
	    }
	    try{
	    	listenerSocket2 = new ServerSocket(30004);
	    }catch (BindException e) {
	    	listenerSocketFlagNum = 2;
	    }catch (IOException e) {
	    	System.exit(1);
	    }
	    if(listenerSocketFlagNum == 2){
       		System.err.println("A previous instance is already running....");
       		System.exit(1);

    	}

		try{
			if(args.length == 2){
				System.out.println(dateFormat.format(new Date())+ " Initing...");
				Log(dateFormat.format(new Date())+ " Initing...");
				userName = args[0];
				userPwd  = args[1];
				//System.out.println(userName + ": " + userPwd);
			}
			else{
				System.out.println("Error: no user account");
				Log("Error: no user account");
				return;
			}
			Call autocall = new Call(userName,userPwd);
			int op = 0;

			/* main process */
			while(true){
				switch (op) {
					case 0: {// request the login page
						if(autocall.requestLogin()){
							System.out.println("Msg: "+ dateFormat.format(new Date()) +" requestLogin successfully, start logining");
							Log("Msg: "+ dateFormat.format(new Date()) +" requestLogin successfully, start logining");
							op = 1;
							
							break;
						}
						else{// request failed
							
							// log
							System.out.println("Error: "+ dateFormat.format(new Date()) +" requestLogin failed");
							Log("Error: "+ dateFormat.format(new Date()) +" requestLogin failed");
							
							op = 3;
							//test
							//System.out.println(op);
							break;
						}
					}
					case 1:{// post login forms
						if (autocall.startLogin()) {// if login successfully,go to case 2
							startTime = new Date();
							System.out.println("Msg: "+ dateFormat.format(new Date()) +" startLogin successfully");
							Log("Msg: "+ dateFormat.format(new Date()) +" startLogin successfully");
							op = 2;
							
							Thread.sleep(INTVAL);
							break;
						}
						else{// login failed

							// log
							System.out.println("Error: "+ dateFormat.format(new Date()) +" startLogin failed");
							Log("Error: "+ dateFormat.format(new Date()) +" startLogin failed");
							op = 3;
							
							break;
						}
					}
					case 2:{// heartbeats
						if(autocall.heartBeats()){
							System.out.println("Msg: "+ dateFormat.format(new Date()) +" heartBeats successfully");
							Log("Msg: "+ dateFormat.format(new Date()) +" heartBeats successfully");
							
							Thread.sleep(INTVAL);
							
							op = 2;
							break;
						}
						else{
							// log 
							System.out.println("Error: "+ dateFormat.format(new Date()) +"heartBeats failed");
							Log("Error: "+ dateFormat.format(new Date()) +" heartBeats failed");
							
							op = 3;
							break;
						}
						
					}
					case 3: {// sleep for one minute
						System.out.println("Msg: "+ dateFormat.format(new Date()) +" stop for 1 min");
						Log("Msg: "+ dateFormat.format(new Date()) +" stop for 1 min");
						Thread.sleep(STOP);
						op = 0;
						
						break;
					}
				}

				if( ((new Date()).getTime()-startTime.getTime()) > RESTART ){
					startTime = new Date();
					if(autocall.heartBeats() == true){
						autocall.requestLogout();
						autocall = null;
						autocall = new Call(userName,userPwd);
						op = 0;
					}
				}
			}

		}catch (Exception e) {
			e.printStackTrace();
			return ;
		}
	}

	/* take a log */
	static void Log(String logStr) throws Exception {
		FileWriter fw = new FileWriter("AutoConnect.log",true);
		fw.write(logStr+"\n");
		fw.close();
	}
}
