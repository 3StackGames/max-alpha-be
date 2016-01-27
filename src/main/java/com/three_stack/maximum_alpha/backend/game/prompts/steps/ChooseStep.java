package com.three_stack.maximum_alpha.backend.game.prompts.steps;

import com.three_stack.maximum_alpha.backend.game.cards.Card;
import io.gsonfire.annotations.ExposeMethodResult;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ChooseStep extends Step {
    protected transient List<Card> choices;
    protected transient Card choice;

    public ChooseStep(String instruction, List<Card> choices) {
        super(instruction);
        this.choices = choices;
    }

    @Override
    public void complete(Card input) {
        setChoice(input);
    }

    @Override
    public void reset() {

    }

    @ExposeMethodResult("choiceIds")
    public List<UUID> getChoiceIds() {
        return choices.stream()
                .map(Card::getId)
                .collect(Collectors.toList());
    }

    @ExposeMethodResult("choiceId")
    public UUID getChoiceId() {
        return choice.getId();
    }

    public Card getChoice() {
        return choice;
    }

    public void setChoice(Card choice) {
        this.choice = choice;
    }

    public List<Card> getChoices() {
        return choices;
    }

    public void setChoices(List<Card> choices) {
        this.choices = choices;
    }
}
