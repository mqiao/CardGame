import java.awt.Dimension;
import java.awt.Graphics;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.Timer;

import javax.swing.JPanel;



public class PlayerPanel extends JPanel implements ActionListener {
	Game game;
	JPanel whole;
	Player player;
	GraphicsPanel graphicsPanel;
	JButton characterButton;
	JPanel top;
	JPanel buttonsPanel;
	ImageIcon characterIcon;
	JButton buttons[];
	Timer myTimer;
	int location;//1=up, 2=left,3=bottom,0=right
	boolean ifHuman;

	
	public PlayerPanel (String imageName,String characterName,int location,boolean ifHuman, Game g){//constructor for a newly created player
		
		
		game=g;
		this.location = location;	
		characterIcon = new ImageIcon(imageName);
		player=new Player(characterName,location);
		this.ifHuman=ifHuman;
		initialize();	
		

		
	}
	
	public PlayerPanel (String imageName,Player player,int location,boolean ifHuman,Game g){//constructor for an existed player(read from the dat file)
		
		
		game=g;
		this.location = location;	
		characterIcon = new ImageIcon(imageName);
		this.player=player;
		this.ifHuman=ifHuman;
		player.initialize(location);
		initialize();
		
		
		
		
	}
	int generateRandomPlayerIndex()	//new function: generate the index of selected player for the AI
	{								//  must no be the AI itself
		int index;
		do
		{
			index = (int)(Math.random()*4);
			
		}while(index==location);
		
		return index;
	}
	
	int generateRandomRank()	//new function: generate the rank of the card for the AI
	{							// the AI must have this rank of card in hands
		int index;
		do
		{
			index = (int)(Math.random()*13);
			
		}while(player.getDeck().getRankList()[index]==false);
		
		return index;
	}
	
	void initialize()	//initialize all the swing component
	{
		whole= new JPanel();
		characterButton = new JButton(characterIcon);
		characterButton.addActionListener(this);
		characterButton.setPreferredSize(new Dimension(100,158));				
		characterButton.setToolTipText(player.getName());
		
		buttons = new JButton[13];
		for(int i=1;i<10;i++)
		{
			buttons[i] = new JButton(""+(i+1));

		}
		buttons[0] = new JButton("A");
		buttons[10] = new JButton("J");
		buttons[11] = new JButton("Q");
		buttons[12] = new JButton("K");
		for(int i = 0;i<13;i++)
		{
			buttons[i].setPreferredSize(new Dimension(20,20));
			buttons[i].addActionListener(this);
		}
		
		buttonsPanel = new JPanel();
		
		top = new JPanel();//also left
		graphicsPanel= new GraphicsPanel(location);
		if(location==1||location==3)
		{
			boolean up=false;
		
			if(location==1)
				up=true;
			else if(location == 3)
				up=false;
		
			
			graphicsPanel.setPreferredSize(new Dimension(600,160));
		
		
			BoxLayout bpl = new BoxLayout(buttonsPanel,BoxLayout.X_AXIS);
			buttonsPanel.setLayout(bpl);
			for(int i=0;i<13;i++)
			{
				buttonsPanel.add(buttons[i]);
			}
		

			whole.setLayout(new BoxLayout(whole,BoxLayout.Y_AXIS));
		
			top.setLayout(new BoxLayout(top,BoxLayout.X_AXIS));
			if(up)
			{
				whole.add(buttonsPanel);
				whole.add(top);
				top.add(characterButton);
				top.add(graphicsPanel);	
			}
			else
			{
				whole.add(top);
				whole.add(buttonsPanel);
				top.add(graphicsPanel);
				top.add(characterButton);
			}
		}
		else if (location==2||location==0)
		{
			boolean left=false;
			
			if(location==2)
				left=true;
			else if(location == 0)
				left=false;
		
			
			graphicsPanel.setPreferredSize(new Dimension(160,500));
		
		
			BoxLayout bpl = new BoxLayout(buttonsPanel,BoxLayout.Y_AXIS);
			buttonsPanel.setLayout(bpl);
			for(int i=0;i<13;i++)
			{
				buttonsPanel.add(buttons[i]);
			}
		

			whole.setLayout(new BoxLayout(whole,BoxLayout.X_AXIS));
		
			top.setLayout(new BoxLayout(top,BoxLayout.Y_AXIS));
			if(left)
			{
				whole.add(buttonsPanel);
				whole.add(top);
				top.add(graphicsPanel);	
				top.add(characterButton);
			}
			else
			{
				whole.add(top);
				whole.add(buttonsPanel);
				top.add(characterButton);	
				top.add(graphicsPanel);
			}
		}

		
			add(whole);
	}
	public void enableIcon(boolean enable){	//enable all the icons for characters
		characterButton.setEnabled(enable);
	}

