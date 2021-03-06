package com.three_stack.maximum_alpha.backend.game.actions.implementations;

import java.util.UUID;

import com.three_stack.maximum_alpha.backend.game.State;
import com.three_stack.maximum_alpha.backend.game.actions.abstracts.ExistingCardWithCostAction;
import com.three_stack.maximum_alpha.backend.game.cards.Ability;
import com.three_stack.maximum_alpha.backend.game.cards.Creature;
import com.three_stack.maximum_alpha.backend.game.cards.NonSpellCard;
import com.three_stack.maximum_alpha.backend.game.effects.QueuedEffect;
import com.three_stack.maximum_alpha.backend.game.effects.events.Event;
import com.three_stack.maximum_alpha.backend.game.phases.MainPhase;

public class ActivateAbilityAction extends ExistingCardWithCostAction {
    protected UUID abilityId;
    protected Ability ability;
    protected NonSpellCard nonSpellCard;

    @Override
    public void run(State state) {
        player.pay(cost);

        if(ability.isTap()) {
            nonSpellCard.exhaust(state.getTime(), state);
        }

        Event activateAbilityEvent = state.createSingleCardEvent(card, "activate ability", state.getTime(), null);

        ability.getEffects().stream()
                .map(effect -> new QueuedEffect(activateAbilityEvent, effect))
                .forEach(state::addQueuedEffect);
    }

    @Override
    public boolean isValid(State state) {
        if(!super.isValid(state)) {
            return false;
        }
        boolean notInPrompt = notInPrompt(state);
        boolean correctPhase = state.isPhase(MainPhase.class);
        boolean playerTurn = isPlayerTurn(state);
        boolean playerOwnsCard = card.getController().equals(player);
        if(!(card instanceof NonSpellCard)) {
          return false;
        }

        NonSpellCard nonSpellCard = (NonSpellCard) card;
        Ability ability = nonSpellCard.getAbility(abilityId);
        if(ability == null) {
          return false;
        }

        boolean inputCostSufficient = cost.hasResources(ability.getCost());

        if(ability.isTap()) {
            if(nonSpellCard.isExhausted()) {
                return false;
            }
            if(nonSpellCard instanceof Creature) {
                Creature creature = (Creature) nonSpellCard;
                if(creature.isSummoningSickness()) {
                    return false;
                }

            }
        }

        return notInPrompt && correctPhase && playerTurn && playerOwnsCard && inputCostSufficient;
    }

    @Override
    public void setup(State state) {
        super.setup(state);

        nonSpellCard = (NonSpellCard) card;
        ability = nonSpellCard.getAbility(abilityId);
    }
}
