package com.three_stack.maximum_alpha.backend.game.cards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.three_stack.maximum_alpha.backend.game.effects.Check;
import com.three_stack.maximum_alpha.backend.game.effects.Checks;
import com.three_stack.maximum_alpha.backend.game.effects.Effect;
import com.three_stack.maximum_alpha.backend.game.effects.results.Result;
import com.three_stack.maximum_alpha.backend.game.effects.results.TargetStep;
import com.three_stack.maximum_alpha.backend.game.effects.results.implementations.BuffResult;
import com.three_stack.maximum_alpha.backend.game.effects.results.implementations.DealDamageResult;
import com.three_stack.maximum_alpha.backend.game.utilities.ValueExpression;

public class TagEffectFactory {
    public static Effect createDegenerateEffect(int damage, Card source) {
        List<Check> checks = new ArrayList<>();
        checks.add(Checks.S_ON_FIELD);
        checks.add(Checks.C_TURN_PLAYER);

        List<Result> results = new ArrayList<>();
        List<TargetStep> targetSteps = new ArrayList<>();
        Map<String, Object> targetStepMap = new HashMap<>();
        targetStepMap.put("self", true);
        TargetStep targetSelf = new TargetStep(targetStepMap);
        targetSteps.add(targetSelf);
        DealDamageResult dealDamageResult = new DealDamageResult(targetSteps, new ValueExpression(damage));
        results.add(dealDamageResult);

        Effect effect = new Effect(source, checks, results);
        return effect;
    }

    //TODO: consolidate buffs rather than constantly adding new ones
    public static Effect createGrowthEffect(int attackRate, int healthRate, Card source) {
        List<Check> checks = new ArrayList<>();
        checks.add(Checks.S_ON_FIELD);
        checks.add(Checks.C_TURN_PLAYER);

        List<Result> results = new ArrayList<>();
        List<TargetStep> targetSteps = new ArrayList<>();
        Map<String, Object> targetStepMap = new HashMap<>();
        targetStepMap.put("self", true);
        TargetStep targetSelf = new TargetStep(targetStepMap);
        targetSteps.add(targetSelf);
        BuffResult buffResult = new BuffResult(targetSteps, new ValueExpression(attackRate), new ValueExpression(healthRate));
        results.add(buffResult);

        Effect effect = new Effect(source, checks, results);
        return effect;
    }
}
