
/*	methods
 * public Player() 
 * 	public Player(boolean b)
 * 	public Deck getDeck()
 * 	public void addCompletedSet() ***
 *  public int getCompletedSet()
 * 	public void addCard(Card newCard){
 * 	public void sortCards()
 *  public void printCardsInHand()
 *  public Card removeCard(int index)
 */

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.Serializable;

import javax.swing.JFrame;

public class Player implements Serializable{

	Deck deckInHand;		
	int completedSet;
	String name;
	int location;
	int gameWon;
	int gameLost;
	boolean ifSelected;
	
	

	public Player(String name,int location) {
		
		deckInHand = new Deck();
		completedSet = 0;
		gameWon = 0;
		gameLost = 0;
		this.name = name;
		this.location = location;
		ifSelected = false;

	}
	public Player(Player player)
	{
		deckInHand = new Deck();
		completedSet = 0;
		gameWon = player.getGameWon();
		gameLost = player.getGameLost();
		this.name = player.getName();
		this.location = player.getPanelLocation();
		ifSelected = false;
	}
	
	void setSelected (boolean selected)
	{
		ifSelected = selected;
	}
	
	boolean getSelected()
	{
		return (ifSelected);
	}
	public void initialize(int location)		//initialize these datas after read player from a file
	{
		deckInHand = new Deck();
		completedSet = 0;
		this.location = location;
	}
	public void  addGameLost()
	{
		gameLost++;
	}
	
	public void  addGameWon()
	{
		gameWon++;
	}
	
	public int getGameLost()
	{
		return gameLost;
	}
	
	public int getGameWon()
	{
		return gameWon;
	}
	
	public void flipCard(boolean faceUp)	//flipCard base on the boolean faceUp, if faceUp==true, display front side of the card, else display the back of the card
	{
		for(int i = 0; i < deckInHand.getSize(); i++)
		{
			deckInHand.getCard(i).setFaceUp(faceUp);
		}
	}
	
	/***********getters**************/
	public int getPanelLocation(){
		
		return location;
	}
	public String getName(){
		
		return name;
	}
	public Deck getDeck(){
		
		return deckInHand;
		
	}
	
	public void addCompletedSet(){
		
		completedSet++;
		
	}
	
	public int getCompletedSet(){
		
		return completedSet;
		
	}
	
	public void addCard(Card newCard){	//add a card to deckInHand
		
		deckInHand.addACard(newCard);
		
	}
	
	public void sortCards()	{			//sort the deckInHand
		deckInHand.sortDeck();
	}
	
	public void printCardsInHand(){		//print the info of all the cards in the deck
	
		deckInHand.printDeck();
		
	}
	
	public void paintCardsInHand(Graphics g2,int x, int y){		//paint all the cards in deckInHand
	
		deckInHand.paintDeck(g2, x, y,20);
		
	}
	public void paintCardsInHandV(Graphics g2,int x, int y){		//paint all the cards in deckInHand
		
		deckInHand.paintDeckV(g2, x, y,17);
		
	}
	
	public Card removeCard(int index){			//remove deckInHand[index] and return it 
	
		return(deckInHand.remove(index));
		
	}



}
