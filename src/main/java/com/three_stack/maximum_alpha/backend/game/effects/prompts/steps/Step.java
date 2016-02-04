package com.three_stack.maximum_alpha.backend.game.effects.prompts.steps;

import com.three_stack.maximum_alpha.backend.game.cards.Card;
import com.three_stack.maximum_alpha.backend.game.effects.prompts.Prompt;

public abstract class Step {
    /**
     * Used to pass a value through to the result lambda
     */
    protected Object value;
    protected final String instruction;
    protected StepCompleter stepCompleter;
    protected StepInputChecker stepInputChecker;

    public Step(String instruction, Object value, StepInputChecker stepInputChecker, StepCompleter stepCompleter) {
        this.instruction = instruction;
        this.value = value;
        this.stepInputChecker = stepInputChecker;
        this.stepCompleter = stepCompleter;
    }

    public void complete(Card input, Prompt prompt) {
        if(stepCompleter != null) {
            stepCompleter.run(this, input, prompt);
        }
    }

    public boolean isValidInput(Card input, Prompt prompt) {
        if(stepInputChecker == null) return true;
        else return stepInputChecker.run(this, input, prompt);
    }
    
    public abstract void reset();

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}