/*methods:
 * public Game() {
 * public void initialize()
 * public void drawFromDeck(Player p)
 * public void p1ToP2(int selectedRank,Player p1,Player p2)	//move Card with selectedRank from p1 to p2
 * boolean ifPHasIt(Player p, int selectedRank)
 * public boolean goFish(Player p,int selectedRank)
 * public void p1SelectCardFromP2(Player p1, Player p2)
 * public int convertToRank(char temp)
 * public void checkCollection(Player p)
 * 	public void gameStart()
 * public void checkIfWin()
 * 	public void printInfo()
 * */


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.io.*;
import java.net.Socket;



public class Game extends JFrame implements ActionListener{

	/*Menu GUI***/
	JMenuBar menuBar;
	JMenu generalMenu, playerMenu;
	JMenuItem startGame,stopGame,exitApplication,createPlayer,listPlayers;
	
	/**Socket and Streams**/
	Socket mySocket;	//socket of the thread of this game
	Scanner s;			
	ObjectOutputStream oos;		//write to server
	ObjectInputStream ois;		//receive message from server
	
	/**List of players , panels and player datas**/
	ArrayList<PlayerPanel> playerPanel;			// This is the array of the 4 panel in each game. when the game is started, its length should be always 4
	ArrayList<Player> playerDataBase;			//The database of all players, including game lost and game won, read from the server
	ArrayList<Player> playerList;				// a list of human players in the current game

	/***basic preparation****/
	Deck unusedDeck;	// deck of unused card
	Deck completedCollection;	//deck of completed card
	boolean win;		
	boolean winner[];			//0 if winner is player0, 1 if winner is player1...-1 for a draw
	
	private Timer timer;
	PlayerPanel currentPlayer;	//It is currentPlayer's turn of the game	
	PlayerPanel selectedPlayer;	//It is selected by currentPlayer and will be asked if it has some cards
	PlayerPanel myPlayer;		//The player who is using this window
	int myLocation;				//the location of myPlayer's playerPanel in the window
	Font fontB = new Font("SansSerif",Font.PLAIN,30);
	Font fontS = new Font("SansSerif",Font.PLAIN,20);
	
	/***GUI**/
	JPanel gamePanel;	//a panel contains all the game prosess
	JPanel centralPanel;	//the centerpart of a game panel
	JPanel leftPanel;		//the left part in the center panel show the graphics and label of cards left
	JLabel cardsRemained;	//a label showing cards remained
	JPanel datas;		//a in the middle of central panel that  contains the text area and process
	CardsInDeckPanel cardsInDeckPanel;	//in left panel
	ArrayList<JLabel> names;			//form a form with sets and put in  datas Panel
	ArrayList<JLabel> sets;				//records corresponding completed sets of a give player
	JTextArea gameRecords;				//text areas to record the game records
	JPanel textPanel;					//on the right and contains textarea
	JLabel textTitle;					//in text panel, function as the title
	
	JPanel bigMiddlePanel;
	JLabel datasTitle;	
	JScrollPane textScrollBar;
	
	JPanel initialPage;
	JPanel createPlayerPanel;	//create player panel, can go to game
	JPanel displayStatsPanel;	//display user stats
	JPanel selectPlayerPanel;	//as user choos four from database, provide the option to add new. can go to game
	JPanel newGamePanel;
	JPanel loadGamePanel;

	CardLayout cardlayout;
	
	/****initialPage****/
	JButton p0_NewGame;		
	JPanel p0_Buttons;
	JLabel welcomeImageLabel;
	ImageIcon welcomeImage;
	/*******newPlayerPanel*******/

	JLabel p1_Name;
	JButton p1_NewPlayer;
	JButton p1_ExistingPlayer;
	JButton p1_Start;
	JPanel p1_Buttons;
	JPanel p1_LeftPanel;
	JPanel p1_Panel;
	/******creaetPlayerPanel*****/
	JLabel p2_Instruction;
	JTextField p2_TextField;
	JButton p2_Confirm;
	JPanel p2_BottomPanel;
	JPanel p2_Panel;

	/********selectPlayerPanel**********/
	JScrollPane p3_ScrollPane;
	JPanel p3_Tables;
	JButton p3_GoToP1;
	ArrayList<JPanel> p3_Rows;
	ArrayList<JLabel> p3_Names;
	ArrayList<JButton> p3_Select;
	
	/*******displayStatsPanel*******/
	JScrollPane p4_ScrollPane;
	JPanel p4_Tables;
	JButton p4_BackToP2;
	ArrayList<JPanel> p4_Rows;
	ArrayList<JLabel> p4_Names;
	ArrayList<JLabel> p4_GameWon;
	ArrayList<JLabel> p4_GameLost;
	JButton p4_GoToGame;
	
	
	final String gameDataBaseConst = "PlayerDatabase";
	final String p1_NewPlayerConst = "CreateNewPlayer";
	final String p1_ExistingPlayerConst = "SelectFromExistedPlayers";
	final String p2_ConfirmConst = "ConfirmNewPlayer";
	final String sendNewPlayerConst = "SendNewPlayer";
	final String p3_SelectConst = "selectingPlayer";
	final String showStatsConst ="stats";
	final String myPlayerReady = "ready";
	final String allReady = "allReady";
	String newPlayerAssignIndex = "newPlayerAssignIndex";
	/*********loadGamePanel***********/	
	JLabel p5_instruction;
	JLabel waitingImageLabel;
	ImageIcon waitingImage;
	/*********Game General*/

	boolean ifStarts;
	int DataBaseIndex;
	int numberOfAI;
	int playerNumberConst;
	Timer AITimer;
	boolean timerStarted;
	
	public Game() throws ClassNotFoundException, IOException {
		
		setSize(1000,750);	
		initializeJFrame();
		intializeSocket();
		System.out.println("finish initialization of socket");
		AITimer = new Timer(3000,this);
		timerStarted=false;
		initialize();
			
	}
	