	public Player getPlayer()	//return the player in this class
	{
		return player;
	}
	
	public void actionPerformed(ActionEvent ae) {
		
		if(characterButton==ae.getSource())	//if a character button is clicked, set this playerPanel as the selected enable the card button for the current player
		{
			if(game.myPlayer==game.currentPlayer)
			{
				if(this!=game.getCurrentPlayer())
				{
					try{
					game.oos.writeObject("SelectedPlayer");
					game.oos.reset();
					game.oos.writeInt(this.getPanelLocation());
					game.oos.reset();
					}
					catch(Exception e)
					{
						e.printStackTrace();	
					}
					
					game.showRankButtons();
					game.setSelectedPlayer(this);
					graphicsPanel.repaint();
					game.checkIfWin();
				}
				else
					game.printToRecords("You cannot ask card from yourself!!!\n\n");
			}
		}
		
		for(int i=0;i<13;i++)
		{
			
			if (buttons[i]==ae.getSource())	//if a card button is clicked, test if the selected user has the card, it he/she does, draw the cards from his/her hands to the current player , if not let the current player go fish
			{
				if(game.ifPHasIt(game.getSelectedPlayer().getPlayer(),i+1))
					{
					
						//game.printToRecords(game.getSelectedPlayer().getPlayer().getName()+" has the card you want!\n\n");
						try{
							game.oos.writeObject("sHasIt");
							game.oos.reset();
							int temp = i+1;
							game.oos.writeInt(temp);
							game.oos.reset();
						}
						catch(Exception e)
						{
							System.out.println("line 218 in playerpanel.java");
							e.printStackTrace();
						}
						
					}
				else
				{
					//game.printToRecords(game.getSelectedPlayer().getPlayer().getName()+" does not have the card you want!Please go fish now\n\n");
					try{
						game.oos.writeObject("doesntHaveIt");
						game.oos.reset();
						int temp= i+1;
						game.oos.writeInt(temp);
						game.oos.reset();
					}
					catch(Exception e)
					{
						System.out.println("line 238 in PlayerPanel");
						e.printStackTrace();
					}

				}
				game.checkIfWin();
				
			}
		}
		
	}
	
	public int getPanelLocation()
	{
		return location;
	}
	
	void enableButtons(boolean enable)
	{
			for(int i=0;i<13;i++)
				buttons[i].setEnabled(enable);		
		
	}
	
	void showButtons()	//show the current players card buttons according to the cards in his/her hands(only the ranks that he/she has will be enabled)
	{
		for(int i = 0 ; i < 13 ; i++)
		{
			if(player.getDeck().rankList[i]==true)
			{
				buttons[i].setEnabled(true);
			}
			else
				buttons[i].setEnabled(false);
		
		}
	}
	protected void paintComponent(Graphics g)
	{
		graphicsPanel.paintComponents(g);
	}
	
	
	public class GraphicsPanel extends JPanel
	{
		int location;
		public GraphicsPanel(int location)
		{
			this.location=location;
		}
		
		protected void paintComponent(Graphics g){	
			if(player==game.myPlayer.getPlayer())
				player.flipCard(true);
			else
				player.flipCard(false);//TODO
			
			if(location==1)
			{
				player.paintCardsInHand(g,30,30);
				
			}
			else if(location==3)
			{
				player.paintCardsInHand(g,getWidth()-73-30-(player.getDeck().getSize()-1)*20,30);
			}
			else if(location == 0 )
			{
				player.paintCardsInHandV(g,30,20);
			}
			else if(location == 2)
			{
				player.paintCardsInHandV(g,30,getHeight()-97-20-(player.getDeck().getSize()-1)*15);
			}
		}
		
		
	}
	

	

}


