import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Server extends JFrame{
	
	ServerSocket ss ;
	ArrayList<Socket> s ;					
	ArrayList<HandleAPlayerClient> hapc;	//list of all the handleAPlayerClient
	ArrayList<Player> playerDataBase;		//list of all the player in the player database
	ArrayList<Player> playingPlayers;		//list of all the players in this game
	int startedGameCount;					//count the number of player who wish to start the game
	int playerCount;						

	ObjectOutputStream output;
	ObjectInputStream input;
	
	/* some constants for the strings to be sent*/
	final String gameDataBaseConst = "PlayerDatabase";
	final String p1_NewPlayerConst = "CreateNewPlayer";
	final String p1_ExistingPlayerConst = "SelectFromExistedPlayers";
	final String p2_ConfirmConst = "ConfirmNewPlayer";
	final String sendNewPlayerConst = "SendNewPlayer";
	final String p3_SelectConst = "selectingPlayer";
	final String showStatsConst ="stats";
	final String myPlayerReady = "ready";
	final String allReady = "allReady";
	final String newPlayerAssignIndex = "newPlayerAssignIndex";
	
	
	int restartPlayer;
	Deck deckOnServer;			//save the deck on server so all the window can use the same deck
	
	int currentPlayer;
	int selectedPlayer;
	int numberOfAI;				//number of AI
	int playerNumberConst;		//number of Human player
	
	public Server(){
		
		ss = null;
		s = new ArrayList<Socket>();
		Scanner consoleInput = new Scanner(System.in);
		hapc = new ArrayList<HandleAPlayerClient>();
		playerDataBase = new ArrayList<Player>() ;
		playingPlayers = new ArrayList<Player>();
		deckOnServer = new Deck(true);
		restartPlayer = 0;
		readGame();		//read from database at the beginning
		
		
		try {
			writeGame();		
		} catch (IOException e) {
			
			 System.out.println("got an exception: line 47"+ e.getMessage());
		}
		
		System.out.println("Please enter the number of player(s): ");
		playerNumberConst=consoleInput.nextInt();
		numberOfAI=4-playerNumberConst;
	
		try{
			ss = new ServerSocket(9660);			//my port number is 9660
			
		}
		catch(Exception e)
		{
			 System.out.println("got an exception: line 59"+ e.getMessage());
			System.exit(0);
		}
		
		for(int i = 0; i < playerNumberConst;i++)		
		{
			try{
				Socket newSocket = ss.accept();		//try to connect with playerNumberConst window
				s.add(newSocket);
				HandleAPlayerClient newHandler = new HandleAPlayerClient(newSocket,this);
				hapc.add(newHandler);
				new Thread(newHandler).start();
								
			}
			catch (Exception e) {
				 System.out.println("got an exception: line 75"+ e.getMessage());
				}
			System.out.println( "got a connection"+i );
		}
		
	}
	public void readGame(){		//read from a file and update the datas to the local playerDatabase
		
		File tempFile = new File("game.dat");
		if(tempFile.exists())	
			{
			try {
				input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(tempFile)));

	            //Construct the ObjectInputStream object
	        	Player obj = null;
	            while ((obj = (Player)input.readObject()) != null) {
	            
	            			obj.setSelected(false);
	                		playerDataBase.add(obj);	                    
	                }

	            input.close();
	                        
	        } catch (EOFException ex) { //This exception will be caught when EOF is reached
	            System.out.println("End of file reached.");
	        } catch (FileNotFoundException e) {

	        	 System.out.println("got an exception: line 113"+ e.getMessage());
			} catch (IOException e) {

				 System.out.println("got an exception: line 116"+ e.getMessage());
			} catch (ClassNotFoundException e) {

				 System.out.println("got an exception: line 119"+ e.getMessage());
			} 
			}

	}
	
	public void writeGame() throws IOException{
		
		File tempFile = new File("game.dat");
		try {
			output = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(tempFile)));
		} catch (FileNotFoundException e1) {

			System.out.println("got an exception: line 126"+ e1.getMessage());
		} catch (IOException e1) {

			System.out.println("got an exception: line 129"+ e1.getMessage());
		}
		
		 try {
	            
	            //Construct the ObjectInputStream object
	      for(int i=0;i<playerDataBase.size();i++)
	      {
	    	  output.writeObject(playerDataBase.get(i));
	    	
	      }
	                        
	        } catch (Exception ex) { //This exception will be caught when EOF is reached
	            System.out.println("Error with output");
	        } 
		 output.close();
	}
	
	public void printPlayer(){		//print player's info
		
		for(int i = 0; i<playerDataBase.size(); i++)
		{
			System.out.print(playerDataBase.get(i).getName()+ "  ");
			System.out.print(playerDataBase.get(i).getGameWon()+"  ");
			System.out.println(playerDataBase.get(i).getGameLost());
		}
	}
	
	public static void main( String[] args ) {
		Server server = new Server();
		}	


	class HandleAPlayerClient implements Runnable {
		Socket mySocket;
		ObjectOutputStream oos;
		ObjectInputStream ois;
		
		Server myGameServer;
		boolean startedGame = false;
		Player myPlayer;

		public HandleAPlayerClient( Socket s, Server gs ) {
			mySocket = s;
			myGameServer = gs;
		}

	public void run() {
			try {

						//initialize input and output streams
				oos = new ObjectOutputStream(mySocket.getOutputStream());
				ois = new ObjectInputStream(mySocket.getInputStream());
				} catch (Exception e) {
					System.exit(0);
				}

				try {
					oos.writeObject("Deck");	//write the deck to the client at the very beginning
					oos.writeObject(deckOnServer);
				} catch (IOException e1) {
					
					e1.printStackTrace();
					System.out.println("Fail to send cards to player");
				}
				
				
			while( true ) {
				try {
					//Wait for the client to send a request
					String message = (String)ois.readObject();
					
					System.out.println("message is:" + message );
					
					if(message.equals(newPlayerAssignIndex))	//if the client ask for an index, return the index to it
					{
						oos.writeObject(newPlayerAssignIndex);
						oos.writeInt(hapc.size()-1); ///super not sure!!!!
						
					}
					else if ( message.equals(p1_NewPlayerConst)) {		//if the player want to create new player, send back the up-to-date database to it
						oos.writeObject(p1_NewPlayerConst);
						oos.reset();

						oos.writeObject(playerDataBase);
						oos.reset();
					}
					else  if(message.equals(p1_ExistingPlayerConst))	//if the player want to select an existed player, send back the up-to-date databse to it
					{
						oos.writeObject(p1_ExistingPlayerConst);
						oos.reset();
						oos.writeObject(playerDataBase);
						oos.reset();
						
					}
					else if(message.equals(p2_ConfirmConst))		//if one confirm to create a new player, send back the database to help the player do the last minute check
					{
						oos.writeObject(p2_ConfirmConst);
						oos.reset();
						oos.writeObject(playerDataBase);
						oos.reset();
						
					}
					else if(message.equals(sendNewPlayerConst))		// add the newly created player to local database and write it to the file
					{
						Player newPlayer = (Player)ois.readObject();
						playerDataBase.add(newPlayer);
						writeGame();
						newPlayer.setSelected(true);
						myPlayer = newPlayer;
						playingPlayers.add(myPlayer);
						
					}
					else if(message.equals(p3_SelectConst))			//if someone select an existed player, return its index in the database
					{
						int tempIndex=ois.readInt();
						playerDataBase.get(tempIndex).setSelected(true);
						myPlayer = playerDataBase.get(tempIndex);
						playingPlayers.add(myPlayer);
						writeGame();
						
						oos.writeObject(p3_SelectConst);
						oos.reset();
						oos.writeInt(tempIndex);
						oos.reset();
						
						
						
					}
					else if(message.equals(showStatsConst))		// if someone want to see the stats for all players, send the latest player database
					{
						oos.writeObject(showStatsConst);
						oos.reset();
						oos.writeObject(playerDataBase);
						oos.reset();
					}
					
					else if(message.equals(myPlayerReady))	//if someone is ready to play a game, check if everyone is ready and send back the signal of start if every player is ready
					{
						startedGameCount++;
						
						if(startedGameCount == playerNumberConst)
						{
							restartPlayer =0;
							System.out.println("Everyone is ready!!!!!");
							
							for(int i=0;i<hapc.size();i++)
							{
								hapc.get(i).oos.writeObject(allReady);
								hapc.get(i).oos.reset();
								hapc.get(i).oos.writeObject(playingPlayers);
								hapc.get(i).oos.reset();
								hapc.get(i).oos.writeInt(playerNumberConst);
								hapc.get(i).oos.reset();
							}
						}
					}
					else if(message.equals("SelectedPlayer"))		//send the selected player (from current player's window) to every other client
					{	
						selectedPlayer=ois.readInt();
						for(int i=0;i<hapc.size();i++)
						{
							hapc.get(i).oos.writeObject("SelectedPlayer");
							hapc.get(i).oos.reset();
							hapc.get(i).oos.writeInt(selectedPlayer);
							hapc.get(i).oos.reset();
						}
					}
					else if(message.equals("sHasIt"))		//if selected player has the card the current player wants, send back its location
					{
						int temp = ois.readInt();
	
						for(int i=0;i<hapc.size();i++)
						{
							hapc.get(i).oos.writeObject("sHasIt");
							hapc.get(i).oos.reset();
							hapc.get(i).oos.writeInt(temp);
							hapc.get(i).oos.reset();
						}
					}
					else if(message.equals("doesntHaveIt"))		//if selected player does not have the current player wants, send back its location
					{
						int temp = ois.readInt();
						
						for(int i=0;i<hapc.size();i++)
						{
							hapc.get(i).oos.writeObject("doesntHaveIt");
							hapc.get(i).oos.reset();
							hapc.get(i).oos.writeInt(temp);
							hapc.get(i).oos.reset();
							
						}
					}
					else if(message.equals("UpdateDataBase"))		//if the client side made some changes and update to the local database
					{
						playerDataBase = (ArrayList<Player>)ois.readObject();
					}
					else if(message.equals("MessageFromAI"))		//if AI selects a player and a rank, send it to other player!!!
					{
						int tempPlayer = ois.readInt();
						int tempRank = ois.readInt();
						
						for(int i=0;i<hapc.size();i++)
						{
							hapc.get(i).oos.writeObject("AISelectedPlayer");
							hapc.get(i).oos.reset();
							hapc.get(i).oos.writeInt(tempPlayer);
							hapc.get(i).oos.reset();
							hapc.get(i).oos.writeInt(tempRank);
							hapc.get(i).oos.reset();
						}
		
					}
					else if(message.equals("SaveGame"))		// if the client want to save database, save it
					{	
						writeGame();
					}
					else if(message.equals("ReadyToRestart"))		// if one player is ready to restart, see if other players are ready to reastart and restart teh game
					{
						restartPlayer++;
						
						if(restartPlayer==playerNumberConst)
						{
							
							deckOnServer = new Deck(true);
							
							for(int i=0;i<playerNumberConst;i++)
							{
								hapc.get(i).oos.writeObject("AllRestart");
								hapc.get(i).oos.reset();
								hapc.get(i).oos.writeObject(deckOnServer);
								hapc.get(i).oos.reset();
								
								
							}
							restartPlayer=0;
						}
					
							
					}
					else if(message.equals("Exit"))			// if one player wants to exit, send the "Exit" info to all players
					{
						for(int i=0;i<playerNumberConst;i++)
						{
							hapc.get(i).oos.writeObject("AllExit");
							hapc.get(i).oos.reset();
						}
					}
					else if(message.equals("AlreadyClosed"))	// if the client is closed, close the streams
					{
						oos.close();
						ois.close();
						break;
					}
					else
					{
						System.out.println("There's no match!!!");
					}

				} catch (Exception e) {
					 System.out.println("got an exception: line 268"+ e.getMessage());
				}

			}//loop ends
			
			int counter =0;
			for(int i=0; i<playerNumberConst; i++)		// if all the handlers are closed, close the serversocket
			{
				if(hapc.get(i).mySocket.isClosed())
					counter++;			
			}
			if(counter==playerNumberConst)
				{
					try {
						ss.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.exit(0);
				}
				
		}//function ends
	}

 }