	 public void swapView() {		//go to the next page in this card layout
		 
		 cardlayout.next(this.getContentPane());
		 validate();
	 }
	 
	 void initializeJFrame() {		//initialize the menuBar and window (was form Application in version 2)
		 
		 	setTitle("go fish"); 	
			menuBar = new JMenuBar();		//initialize menu
			setJMenuBar(menuBar);
			generalMenu = new JMenu("General");
			playerMenu = new JMenu("Player");
			menuBar.add(generalMenu);
			menuBar.add(playerMenu);
			startGame = new JMenuItem("Start game");
			stopGame = new JMenuItem("Restart game");

			exitApplication = new JMenuItem("Exit");
			createPlayer = new JMenuItem("Create a player");
			listPlayers = new JMenuItem("List all players");
			
			generalMenu.add(startGame);
			generalMenu.add(stopGame);
			generalMenu.add(exitApplication);
			playerMenu.add(createPlayer);
			playerMenu.add(listPlayers);
		
			startGame.setEnabled(false);
			stopGame.setEnabled(false);
			exitApplication.setEnabled(false);
			
			startGame.addActionListener(this);
			stopGame.addActionListener(this);

			exitApplication.addActionListener(this);
			createPlayer.addActionListener(this);
			listPlayers.addActionListener(this);
	 }
	 
	 void intializeSocket()			//initialize basics about networking including input stream and output stream
	 {
		 try{	 
			 	mySocket = new Socket("localhost", 9660);
			 	System.out.println("got a connection");
		 
			 	oos = new ObjectOutputStream(mySocket.getOutputStream());
			 	System.out.println("got outputStreams");
			 	ois = new ObjectInputStream(mySocket.getInputStream());
				 
			 	System.out.println("got Streams");
		 } 
		 catch(Exception e){
			 	System.out.println("got an exception: line 219"+ e.getMessage());
		 }
		 
		try {
				oos.writeObject(newPlayerAssignIndex);
				oos.reset();
		} 
		catch (IOException e) {
	
			 	System.out.println("got an exception: line 225"+ e.getMessage());

		}
		 
	 }
	 
	 void initializeLoadGamePanel(){		//New page in this version. This page will be displayed when the player is waiting for others to start the game
		 
		 waitingImage = new ImageIcon("cards/wait.jpeg");
		 waitingImageLabel = new JLabel(waitingImage);
		 p5_instruction = new JLabel("     Waiting for other players to be connected! =D");
		 loadGamePanel.add(waitingImageLabel);
		 loadGamePanel.add(p5_instruction);
		}
	 
	 void initializeInitialPage(){	//initialize the welcome page, the first panel in the cardlayout
		
		p0_NewGame = new JButton("New Game");
		p0_NewGame.addActionListener(this);

		welcomeImage = new ImageIcon("cards/welcome.jpeg");
		welcomeImageLabel = new JLabel(welcomeImage);

		p0_Buttons = new JPanel();
		p0_Buttons.setLayout(new BoxLayout(p0_Buttons,BoxLayout.X_AXIS));
		p0_Buttons.add(p0_NewGame);
		initialPage.add(welcomeImageLabel);
		initialPage.add(p0_Buttons);
		
		
	}
	void initializeNewGamePanel(){  //initialize the new Game Panel, the second panel in the cardLayout

		p1_Name = new JLabel();
		p1_Name=new JLabel("         ");

		p1_LeftPanel = new JPanel();
		p1_LeftPanel.add(p1_Name);
		p1_NewPlayer = new JButton("Create New Player");
		p1_ExistingPlayer = new JButton("Add an Existing Player");
		p1_Start = new JButton("Start Game");
		p1_NewPlayer.addActionListener(this);
		p1_ExistingPlayer.addActionListener(this);
		p1_Start.addActionListener(this);
		p1_Buttons = new JPanel();
		p1_Buttons.setLayout(new BoxLayout(p1_Buttons,BoxLayout.Y_AXIS));
		p1_Buttons.add(p1_NewPlayer);
		p1_Buttons.add(p1_ExistingPlayer);
		p1_Buttons.add(p1_Start);
		p1_Panel = new JPanel();
		p1_Panel.setLayout(new BoxLayout(p1_Panel,BoxLayout.X_AXIS));
		p1_Panel.add(p1_LeftPanel);
		p1_Panel.add(p1_Buttons);

		newGamePanel.add(p1_Panel);
	}
	
	void initializeCreatePlayerPanel(){ //initialize the panel for creating new player, the third panel in the cardLayout
		
		p2_Instruction = new JLabel("Please enter the name of the new player and hit the enter button");
		p2_TextField = new JTextField("Name");
		p2_Confirm = new JButton("Confirm");
		p2_Confirm.addActionListener(this);
		p2_BottomPanel = new JPanel();
		p2_BottomPanel.setLayout(new GridLayout(1,2));
		p2_BottomPanel.add(p2_TextField);
		p2_BottomPanel.add(p2_Confirm);
		p2_Panel = new JPanel();
		p2_Panel.setLayout(new BoxLayout(p2_Panel,BoxLayout.Y_AXIS));
		p2_Panel.add(p2_Instruction);
		p2_Panel.add(p2_BottomPanel);
		createPlayerPanel.setLayout(new FlowLayout());
		createPlayerPanel.add(p2_Panel);
		
	}
	
