package com.three_stack.maximum_alpha.backend.game.cards;

import com.three_stack.maximum_alpha.backend.game.State;
import com.three_stack.maximum_alpha.backend.game.ResourceList;

public interface Worker {
	/**
	 * Called each turn at Draw Phase start when this card is assigned as a worker.
	 * 
	 * @param s The gamestate in which this card exists
	 * @return A resource list with resource values equal to the net change in resource quantities
	 */
	ResourceList work(State s);
}