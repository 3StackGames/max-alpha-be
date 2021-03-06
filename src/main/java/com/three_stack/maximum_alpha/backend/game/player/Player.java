package com.three_stack.maximum_alpha.backend.game.player;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

import com.three_stack.maximum_alpha.backend.game.Parameters;
import com.three_stack.maximum_alpha.backend.game.State;
import com.three_stack.maximum_alpha.backend.game.Time;
import com.three_stack.maximum_alpha.backend.game.cards.Card;
import com.three_stack.maximum_alpha.backend.game.cards.Castle;
import com.three_stack.maximum_alpha.backend.game.cards.Structure;
import com.three_stack.maximum_alpha.backend.game.cards.Worker;
import com.three_stack.maximum_alpha.backend.game.effects.QueuedEffect;
import com.three_stack.maximum_alpha.backend.game.effects.Trigger;
import com.three_stack.maximum_alpha.backend.server.Connection;

public class Player {
    //TODO: Actually retrieve their username
    private static int usernameCounter = 0;

    private final String username;
    private final transient Connection connection;
    private final UUID playerId;

    //Zones
    private Hand hand;
    private MainDeck mainDeck;
    private StructureDeck structureDeck;
    private Field field;
    private Graveyard graveyard;
    private Town town;
    private Courtyard courtyard;

    private Castle castle;

    private ResourceList resources;
    private boolean hasAssignedOrPulled;
    private Status status;
    private int maxHandSize = 10;

    private Deque<QueuedEffect> preparationPhaseQueuedEffects;
    private boolean preparationDone;

    public enum Status {
    	WIN, LOSE, TIE, PLAYING
    }

    public Player(Connection connection, Parameters parameters) {
        username = "Player " + usernameCounter++;

        this.connection = connection;
        playerId = UUID.randomUUID();

        mainDeck = new MainDeck(this);
        structureDeck = new StructureDeck(this);

        hand = new Hand(this);
        field = new Field(this);

        graveyard = new Graveyard(this);
        town = new Town(this);
        courtyard = new Courtyard(this);

        resources = new ResourceList(parameters.initialResources);
        castle = new Castle(parameters.maxHealth, this);

        status = Status.PLAYING;
        preparationPhaseQueuedEffects = new ArrayDeque<>();
        preparationDone = false;
    }

    public Collection<Card> getAllCards() {
    	Collection<Card> cards = new HashSet<>();
    	cards.addAll(mainDeck.getCards());
    	cards.addAll(hand.getCards());
    	cards.addAll(field.getCards());
    	cards.addAll(graveyard.getCards());
      cards.addAll(town.getCards());
      cards.addAll(courtyard.getCards());
      cards.addAll(structureDeck.getCards());
    	cards.add(castle);

    	return cards;
    }

    public Collection<Card> getVisibleCards() {
    	Collection<Card> cards = new HashSet<>();
    	cards.addAll(field.getCards());
    	cards.addAll(graveyard.getCards());
      cards.addAll(town.getCards());
      cards.addAll(courtyard.getCards());
      cards.addAll(structureDeck.getCards());
    	cards.add(castle);

      return cards;
    }

    public Collection<Card> getSelfVisibleCards() {
    	Collection<Card> cards = getVisibleCards();
    	cards.addAll(hand.getCards());

    	return cards;
    }

    public Collection<Card> getTargets() {
    	Collection<Card> targets = courtyard.getCards().stream().
    	    filter(structure -> !structure.isUnderConstruction()).collect(Collectors.toSet());
    	targets.add(castle);
    	return targets;
    }

    public void completeStructures(Time time, State state) {
    	courtyard.getCards().stream().filter(Structure::isUnderConstruction).forEach((structure) -> {
    		structure.setUnderConstruction(false);
            state.createSingleCardEvent(structure,"complete", time, Trigger.ON_STRUCTURE_COMPLETE);
    	});
    }

    //TODO: do we still need this?
    public void newTurn() {
    }

    public void draw(Time time, State state) {
        Card card = mainDeck.draw(time, state);
        hand.add(card, time, state);
    }

    public void gatherResources(State state) {
        for(Card workerCard : town.getCards()) {
            Worker worker = (Worker) workerCard;
            ResourceList resourceChange = worker.work(state);
            addResources(resourceChange);
        }
    }

    public void addResources(ResourceList addition) {
    	resources.add(addition);
    }

    public void pay(ResourceList cost) {
        resources.lose(cost);
    }

    public boolean hasResources(ResourceList other) {
        return resources.hasResources(other);
    }

    public void pushPreparationPhaseQueuedRunnable(QueuedEffect queuedEffect) {
        preparationPhaseQueuedEffects.push(queuedEffect);
    }

    public void castPreparedSpells(State state) {
        preparationPhaseQueuedEffects.stream()
                .forEachOrdered(state::addQueuedEffect);
        preparationPhaseQueuedEffects.clear();
    }

    public boolean isPreparationDone() {
        return preparationDone;
    }

    public void setPreparationDone(boolean preparationDone) {
        this.preparationDone = preparationDone;
    }

    /**
     * Generic Getters and Setters Below Here
     */

    public String getUsername() {
        return username;
    }

    public Connection getConnection() {
        return connection;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public Hand getHand() {
        return hand;
    }

    public Field getField() {
        return field;
    }

    public Graveyard getGraveyard() {
        return graveyard;
    }

    public Town getTown() {
        return town;
    }

    public Courtyard getCourtyard() {
        return courtyard;
    }

    public ResourceList getResources() {
        return resources;
    }

    public void setResources(ResourceList resources) {
        this.resources = resources;
    }

    public Castle getCastle() {
        return castle;
    }

    public MainDeck getMainDeck() {
        return mainDeck;
    }

    public void setMainDeck(MainDeck mainDeck) {
        this.mainDeck = mainDeck;
    }

    public StructureDeck getStructureDeck() {
        return structureDeck;
    }

    public void setStructureDeck(StructureDeck structureDeck) {
        this.structureDeck = structureDeck;
    }

    public Status getStatus() {
      return status;
    }
  
    public void setStatus(Status status) {
      this.status = status;
    } 
  
    public int getMaxHandSize() {
      return maxHandSize;
    }
  
    public void setMaxHandSize(int maxHandSize) {
      this.maxHandSize = maxHandSize;
    }
    
    //TODO: add support for triggerEffects which give extra assigns/pulls? or should those be separate triggerEffects
    public boolean canAssignOrPull() {
        return !hasAssignedOrPulled;
    }

    public void setHasAssignedOrPulled(boolean hasAssignedOrPulled) {
        this.hasAssignedOrPulled = hasAssignedOrPulled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
          return true;
        }
        if (o == null || getClass() != o.getClass()) {
          return false;
        }

        Player player = (Player) o;

        return playerId != null ? playerId.equals(player.playerId) : player.playerId == null;

    }

    @Override
    public int hashCode() {
        return playerId != null ? playerId.hashCode() : 0;
    }
}
