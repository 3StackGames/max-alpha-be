package com.three_stack.maximum_alpha.backend.game.effects;

public abstract class Effect {
    private long sourceId;

    private String prompt;

    public abstract String getType();
}
