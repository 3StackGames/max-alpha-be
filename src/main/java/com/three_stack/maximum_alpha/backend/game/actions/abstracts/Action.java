package com.three_stack.maximum_alpha.backend.game.actions.abstracts;

import com.three_stack.maximum_alpha.backend.game.player.Player;
import com.three_stack.maximum_alpha.backend.game.State;
import com.three_stack.maximum_alpha.backend.game.events.Event;

import java.util.UUID;

/**
 * Always player Input
 */
public abstract class Action extends Event {
    protected UUID playerId;

    protected Player player;

    public void run(State state) {
        player = state.findPlayer(playerId);
    }
    
    public abstract boolean isValid(State state);

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public Player getPlayer(State state) {
        for(Player player : state.getPlayers()) {
            if(playerId.equals(player.getPlayerId())) {
                return player;
            }
        }
        throw new IllegalArgumentException("Player Not Found");
    }
    
    public boolean isPlayerTurn(State state) {
		return (player == state.getTurnPlayer()); 	
    }
    
    public boolean isPhase(State state, String phaseName) {
    	return state.getCurrentPhase().getName().equals(phaseName);
    }
    
    public boolean notInPrompt(State state) {
    	return state.getCurrentPrompt() == null;
    }
}
