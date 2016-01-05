package com.three_stack.maximum_alpha.backend.game.effects;

import com.three_stack.maximum_alpha.backend.game.State;
import com.three_stack.maximum_alpha.backend.game.cards.Card;
import com.three_stack.maximum_alpha.backend.game.cards.Creature;
import com.three_stack.maximum_alpha.backend.game.events.Event;

import java.util.List;

public class BlockEffect extends SingleTargetEffect {

    public BlockEffect(Card source, String prompt, List<Card> targetables) {
        super(source, prompt, targetables);
    }

    @Override
    public void resolve(State state, Card target) {
        Creature blocker = (Creature) getSource();
        blocker.setBlockTarget(target);

        Event event = new Event(blocker.getName() + " is now blocking " + target.getName());
        state.addEvent(event);
    }
}