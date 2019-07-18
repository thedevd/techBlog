package com.thedevd.javaexamples.designpatterns.creational;

/* Some times when object creation is costly and requires a lot of time and resources and we need to
 * create the similar object multiple time again and again. In such case prototype pattern helps us
 * to provide a way to copy the original object and modify it according to our needs.
 * 
 * The core concept of prototype pattern is Java cloning concept which is used to clone a object. In
 * plane words this pattern Creates object based on an existing object through cloning.
 * 
 * Realworld example- Assume you are creating online chess playing game. So each game requires a
 * setup of chess board i.e. initial position of all the 16 pieces (king, queen, 2 rooks, 2 knights,
 * two bishops and 8 pawns). So here prototype pattern can be used. So whenever a new game needs to
 * start we can clone the existing initial setup of chess board object and assign joined player to
 * them. */
public class PrototypePattern {

	public static void main( String[] args ) throws CloneNotSupportedException
	{
		ChessBoard originalChessBoard = new ChessBoard();

		// start a game 
		ChessBoard game1 = originalChessBoard.clone();
		game1.setPlayer1id(1);
		game1.setPlayer2id(2);
		game1.startGame();

		// start another game
		ChessBoard game2 = originalChessBoard.clone();
		game2.setPlayer1id(3);
		game2.setPlayer2id(4);
		game2.startGame();

		// @formatter:off
		/*
		 * output-
		 * Initial Chess board setup done....
		 * Play started between: 1 and 2
		 * Play started between: 3 and 4
		 */
		// @formatter:on

	}
}

class ChessBoard implements Cloneable {

	private int player1id;
	private int player2id;

	public ChessBoard()
	{
		setup();
	}

	private void setup()
	{
		// all setup steps goes here....
		System.out.println("Initial Chess board setup done....");
	}

	@Override
	protected ChessBoard clone() throws CloneNotSupportedException
	{
		return (ChessBoard) super.clone();
	}

	public int getPlayer1id()
	{
		return player1id;
	}

	public void setPlayer1id( int player1id )
	{
		this.player1id = player1id;
	}

	public int getPlayer2id()
	{
		return player2id;
	}

	public void setPlayer2id( int player2id )
	{
		this.player2id = player2id;
	}

	public void startGame()
	{
		System.out.println("Play started between: " + player1id + " and " + player2id);
	}
}
