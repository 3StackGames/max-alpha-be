package com.three_stack.maximum_alpha.backend.game.actions.implementations;

import com.three_stack.maximum_alpha.backend.game.State;
import com.three_stack.maximum_alpha.backend.game.actions.abstracts.ExistingCardWithCostAction;
import com.three_stack.maximum_alpha.backend.game.cards.Card;
import com.three_stack.maximum_alpha.backend.game.cards.Creature;
import com.three_stack.maximum_alpha.backend.game.cards.Spell;
import com.three_stack.maximum_alpha.backend.game.effects.Trigger;
import com.three_stack.maximum_alpha.backend.game.effects.events.Event;
import com.three_stack.maximum_alpha.backend.game.phases.MainPhase;
import com.three_stack.maximum_alpha.backend.game.phases.PreparationPhase;

public class PlayCardAction extends ExistingCardWithCostAction {
    @Override
    public void run(State state) {
        //find a in player's hand
        Card card = player.getHand().takeCard(cardId, state.getTime(), state);

        player.pay(cost);


        if(card instanceof Creature) {
            state.createSingleCardEvent(card, "playCreature", state.getTime(), Trigger.ON_PLAY);
            player.getField().add((Creature) card, state.getTime(), state);
        } else if(card instanceof Spell) {
            Event playEvent = state.createSingleCardEvent(card, "playSpell", state.getTime(), Trigger.ON_PLAY);
            Spell spell = (Spell) card;
            spell.cast(playEvent, state);
        }
    }

  	@Override
  	public boolean isValid(State state) {
        if(!super.isValid(state)) {
            return false;
        }

        if(state.isPhase(MainPhase.class)) {
            if(!isPlayerTurn(state)) {
              return false;
            }
        } else if(state.isPhase(PreparationPhase.class)) {
            if(!(card instanceof Spell)) {
              return false;
            }
            if(player.isPreparationDone()) {
              return false;
            }
        } else {
            return false;
        }
        boolean notInPrompt = notInPrompt(state);
        boolean isPlayable = card.isPlayable();
        boolean inputCostSufficient = cost.hasResources(card.getCurrentCost());

        return notInPrompt && inputCostSufficient && isPlayable;
  	}
}
