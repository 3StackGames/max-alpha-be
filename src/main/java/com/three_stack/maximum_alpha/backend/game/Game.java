package com.three_stack.maximum_alpha.backend.game;


//GameState will have a history as one of its fields (?)

public final class Game {
	
	private Game() {}
	
	public static GameState newGame(GameParameters gp) {
		return null;
	}

	public static GameState processAction(GameState state, GameAction action) {
		return null;
	}

	public static boolean isMoveLegal(GameState state, GameAction action) {
		return false;
	}

	public static Deck loadDeck(int deckId) {
		return null;
	}

	public static Card loadCard(int cardId) {
		return null;
	}
}
