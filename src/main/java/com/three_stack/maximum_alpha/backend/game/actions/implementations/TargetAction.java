package com.three_stack.maximum_alpha.backend.game.actions.implementations;

import com.three_stack.maximum_alpha.backend.game.State;
import com.three_stack.maximum_alpha.backend.game.actions.abstracts.Action;

import java.util.List;
import java.util.UUID;

public class TargetAction extends Action {
    protected List<UUID> cardIds;

    @Override
    public void run(State state) {
        super.run(state);
    }
}