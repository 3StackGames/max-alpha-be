package com.three_stack.maximum_alpha.backend.game.actions.implementations;

import com.three_stack.maximum_alpha.backend.game.State;
import com.three_stack.maximum_alpha.backend.game.actions.abstracts.Action;
import com.three_stack.maximum_alpha.backend.game.actions.abstracts.ExistingCardAction;
import com.three_stack.maximum_alpha.backend.game.effects.SingleTargetEffect;

import java.util.List;
import java.util.UUID;

public class SingleTargetEffectAction extends ExistingCardAction {
    @Override
    public void run(State state) {
        super.run(state);
        SingleTargetEffect effect = (SingleTargetEffect) state.takeEffect();
        effect.resolve(state, card);
    }
}