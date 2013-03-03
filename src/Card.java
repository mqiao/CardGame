/*	methods
 * public Card(char suit, int rank,boolean ifCompleted,boolean inHand,boolean faceUp)
	public String toString()
	public void paintCard(JFrame j, Graphics g, int x, int y)
	String chooseRightImage()
	public char getSuit()
	public int getRank()
	public static char getRankChar(int rank)**********
	public boolean getIfCompleted()
	public string getRankString()
	public boolean getInHand()
	public boolean getIfFaceUp()
	public ImageIcon getImage()
	public void setCompleted(boolean ifCompleted)
	public void setInHand(boolean inHand)
	public void setFaceUp(boolean faceUp)
	
	*/
	

import java.awt.*;
import java.io.Serializable;

import javax.swing.*;


public class Card implements Serializable{

	char suit;	 //heart h, diamond d, spade s, clover c
	int rank;	 //1 to 13
	boolean ifCompleted;	//if the card is in the collection
	boolean inHand;			//if the card is in someone's hand
	boolean faceUp;			//if the card is face up
	ImageIcon cardImage;		//the front image of the card
	
	static ImageIcon backImage; //the back image of the card
	
	
	private Card() {	//prevent user to  create an instance without parameters

	}
	
	public Card(char suit, int rank,boolean ifCompleted,boolean inHand,boolean faceUp){
		this.suit = suit;
		this.rank = rank;
		this.ifCompleted = ifCompleted;
		this.inHand = inHand;
		this.faceUp = faceUp;
		cardImage = new ImageIcon(chooseRightImage());
		backImage = new ImageIcon("cards/b.gif");

	}
	
	public String toString(){	//override the toString class in Object class, display basic info (rank and suit) of the card
		String suitDiscription;
		switch(suit){
		case 'h': 
			suitDiscription = new String("heart");
			break;
		case 'd':
			suitDiscription = new String("diamond");
			break;
		case 's':
			suitDiscription = new String("spade");
			break;
		case 'c':
			suitDiscription = new String("clover");
			break;
			default:
				suitDiscription = new String("NotDefined");
		}
		
		return(suitDiscription+" "+getRankString(rank));
	}
	

	public void paintCard(Graphics g,int x,int y){	//paint this card at (x,y)
	
		g.drawImage(getImage().getImage(),x,y,null);
		
		
	}
	
	String chooseRightImage(){						//generate the correct filename for the image of a specific card
	
		char tempRank;
		if(rank==1||(rank>=11&&rank<=13)){
			tempRank = Character.toLowerCase(getRankString(rank).charAt(0));
			return("cards/"+tempRank+suit+".gif");
		}
		
		else if(rank==10)
			return("cards/t"+suit+".gif");
		else
			return("cards/"+rank+suit+".gif");
	}

/*************getters****************/
	public char getSuit(){
		return suit;
	}
	
	public int getRank(){
		return rank;
	}
	
	public boolean getIfCompleted(){
		return ifCompleted;
	}

	public boolean getInHand(){
		return inHand;
	}
	
	public boolean getIfFaceUp(){
		return faceUp;
	}	
	
	public ImageIcon getImage(){
		if(faceUp)
			return cardImage;
		else
			return backImage;
	}

/********************setters*********************/	
	public void setCompleted(boolean ifCompleted){
		
		this.ifCompleted=ifCompleted;
	}
	
	public void setInHand(boolean inHand){
		
		this.inHand=inHand;
	}
	
	public void setFaceUp(boolean faceUp){
		
		this.faceUp=faceUp;

	}

	public static String getRankString(int rank){	//convert a card's rank (a int) to the real rank
	
		String specialRank;
		if(rank>=2&&rank<=10){
			
			return(Integer.toString(rank));
		}
		else if((rank>=11 && rank<=13)||rank==1){
			
			switch(rank){
			case 1: specialRank = "A";
			break;
			case 11: specialRank ="J";
			break;
			case 12: specialRank ="Q";
			break;
			case 13: specialRank ="K";
			break;
			default:
			specialRank ="X";
			}
			
			return(specialRank);
		}
	else
		return("X");
		
	}


	
}