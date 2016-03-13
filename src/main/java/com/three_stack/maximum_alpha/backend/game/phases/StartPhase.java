package com.three_stack.maximum_alpha.backend.game.phases;

import com.three_stack.maximum_alpha.backend.game.State;
import com.three_stack.maximum_alpha.backend.game.effects.Trigger;
import com.three_stack.maximum_alpha.backend.game.effects.events.Event;


public class StartPhase extends Phase {
    protected static StartPhase instance;

    protected StartPhase() {
        super();
    }

    public static StartPhase getInstance() {
        if(instance == null) {
            instance = new StartPhase();
        }
        return instance;
    }

    /**
     * Overview
     * 1. Structures Complete
     * 2. Resources Generated
     * 3. Begin Turn Triggers
     * 4. Draw Card
     * 5. Refresh Cards
     * @param state
     */
    public void start(State state) {
        state.setCurrentPhase(instance);
        //@Todo: structure complete
        state.completeStructures();
        //resources generated
        state.gatherResources();
        //@Todo: triggerEffects
        //begin turn triggers
        Event startPhaseStartEvent = new Event(state.getTime(), "START PHASE START");
        state.addEvent(startPhaseStartEvent, Trigger.ON_START_PHASE_START);
        //turnPlayerDraw a
        state.turnPlayerDraw();
        //refresh
        state.refreshTurnPlayerCards(state.getTime(), state);
        
        state.getTurnPlayer().setHasAssignedOrPulled(false);

        end(state);
    }

    public void end(State state) {
        MainPhase.getInstance().start(state);
    }

    @Override
    public String getType() {
        return "START_PHASE";
    }
}
