package com.three_stack.maximum_alpha.backend.game.actions.implementations;

import com.three_stack.maximum_alpha.backend.game.State;
import com.three_stack.maximum_alpha.backend.game.actions.abstracts.ExistingCardAction;
import com.three_stack.maximum_alpha.backend.game.cards.Card;
import com.three_stack.maximum_alpha.backend.game.cards.Creature;
import com.three_stack.maximum_alpha.backend.game.prompts.BlockPrompt;
import com.three_stack.maximum_alpha.backend.game.prompts.Prompt;
import com.three_stack.maximum_alpha.backend.game.events.Event;

import java.util.List;
import java.util.stream.Collectors;

public class DeclareBlockerAction extends ExistingCardAction {

    @Override
    public void run(State state) {
        super.run(state);

        if(!(card instanceof Creature)) {
            throw new IllegalArgumentException("Blocking card isn't a creature");
        }

        Creature blocker = (Creature) card;

        List<Card> blockableTargets = state.getTurnPlayer().getField().getCards().stream()
                .filter(creature -> creature.isAttacking())
                .collect(Collectors.toList());

        Prompt blockPrompt = new BlockPrompt(card, "Select attacker to block", blockableTargets);
        state.addPrompt(blockPrompt);

        Event event = new Event(player, " declared " + blocker.getName() + " as a blocker");
        state.addEvent(event);
    }
}
