package com.three_stack.maximum_alpha.backend.game.effects.prompts.steps;

import com.three_stack.maximum_alpha.backend.game.cards.Card;
import com.three_stack.maximum_alpha.backend.game.effects.prompts.Prompt;

public interface StepInputChecker {
    boolean run(PromptStep promptStep, Card input, Prompt prompt);
}
