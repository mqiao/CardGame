/*	methods
 * public Deck( )
 * 	public Deck(boolean defaultDeck)
 * public void generateRankList()
 * public []boolean getRankList()
 * public Card getLastCard()
 * 
 * 	public Card getCard(int index)
 * 	public void addACard (Card newCard)
 * 	public Card takeTopCard()
 * 	public Card remove(int i)
 * 	public int  getSize()
 * 	public void shuffle()
 *	public void printDeck()
 *	public void sortDeck()
 *	void paintDeck(JFrame j,Graphics2D g2, int x, int y)
 * */

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.*;
import javax.swing.JFrame;

public class Deck implements Serializable{	//collection of cards: We're supposed to have 4 decks in the game: unused deck, deck in player1's hand and deck in player 2 's hand and the deck of completedcollection

	ArrayList<Card> cards;			
	boolean[] rankList;			//an array of boolean with size of 13. if rankList[i] is true, rank i is in the deck
	


	public Deck( ){				//default deck
		
		cards = new ArrayList<Card>();
		
		rankList = new boolean[13];	//initialize rankList
		
		for(int i = 0;i < 13;i++){
			
			rankList[i] = false;
		}
	}
	
		public Deck(boolean defaultDeck){		//a deck with 52 cards, use this to generate the deck at the beggining of the game
	
			cards = new ArrayList<Card>();
			rankList = new boolean[13];
			for(int i = 0;i<13;i++){
				rankList[i] = false;
		}
		
		if(defaultDeck){
			for (int i = 1;i<14;i++){
				cards.add(new Card('h',i,false,false,false));
				cards.add(new Card('d',i,false,false,false));
				cards.add(new Card('s',i,false,false,false));
				cards.add(new Card('c',i,false,false,false));
			}
		}
		generateRankList();
		shuffle();	
		
	}
	
	/*************getters**************/
	public Card getLastCard(){
		
		return cards.get(cards.size()-1);
	}
	
	public Card getCard(int index){
		
		return cards.get(index);
	}
	
	boolean[] getRankList(){
		
		return(rankList);
	}
	
	public int getSize(){
		
		return (cards.size());
	}
	
	public void addACard (Card newCard){	//add a new card to the deck
	
		cards.add(newCard);
		rankList[newCard.getRank()-1] = true;
		
	}
	
	public Card takeTopCard(){ //remove the last card and return it
	
		Card temp = cards.remove(cards.size()-1);
		generateRankList();
		return(temp);
			
	}
	
	public Card remove(int i){ //remove the card at index i and return it
		Card temp=cards.remove(i);

		generateRankList();
	
		return(temp);
	}
	
	

	public void shuffle()			//shuffle the deck
	{
		ArrayList<Card> shuffledCards = new ArrayList<Card>();
		Random r = new Random();
		int size = cards.size();
		for(int i = 0;i<size;i++){
			int index = r.nextInt(size-i);
			shuffledCards.add(cards.remove(index));
			
		}
		cards=shuffledCards;
		
	}
	
	
	public void printDeck(){			//for test purpose, print each card in the deck
	
		if(cards.size()==0)
			System.out.println("It's an empty deck");
		for(int i=0;i<cards.size();i++)
			System.out.println(cards.get(i).toString());
		System.out.println();
	}
	
	public void sortDeck(){			//sort the deck based on their rank, use bubble sort
	
		Card temp;
		for(int k=1;k<cards.size();k++){
			for(int i=0;i<cards.size()-k;i++){
				
				if(cards.get(i).getRank()>cards.get(i+1).getRank()){
					 temp= cards.get(i);
					 cards.set(i, cards.get(i+1));
					 cards.set(i+1,temp);				
				}
			}
			
		}
	}
	
	public void paintDeck(Graphics g2, int x, int y,int interval)		//paint all the card in the deck, start from (x,y) 
	{
		for(int i=0;i<cards.size();i++){
			
				cards.get(i).paintCard(g2, x+interval*i, y);
				
			}
	}
	public void paintDeckV(Graphics g2, int x, int y,int interval)		//paint all the card in the deck, start from (x,y) 
	{
		for(int i=0;i<cards.size();i++){
			
				cards.get(i).paintCard(g2, x, y+interval*i);
				
			}
	}

	public void generateRankList()				//generate or update the rankList
	{
		for(int j=0;j<13;j++){
			rankList[j]=false;
		}
		
		for(int i=0;i<cards.size();i++){		
			if(rankList[cards.get(i).getRank()-1]==false)
				rankList[cards.get(i).getRank()-1]=true;
		}
		
	}
	

	
}