	void initializeSelectPlayerPanel(){  //initialize the panel for select existed User, the forth panel in the cardlayout
	
		p3_Tables = new JPanel();
		p3_Tables.setLayout(new BoxLayout(p3_Tables,BoxLayout.Y_AXIS));
		p3_GoToP1 = new JButton("back");
		p3_GoToP1.addActionListener(this);
		p3_Rows = new ArrayList<JPanel>();
		p3_Names = new ArrayList<JLabel>();
		p3_Select = new ArrayList<JButton>();

		for(int i = 0; i < playerDataBase.size(); i++){
			
			p3_Names.add(new JLabel(playerDataBase.get(i).getName()));
			p3_Select.add(new JButton("  Select  "));
			
			if(playerDataBase.get(i).getSelected())
				p3_Select.get(i).setEnabled(false);
			else
				p3_Select.get(i).setEnabled(true);
			
			p3_Select.get(i).addActionListener(this);
			p3_Rows.add(new JPanel());
			p3_Rows.get(i).setLayout(new GridLayout(1,2));
			p3_Rows.get(i).add(p3_Names.get(i));
			p3_Rows.get(i).add(p3_Select.get(i));
			p3_Tables.add(p3_Rows.get(i));
		}
		p3_ScrollPane = new JScrollPane(p3_Tables,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		p3_ScrollPane.setPreferredSize(new Dimension(200,300));
		selectPlayerPanel.add(p3_ScrollPane);
		selectPlayerPanel.add(p3_GoToP1);
	}
	
	void initializedisplayStatsPanel(){		//display the stats (game won or lost) of all the players in the playerDatabase
		p4_GoToGame = new JButton("Back to Game");
		p4_GoToGame.addActionListener(this);
		p4_Tables = new JPanel();
		p4_Tables.setLayout(new BoxLayout(p4_Tables,BoxLayout.Y_AXIS));
		p4_Rows = new	ArrayList<JPanel> ();
		p4_Names = new ArrayList<JLabel> ();
		p4_GameWon = new ArrayList<JLabel>() ;
		p4_GameLost = new ArrayList<JLabel>();
		p4_Names.add(new JLabel("  Names  "));
		p4_GameWon.add(new JLabel("   Game Won  "));
		p4_GameLost.add(new JLabel("  Game Lost  "));
		p4_Rows.add(new JPanel());
		p4_Rows.get(0).setLayout(new GridLayout(1,3));
		p4_Rows.get(0).add(p4_Names.get(0));
		p4_Rows.get(0).add(p4_GameWon.get(0));
		p4_Rows.get(0).add(p4_GameLost.get(0));
		p4_Tables.add(p4_Rows.get(0));
		for(int i = 0; i < playerDataBase.size(); i++)
		{
			p4_Names.add(new JLabel(playerDataBase.get(i).getName()));
			p4_GameWon.add(new JLabel("  "+playerDataBase.get(i).getGameWon()+"  "));
			p4_GameLost.add(new JLabel("  "+playerDataBase.get(i).getGameLost()+"  "));
			p4_Rows.add(new JPanel());
			p4_Rows.get(i+1).setLayout(new GridLayout(1,3));
			p4_Rows.get(i+1).add(p4_Names.get(i+1));
			p4_Rows.get(i+1).add(p4_GameWon.get(i+1));
			p4_Rows.get(i+1).add(p4_GameLost.get(i+1));
			p4_Tables.add(p4_Rows.get(i+1));
		}


		p4_ScrollPane = new JScrollPane(p4_Tables,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		p4_BackToP2 = new JButton("Back");
		p4_BackToP2.addActionListener(this);
		p4_ScrollPane.setPreferredSize(new Dimension(300,300));
		displayStatsPanel.add(p4_ScrollPane);
		displayStatsPanel.add(p4_BackToP2);
		displayStatsPanel.add(p4_GoToGame);
		p4_GoToGame.setEnabled(false);
		p4_BackToP2.setEnabled(true);
	
	}
	
	
	public void initialize() throws ClassNotFoundException,IOException{		//initialize all the datas needed for a new game
		
		ifStarts=false;
		playerDataBase = new ArrayList<Player>();
		playerList = new ArrayList<Player>();
			
		timer=new Timer(500,this);	
		gamePanel = new JPanel();
		initialPage = new JPanel();	//page 0
		createPlayerPanel = new JPanel();	//page 2,create player panel, can go to game
		displayStatsPanel = new JPanel();	//page 4,display user stats
		selectPlayerPanel = new JPanel();	//page 3,as user choos four from database, provide the option to add new. can go to game
		newGamePanel = new JPanel();	//page 1
		loadGamePanel = new JPanel();
		playerPanel = new ArrayList<PlayerPanel>();	

		cardlayout = new CardLayout();
		this.setLayout(cardlayout);
		
		add(initialPage,"initial");//,"initial"
		initializeInitialPage();	
		add(newGamePanel,"newGame");//,"newGame"
		initializeNewGamePanel();
		add(createPlayerPanel,"createPlayer");//,"createPlayer"
		initializeCreatePlayerPanel();
		add(selectPlayerPanel,"selectedPlayer");//,"selectedPlayer"
		initializeSelectPlayerPanel();
		add(displayStatsPanel,"displayStats");//,"displayStats"
		initializedisplayStatsPanel();
		add(loadGamePanel,"loadGame");//,"loadGame"
		initializeLoadGamePanel();		
		add(gamePanel,"gamePanel");
		initializeGamePanel();
		
		validate();
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(1200,730);
		setLocationRelativeTo(null);
		setVisible(true);
		setBackground(new Color(0,139,0));
		}
	public void initializeGamePanel()		//initialize the game panel
	{
		winner = new boolean[4];
		for(int i = 0;i < 4;i++)
		{
			winner[i] = false;			
		}
		
		win = false;
		unusedDeck = new Deck(true);
		completedCollection = new Deck();
		cardsRemained = new JLabel("Cards remained: "+unusedDeck.getSize());
		centralPanel = new JPanel();
		datas = new JPanel();
		textPanel = new JPanel();
		leftPanel = new JPanel();		
		cardsInDeckPanel = new CardsInDeckPanel();
		datasTitle = new JLabel("Completed sets");
		names = new ArrayList<JLabel>();
		sets = new ArrayList<JLabel>();
		textTitle = new JLabel("Game records");
		gameRecords = new JTextArea();
		
		gameRecords.setEditable(false);
		gameRecords.setLineWrap(true);
		gameRecords.setWrapStyleWord(true);
		printToRecords("Hi!!Welcome to Mingyu's Go Fish Game! Have Fun!\n\n");
		
		datas.setPreferredSize(new Dimension(130,100));
		
		textScrollBar = new JScrollPane(gameRecords,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		gameRecords.select(gameRecords.getDocument().getLength(),gameRecords.getDocument().getLength());
		
		for(int i = 0; i < 4; i++)
		{
			names.add(new JLabel("      "));
			sets.add(new JLabel("0"));
			
		}
		
		centralPanel.setLayout(new GridLayout(1,3));
		centralPanel.add(datas);
		centralPanel.add(leftPanel);
		centralPanel.add(textPanel);
		leftPanel.setLayout(new BoxLayout(leftPanel,BoxLayout.Y_AXIS));
		leftPanel.add(cardsRemained);
		leftPanel.add(cardsInDeckPanel);
		datas.setLayout(new GridLayout(9,1));
		datas.add(datasTitle);
		for(int i = 0 ; i < 4 ; i++)
		{
			datas.add(names.get(i));
			datas.add(sets.get(i));
		}
		datas.setPreferredSize(new Dimension(150,300));
		datas.setMinimumSize(new Dimension(150,300));
		datas.setMaximumSize(new Dimension(150,300));
		textPanel.setLayout(new BoxLayout(textPanel,BoxLayout.Y_AXIS));
		textPanel.add(textTitle);
		textPanel.add(textScrollBar);
		textPanel.setPreferredSize(new Dimension(200,300));
		textPanel.setMaximumSize(new Dimension(200,300));
		textPanel.setMinimumSize(new Dimension(200,300));
		gamePanel.setLayout(new BoxLayout(gamePanel,BoxLayout.X_AXIS));
		bigMiddlePanel = new JPanel();
		bigMiddlePanel.setLayout(new BoxLayout(bigMiddlePanel,BoxLayout.Y_AXIS));
	}
	
	
	public void myRun()			//call this when game starts, receiving all the messages from server
	{
		while(true)
		{
			try {
				String message = (String)ois.readObject();	
				System.out.println("Client got a message: "+message);
				
				if(message.equals("Deck"))	//server would send a deck (size: 52)
				{
					unusedDeck = (Deck)ois.readObject();
				}
				
				else if(message.equals(newPlayerAssignIndex))	//assign the index of my player of this window
				{
					myLocation = ois.readInt();
				}
				else if(message.equals(p1_NewPlayerConst))		//update playerDatabase when the user want to create a new player
				{
					playerDataBase = (ArrayList<Player>)ois.readObject();				
					cardlayout.show(this.getContentPane(), "createPlayer");
					validate();
				}
				else if(message.equals(p1_ExistingPlayerConst))	//update playerDatabase when the user want to select an existed player
				{
					playerDataBase = (ArrayList<Player>)ois.readObject();	
					destroySelectPlayerPanel();
					initializeSelectPlayerPanel();//add more code here to prevent 2 select same players
					cardlayout.show(this.getContentPane(),"selectedPlayer");	
					validate();
				}
				else if(message.equals(p2_ConfirmConst))	//receive the new user's name and create this player, also check if the name already exists
				{
					playerDataBase = (ArrayList<Player>)ois.readObject();	
					
					if(IfNewUser(p2_TextField.getText()))
					{
						myPlayer =new PlayerPanel(selectImageName(myLocation),p2_TextField.getText(),myLocation,true,this);
						System.out.println("The player's name is "+ myPlayer.getPlayer().getName());
						oos.writeObject(sendNewPlayerConst);
						oos.reset();
						oos.writeObject(myPlayer.getPlayer());
						oos.reset();
						myPlayer.getPlayer().setSelected(true);
						playerDataBase.add(myPlayer.getPlayer());
						p1_Name.setText(p2_TextField.getText());
						cardlayout.previous(this.getContentPane());
						validate();
						p1_NewPlayer.setEnabled(false);
						p1_ExistingPlayer.setEnabled(false);
						p1_Start.setEnabled(true);
						createPlayer.setEnabled(false);
						startGame.setEnabled(true);
						
					}
					else
					{
						p2_TextField.setText("The user already exists");
						p2_TextField.selectAll();
					}
				}
				else if(message.equals(p3_SelectConst))	//if the user select an existed player, get its info from the database according to the index received
				{
					DataBaseIndex = ois.readInt();
					playerDataBase.get(DataBaseIndex).setSelected(true);
					myPlayer =new PlayerPanel(selectImageName(myLocation),playerDataBase.get(DataBaseIndex),myLocation,true,this);
					System.out.println("The player's name is "+ myPlayer.getPlayer().getName());
					p1_Name.setText(playerDataBase.get(DataBaseIndex).getName());
					cardlayout.show(this.getContentPane(),"newGame");
					validate();

						p1_NewPlayer.setEnabled(false);
						p1_ExistingPlayer.setEnabled(false);
						p1_Start.setEnabled(true);
						createPlayer.setEnabled(false);
						startGame.setEnabled(true);
					
					
				}
				else if(message.equals(showStatsConst))		//show the players' stats when receive a message from the server
				{
					playerDataBase = (ArrayList<Player>)(ois.readObject());
					destroyDisplayStatsPanel();
					initializedisplayStatsPanel();
					goToStats();
				}
				else if(message.equals(allReady))		//if receiving a signal of "all ready" ,initialize the arraylist of playerpanel and  start the game
				{
					
					playerList=(ArrayList<Player>)ois.readObject();
					playerNumberConst = ois.readInt();
					numberOfAI = 4-playerNumberConst;
				    
					System.out.println("Yeah!!! everyone is ready!!!");
					for(int i = 0 ; i < playerNumberConst ; i++)	/*TODO maybe 4 need to be changed when we need AI*/
					{
						PlayerPanel tempPlayerPanel = new PlayerPanel(selectImageName(i),playerList.get(i),i,true,this);
						playerPanel.add(tempPlayerPanel);
					}
					for(int i = 0 ; i<numberOfAI;i++)
					{
						PlayerPanel tempPlayerPanel = new PlayerPanel(selectImageName(playerNumberConst+i),new String("Computer "+(i+1)),playerNumberConst+i,false,this);
						playerPanel.add(tempPlayerPanel);
					}
					myPlayer = playerPanel.get(myLocation);
					
					initializeBeforeGame();
					cardlayout.show(this.getContentPane(),"gamePanel");
					
					
					
				}
				else if(message.equals("SelectedPlayer"))	// choose the correct selected plaeyr according to the index from server
				{
					int tempSelectedPlayerLocation=ois.readInt();
					selectedPlayer = playerPanel.get(tempSelectedPlayerLocation); 
				}
				else if(message.equals("sHasIt"))	//move the card from the selected player to current player if the selected player has the card that current player wants
				{
					
					int temp = ois.readInt();
					printToRecords("\n\n"+currentPlayer.getPlayer().getName()+": "+selectedPlayer.getPlayer().getName()+", do you have a "+ Card.getRankString(temp)+" ?");
					printToRecords("\n\n"+selectedPlayer.getPlayer().getName()+": Oh I do! Here you go!");
					currentPlayerDrawFromP1(selectedPlayer.getPlayer(),temp);
					
					// in myRun in eachGame, printToRecords(selectedPlayer.getPlayer().getName()+" has the card you want!\n\n"
					//call currentPlayerDrawFromP1(selectedPlayer.getPlayer(),rank)
						if(myPlayer.getPlayer().getName().equals(currentPlayer.getPlayer().getName())){
							showRankButtons();
						}
				}
				else if(message.equals("doesntHaveIt"))	//if selected player does not have the card, let the current player go fish
				{	int temp = ois.readInt();
					
						printToRecords("\n\n"+currentPlayer.getPlayer().getName()+": "+selectedPlayer.getPlayer().getName()+", do you have a "+ Card.getRankString(temp)+" ?");
						printToRecords("\n\n"+selectedPlayer.getPlayer().getName()+": No I don't. "+currentPlayer.getPlayer().getName()+",  go Fish!");
						if(goFish(currentPlayer.getPlayer(), temp))
							{
								if(myPlayer.getPlayer().getName().equals(currentPlayer.getPlayer().getName()))
									showRankButtons();
							}
						else
							{
								goToTheNextUser();
								disableButtons();
							}
						
						if(!win)
						{
							checkCollection(currentPlayer.getPlayer());
							checkIfWin();
						}					
					//in myRun in eachGame, read "doentHaveIt", then read rank;
					//printToRecords(selectedPlayer.getPlayer().getName()+" does not have the card you want!Please go fish now\n\n"
					//then call goFish(currentPlayer.getPlayer(),rank)
				}
				else if(message.equals("AISelectedPlayer"))	//if AI is the current player, choose the selected player and rank based on the two integers it receivers from the server
				{
					int tempSelectedPlayerLocation=ois.readInt();
					int tempRank = ois.readInt();
					selectedPlayer = playerPanel.get(tempSelectedPlayerLocation); 
					if (ifPHasIt(selectedPlayer.getPlayer(),tempRank))
					{
						printToRecords("\n\n"+currentPlayer.getPlayer().getName()+": "+selectedPlayer.getPlayer().getName()+", do you have a "+ Card.getRankString(tempRank)+" ?");
						printToRecords("\n\n"+selectedPlayer.getPlayer().getName()+": Oh I do! Here you go!");
						currentPlayerDrawFromP1(selectedPlayer.getPlayer(),tempRank);
						if (myLocation ==0)
						{
							if(!win)
								{
								if(!timerStarted)			
									AITimer.start();
								else
									AITimer.restart();
								}
						}
						if(!win)
							printToRecords("\n\n"+currentPlayer.getPlayer().getName()+", Please choose a character to select cards from\n");
					}
					else
					{
						printToRecords("\n\n"+currentPlayer.getPlayer().getName()+": "+selectedPlayer.getPlayer().getName()+", do you have a "+ Card.getRankString(tempRank)+" ?");
						printToRecords("\n\n"+selectedPlayer.getPlayer().getName()+": No I don't. "+currentPlayer.getPlayer().getName()+",  go Fish!");
						if(goFish(currentPlayer.getPlayer(), tempRank))
							{
							if (myLocation ==0)
									{
										if(!win)
										{		
											if(!timerStarted)
												AITimer.start();
											else
												AITimer.restart();
										}
									}
							if(!win)
								printToRecords("\n\n"+currentPlayer.getPlayer().getName()+", Please choose a character to select cards from\n");
								
							}
						else
							{
								goToTheNextUser();
								disableButtons();
							}
						checkCollection(currentPlayer.getPlayer());
						if(!win)
							checkIfWin();
					
						
					}
				}
				else if(message.equals("AllRestart"))			// if everyone is ready to restart, initialize some datas and restarts
				{
						if(playerPanel.size()==4)
						{
						gamePanel.remove(playerPanel.get(2));
						gamePanel.remove(playerPanel.get(0));
						gamePanel.remove(bigMiddlePanel);
						
						}
					 	
					playerPanel=null;
					playerPanel=new ArrayList<PlayerPanel>();
					
					for(int i = 0 ; i < 4 ; i++)	
					{
						PlayerPanel tempPlayerPanel;
						
						if(i<playerNumberConst)
						{
							tempPlayerPanel = new PlayerPanel(selectImageName(i),playerList.get(i),i,true,this);
							playerList.get(i).initialize(i);
						}
						else
							tempPlayerPanel = new PlayerPanel(selectImageName(i),new String("Computer "+(i+1-playerNumberConst)),i,false,this);
						playerPanel.add(tempPlayerPanel);
					}
					
					myPlayer = playerPanel.get(myLocation);
					initializeGamePanel();
					unusedDeck = null;
					unusedDeck = (Deck)ois.readObject();
					
					initializeBeforeGame();
					cardlayout.show(this.getContentPane(),"gamePanel");
					validate();						

					
				}
				else if(message.equals("AllExit"))		//if all players need to exit
				{
					oos.writeObject("AlreadyClosed");
					ois.close();
					System.exit(0);
				}
				else
				{
					System.out.println("There's no match!!!");
				}
				
				
			} catch (IOException e) {
				
				 System.out.println("got an exception: line 603"+ e.getMessage());
			} catch (ClassNotFoundException e) {
				
				 System.out.println("got an exception: line 606"+ e.getMessage());			
			}
			
		}
	}
	
	public void destroyDisplayStatsPanel()			//destroy the player panel when a game is stop
	{
		displayStatsPanel.remove(p4_ScrollPane);
		displayStatsPanel.remove(p4_BackToP2);
		displayStatsPanel.remove(p4_GoToGame);		
	}
	
	public void destroySelectPlayerPanel()			//destroy the panel for selecting an existed player
	{
		selectPlayerPanel.remove(p3_ScrollPane);
		selectPlayerPanel.remove(p3_GoToP1);
	}
	
	public void initializeBeforeGame()		//initialization right before the game 
	{
	
		currentPlayer=playerPanel.get(0);	//initial player is player0
		
		for(int i = 0; i < 5; i++){					//distribute  5 cards to each player
			for(int j = 0; j < playerPanel.size(); j++)
				drawFromDeck(playerPanel.get(j).getPlayer());
		}
		
		for(int i = 0; i < playerPanel.size(); i++)			//check if someone already complete a collection
		{
			checkCollection(playerPanel.get(i).getPlayer());
			playerPanel.get(i).getPlayer().sortCards();
		}
		
		for(int i = 0; i < playerPanel.size(); i++)		//repaint the cards in each player's hands
		{
			playerPanel.get(i).repaint();
		}
		
		for(int i = 0 ;i < 4; i++)						
		{	
			names.get(i).setText("  "+playerPanel.get(i).getPlayer().getName() + "  ");
			
		}
		
		bigMiddlePanel.add(playerPanel.get(1));
		bigMiddlePanel.add(centralPanel);
		bigMiddlePanel.add(playerPanel.get(3));
		gamePanel.add(playerPanel.get(2));
		gamePanel.add(bigMiddlePanel);
		gamePanel.add(playerPanel.get(0));
		cardsInDeckPanel.repaint();
		disableButtons();
		if(!win)
			printToRecords(currentPlayer.getPlayer().getName()+", Please choose a character to select cards from\n\n");
		timer.start();		
		for(int i = 0; i < 4; i++)
			playerPanel.get(i).enableIcon(true);
		
		stopGame.setEnabled(false);
		exitApplication.setEnabled(false);
		ifStarts = true;
		printToRecords("Current Player "+ myPlayer.getPlayer().getName());
		
	}
	
	public void drawFromDeck(Player p){			//get the top card from the unused deck and push it to the player p
		p.addCard(unusedDeck.takeTopCard());
		cardsRemained.setText("Cards remained: "+unusedDeck.getSize());
	}
	
	public void p1ToP2(int selectedRank,Player p1,Player p2){	//move Card with selectedRank from p1 to p2
	
		for(int i = 0; i < p1.getDeck().getSize(); i++){
			if(p1.getDeck().getCard(i).getRank()==selectedRank){
				p1.getDeck().getCard(i).setFaceUp(false);
				p2.addCard(p1.removeCard(i));
				i--;
			}
			
		}		
		p2.sortCards();
		
	}
	
	public void currentPlayerDrawFromP1(Player p1, int selectedRank)//move the card(s) with selected rank from p1 to current player
	{
		p1ToP2(selectedRank,p1,currentPlayer.getPlayer());
		checkCollection(currentPlayer.getPlayer());
	}
	
	public void setSelectedPlayer(PlayerPanel player)	//set the selected player to "player"
	{
		selectedPlayer = player;
	}
	
	public PlayerPanel getSelectedPlayer()
	{
		return(selectedPlayer);
	}
	
	public PlayerPanel getCurrentPlayer()
	{
		return(currentPlayer);
	}
	
	public void showRankButtons()			//show all the rank buttons
	{
		currentPlayer.showButtons();
	}
	
	public void disableButtons()	//disable all the buttons
	{
		for(int i = 0; i < playerPanel.size(); i++)
		{
			
				playerPanel.get(i).enableButtons(false);
		}
	}
	
	public void printToRecords(String string)		//print the string to the text area
	{
		gameRecords.append(string);	
		gameRecords.select(gameRecords.getDocument().getLength(),gameRecords.getDocument().getLength());

	}
	
	public void addSets(Player p1)		//add the player's number of completed set by 1 
	{
		sets.get(p1.getPanelLocation()).setText(""+p1.getCompletedSet());
	}
	
	public void goToTheNextUser()		//select the next player when current player's turn is over
	{		
		currentPlayer = selectedPlayer;		
		if(currentPlayer.getPanelLocation()>=playerNumberConst)		//if current player is an AI
			{
				if(myLocation==0)									
				{				
					if(!win)
					{
						if(!timerStarted)
							AITimer.start();					//start timer to mimic the process of thinking and do the later tasks in actionPerformed
						else
							AITimer.restart();
					}
				}
			}

		if(!win)
			printToRecords(currentPlayer.getPlayer().getName()+", Please choose a character to select cards from\n");
		
	}
	
	boolean ifPHasIt(Player p, int selectedRank){			//return true if player p has card(s) with the selected rank
		if(selectedRank==0) return false;
		if(p.getDeck().getRankList()[selectedRank-1]==true)
			return true;
		else
			return false;
	}
	
	public boolean goFish(Player p,int selectedRank){		//let player p go fish, return true if the card he/she has got the car he.she wants

		drawFromDeck(p);
		
		p.getDeck().getLastCard().setFaceUp(false); //TODO change from true to false
		
		if(p.getDeck().getLastCard().getRank()==selectedRank){
			printToRecords("\n\n"+currentPlayer.getPlayer().getName()+": OMG I am so lucky! I got the fish! ("+selectedRank+")");
			checkCollection(p);
			p.sortCards();
			return true;
		}
		else{
			printToRecords("\n\n"+currentPlayer.getPlayer().getName()+": Oh no, I did not get the fish...");
			printToRecords("\n\nIt's now "+selectedPlayer.getPlayer().getName()+"'s turn.");
			checkCollection(p);
			p.sortCards();
			return false;
		}
	}

	
	public void checkCollection(Player p){		//check all the card in player p's deck to see if he/she has completed any sets
		int currentNumber=p.getDeck().getCard(0).getRank();
		int sameTypeNumber=1;
		
		for(int i=1;i<p.getDeck().getSize();i++){	
			if (p.getDeck().getCard(i).getRank()==currentNumber){
					sameTypeNumber++;
			}
			else{
				sameTypeNumber=1;
			}	
				
			currentNumber=p.getDeck().getCard(i).getRank();
			
			
			if(sameTypeNumber==4){
					p.addCompletedSet();
					for(int j=0;j<4;j++){
						
						p.getDeck().getCard(i-3).setCompleted(true);
						completedCollection.addACard(p.getDeck().remove(i-3));
					
					}
					addSets(p);
					printToRecords(p.getName()+"completed a set of "+Card.getRankString(currentNumber)+"\n\n");
					sameTypeNumber=1;
					i=i-4;	
					
				}
			
		}
		if(!win)
			checkIfWin();
		
	}

	public void checkIfWin(){		//check if someone has win the game and set the winner
		
		boolean flag=false;
		
		for(int i = 0; i < 4; i++)
		{		
			flag=flag||(playerPanel.get(i).getPlayer().getDeck().getSize()==0);
		}
		
		if(unusedDeck.getSize()==0||flag){	//if some one has no card in hand or there's no card in the deck
			win = true;						//That means the game is over
			int temp = 0;
			for(int i = 0; i < 4; i++)
					playerPanel.get(i).enableIcon(false);		//disable all the buttons
			
			for(int i = 1; i < 4; i++)		
			{	
				if(playerPanel.get(i).getPlayer().getCompletedSet()>playerPanel.get(temp).getPlayer().getCompletedSet())
					temp = i;
			}			
			for(int i = 0;i<playerPanel.size();i++)
			{
				if(playerPanel.get(i).getPlayer().getCompletedSet()==playerPanel.get(temp).getPlayer().getCompletedSet())
					winner[i] = true;
			}
		
			disableButtons();			
			stopGame.setEnabled(true);					//only after a round of game is finished, the player can exit or restart
			exitApplication.setEnabled(true);

			if(winner[0]&&winner[1]&&winner[2]&&winner[3])
			{
				printToRecords("Wooow,we have a draw\n");
			}
			else{	
				printToRecords("Congratulations to our winners: ");
					for(int i = 0; i < 4 ; i++)
					{
						if(winner[i]==true)
						{	
							printToRecords(playerPanel.get(i).getPlayer().getName()+". ");
							if(i<playerNumberConst)
							{
								playerPanel.get(i).getPlayer().addGameWon();
								for(int j=0;j<playerDataBase.size();j++)		//update the winner's info to local database
									if	(playerPanel.get(i).getPlayer().getName().equals(playerDataBase.get(j).getName()))
									{
										playerDataBase.get(j).addGameWon();
									}
							}
						}
						else{
							if(i<playerNumberConst)	 
							{
								playerPanel.get(i).getPlayer().addGameLost();
								for(int j=0;j<playerDataBase.size();j++)		//update the loser's info to local database
								if	(playerPanel.get(i).getPlayer().getName().equals(playerDataBase.get(j).getName()))
									{
										playerDataBase.get(j).addGameLost();	
									}
									
								}
						}
		
					}
					
					if(myPlayer.getPanelLocation()==0)				//only update the database file once when myPlayer's index is 0
					{	
						try{
							System.out.println("Someone's writing to the file!!!");
							oos.writeObject("UpdateDataBase");
							oos.reset();
							oos.writeObject(playerDataBase);
							oos.reset();
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
						try {
							oos.writeObject("SaveGame");
							oos.reset();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
		
					printToRecords("\n");
			}
		}
		else
			win = false;
		
		
	}


	public boolean IfNewUser(String name)	//to check if the "name" is already used as a player's name
	{
		for(int i = 0 ; i<playerDataBase.size();i++)
		{
			if(name.equals(playerDataBase.get(i).getName()))
			{
				
				return false;
			}
		}
		return true;
	}
	public void actionPerformed (ActionEvent arg0) {
		
		repaint();
		
		p4_GoToGame.setEnabled(ifStarts);
		p4_BackToP2.setEnabled(!ifStarts);
		
		if(myPlayer == null){
			
			p1_Start.setEnabled(false);
		}
		else{
			
			p1_Start.setEnabled(true);
		}
	
		try{
			
			if(arg0.getSource()==AITimer)			// mimic the selection of players and ranks for the AI,1) generate random player index and card rank 2) send these data to server
		{
			int index = currentPlayer.generateRandomPlayerIndex();
			int rank = currentPlayer.generateRandomRank()+1;
			oos.writeObject("MessageFromAI");
			oos.reset();
			oos.writeInt(index);
			oos.reset();
			oos.writeInt(rank);
			oos.reset();
			AITimer.stop();
		}
		else if(p0_NewGame == arg0.getSource()){//if the player select "new game" display corresponding panel
			cardlayout.show(this.getContentPane(), "newGame");
			validate();
		}

		else if(p1_NewPlayer==arg0.getSource()){	//if the player select to add a new Player to the game, show the panel for adding new player
			oos.writeObject(p1_NewPlayerConst);
			oos.reset();
		}
		else if(p1_ExistingPlayer==arg0.getSource()){	// if the player select to add an existed player to the game, show the panel for select existed player
			oos.writeObject(p1_ExistingPlayerConst);
			oos.reset();				
		}
		else if(p1_Start==arg0.getSource()){	// if the player is able to select the start button, show the game panel and begin the game
			oos.writeObject(myPlayerReady);
			oos.reset();
			cardlayout.show(this.getContentPane(),"loadGame");
			validate();
			startGame.setEnabled(false);
				
		}
		else if(p2_Confirm==arg0.getSource()){	//if the player clicked confirm, add the new player based on the given name from textfield
			
			oos.writeObject(p2_ConfirmConst);
			oos.reset();
			oos.writeObject(new String(p2_TextField.getText()));			
			oos.reset();
			
		}
		
		else if(arg0.getSource()==p4_BackToP2)		//go from page 4 to page 2
		{
			cardlayout.show(this.getContentPane(), "newGame");
			startGame.setEnabled(myPlayer!=null);
				
		}
		
		else if(arg0.getSource()==p4_GoToGame)	//go from page 4 to game
		{
			
			cardlayout.show(this.getContentPane(), "gamePanel");
			startGame.setEnabled(false);
			validate();
		}
		
		else if(createPlayer==arg0.getSource())		
			goToNewPlayer();
		
		else if(exitApplication==arg0.getSource())
			try {
				exitGame();
			} catch (IOException e2) {
				 System.out.println("got an exception: line 983"+ e2.getMessage());

			}
		
		else if(stopGame==arg0.getSource()){		//means restart game in this new version
			try {
				oos.writeObject("ReadyToRestart");
				oos.reset();
				cardlayout.show(this.getContentPane(),"loadGame");
				validate();
				startGame.setEnabled(false);
				stopGame.setEnabled(false);
				exitApplication.setEnabled(false);
	
			} catch (Exception e1) {
				 System.out.println("got an exception: line 998"+ e1.getMessage());

			}
		}
		else if(arg0.getSource()==startGame)
		{
			oos.writeObject(myPlayerReady);
			oos.reset();
			cardlayout.show(this.getContentPane(),"loadGame");
			validate();
			startGame.setEnabled(false);
		}
		else if(listPlayers==arg0.getSource())
		{
			try{
			oos.writeObject(showStatsConst);
			oos.reset();
			}
			catch(Exception e){
				System.out.println("listPlayers exception");
			}
	
		}
		else if(arg0.getSource()==p3_GoToP1)
		{
			cardlayout.show(this.getContentPane(), "newGame");
		}
		
		
		
		else if(p3_Select.size()!=0)
			{
				for(int i = 0; i < p3_Select.size(); i++)	
				
				{
						if(arg0.getSource()==p3_Select.get(i))	//if the button for corresponding player is clicked, add the playerPanel of player to the playerPanel arraylist 
						{
							if(myPlayer == null)
							{
								oos.writeObject(p3_SelectConst);
								oos.reset();
								oos.writeInt(i);
								oos.reset();
							}
				
						}
				}
			}
		}catch(Exception e)
		{
			 System.out.println("got an exception: line 1015"+ e.getMessage());

		}
		
	}
	
	public void exitGame() throws IOException		
	{
		//save all the player info
		//writeGame();
		//System.exit(0);
		oos.writeObject("Exit");
		oos.reset();
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IOException{
		
		Game ga = new Game();
		ga.myRun();

	}
	
	public void goToNewPlayer()	///go to the panel of creating new player
	{
		cardlayout.show(this.getContentPane(),"createPlayer");
		validate();
		
	}
	public void goToStats()
	{
		cardlayout.show(this.getContentPane(), "displayStats");
		validate();
		
	}
	public void goToNext()
	{
		cardlayout.next(this.getContentPane());
		validate();
	}

	public void stopGame() throws ClassNotFoundException, IOException
	{
		remove(initialPage);
		remove(newGamePanel);//,"newGame"
		remove(createPlayerPanel);//,"createPlayer"
		remove(selectPlayerPanel);//,"selectedPlayer"
		remove(displayStatsPanel);//,"displayStats"
		remove(loadGamePanel);//,"loadGame"
		if(playerPanel.size()==4)
			{
				gamePanel.remove(playerPanel.get(2));
				gamePanel.remove(playerPanel.get(0));
				gamePanel.remove(bigMiddlePanel);
			}
		for(int i=0;i<playerPanel.size();i++)
			playerPanel.remove(i);
		remove(gamePanel);
		validate();
		initialize();
		cardlayout.first(this.getContentPane());
		
	}
	public String selectImageName(int location)
	{
		if(location==0)
			return ("cards/minitom.png");
		else if(location == 1)
			return("cards/minispike.png");
		else if(location == 2)
			return("cards/minijerry.png");
		else if(location == 3)
			return("cards/minityke.png");
		else
		{
			System.out.println("Bug in the selectedImageName funciton");
			return "";
		}

	}
	  
	
	
	public class CardsInDeckPanel extends JPanel	//A graphics panel extends from JPanel and overwrite the paint component method
	{												//show the unused deck in the middle
		protected void paintComponent(Graphics g){	
			super.paintComponent(g);
			unusedDeck.paintDeck(g,30,30,2);
		}
	}

}



