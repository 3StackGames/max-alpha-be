package com.three_stack.maximum_alpha.backend.game.effects;

public enum Trigger {
    ON_DRAW,
    ON_PULL,
    ON_PLAY,
    ON_ASSIGN,
    ON_DECLARE_ATTACK,
    ON_DECLARE_BLOCK,
    ON_DAMAGE,
    ON_HEAL,
    ON_STRUCTURE_COMPLETE,
    ON_DEATH,
    ON_REFRESH,
    ON_EXHAUST,

    ON_ENTER_HAND,
    ON_LEAVE_HAND,
    ON_ENTER_FIELD,
    ON_LEAVE_FIELD,
    ON_ENTER_TOWN,
    ON_LEAVE_TOWN,
    ON_ENTER_COURTYARD,
    ON_LEAVE_COURTYARD,

    ON_START_PHASE_START,
    ON_ATTACK_PHASE_START,
    ON_BLOCK_PHASE_END,
    ON_END_PHASE_END
}
