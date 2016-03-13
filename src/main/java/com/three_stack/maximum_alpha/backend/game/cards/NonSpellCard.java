package com.three_stack.maximum_alpha.backend.game.cards;

import com.three_stack.maximum_alpha.backend.game.State;
import com.three_stack.maximum_alpha.backend.game.Time;
import com.three_stack.maximum_alpha.backend.game.effects.*;
import com.three_stack.maximum_alpha.backend.game.effects.events.SingleCardEvent;
import com.three_stack.maximum_alpha.backend.game.effects.events.SourceDamageTargetEvent;

import com.three_stack.maximum_alpha.backend.game.player.Player;
import com.three_stack.maximum_alpha.backend.game.utilities.Utility;
import io.gsonfire.annotations.ExposeMethodResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.three_stack.maximum_alpha.backend.game.ResourceList;

public abstract class NonSpellCard extends Card {
    protected final int health;
    protected int damageTaken;
    protected boolean dead;

    protected boolean exhausted;
    protected boolean refreshable;

    protected List<Buff> buffs;
    private transient int buffHealth;
    protected List<Ability> abilities;
    protected List<Tag> tags;
    protected List<CardClass> classes;
    protected boolean legendaryLock;

    protected NonSpellCard(String name, ResourceList cost, String text, String flavorText, int health) {
        super(name, cost, text, flavorText);
        this.health = health;
        damageTaken = 0;
        exhausted = false;
        refreshable = true;
        dead = false;
        tags = new ArrayList<>();
        classes = new ArrayList<>();
        buffs = new ArrayList<>();
        buffHealth = 0;
        legendaryLock = false;
    }

    public NonSpellCard(NonSpellCard other) {
        super(other);
        this.health = other.health;
        this.damageTaken = other.damageTaken;
        this.dead = other.dead;

        this.exhausted = other.exhausted;
        this.refreshable = other.refreshable;

        this.buffs = other.buffs;
        this.abilities = other.getAbilities().stream()
                .map(Ability::new)
                .collect(Collectors.toList());
        this.classes = Utility.copy(other.classes);
        this.tags = Utility.copy(other.tags);
    }

    public SourceDamageTargetEvent takeDamage(int damage, Card source, Time time, State state) {
        damageTaken += damage;
        checkDeathGeneral(time, state);
        //check if source is lethal
        if(!isDead() && damage > 0 && source instanceof Creature) {
            Creature creatureSource = (Creature) source;
            if(creatureSource.hasTag(Tag.TagType.LETHAL)) {
                die(time, state);
            }
        }
        return new SourceDamageTargetEvent(time, source, this, damage);
    }

    public SingleCardEvent die(Time time, State state) {
        setDead(true);
        SingleCardEvent deathEvent = new SingleCardEvent(time, "death", this);
        state.addEvent(deathEvent, Trigger.ON_DEATH);
        return deathEvent;
    }

    @ExposeMethodResult("currentHealth")
    public int getCurrentHealth() {
        return getMaxHealth() - damageTaken;
    }

    @ExposeMethodResult("maxHealth")
    public int getMaxHealth() {
        return health + buffHealth;
    }
    
    public void checkDeathGeneral(Time time, State state) {
        if(getCurrentHealth() <= 0) {
            die(time, state);
        }
    }
    
    public void setDead(boolean dead) {
    	this.dead = dead;
    }

    public boolean isDead() {
        return dead;
    }

    @Override
    public boolean isPlayable() {
        if(!super.isPlayable()) {
            return false;
        }

        if(hasTag(Tag.TagType.LEGENDARY) && isLegendaryLock()) {
            return false;
        }

        return true;
    }

    public void propagateLegendaryLock(boolean locked, State state) {
        state.getPlayingPlayers().stream()
                .map(Player::getAllCards)
                .flatMap(Collection::stream)
                .filter(playerCard -> playerCard instanceof NonSpellCard)
                .map(playerCard -> (NonSpellCard) playerCard)
                .filter(playerCard -> playerCard.hasTag(Tag.TagType.LEGENDARY))
                .filter(playerCard -> playerCard.getName().equals(getName()))
                .forEach(playerCard -> playerCard.setLegendaryLock(locked));
    }

    public void exhaust(Time time, State state) {
        if(exhausted) {
            //don't do anything
            return;
        }
        exhausted = true;
        state.createSingleCardEvent(this, "exhaust", time, Trigger.ON_EXHAUST);
    }

    public void attemptRefresh(Time time, State state) {
        if(refreshable && exhausted) {
            exhausted = false;
            state.createSingleCardEvent(this, "refresh", time, Trigger.ON_REFRESH);
        }
    }

    public int getHealth() {
        return health;
    }

    public boolean isExhausted() {
        return exhausted;
    }

    public void setExhausted(boolean exhausted) {
        this.exhausted = exhausted;
    }

    public boolean isRefreshable() {
        return refreshable;
    }

    public void setRefreshable(boolean refreshable) {
        this.refreshable = refreshable;
    }

    public List<Buff> getBuffs() {
        return buffs;
    }

    public List<Ability> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<Ability> abilities) {
        this.abilities = abilities;
    }

    public Ability getAbility(UUID abilityId) {
        List<Ability> matches =  abilities.stream()
                .filter(ability -> ability.getId().equals(abilityId))
                .collect(Collectors.toList());
        if(matches.size() == 1) {
            return matches.get(0);
        } else if(matches.size() < 1) {
            throw new IllegalArgumentException("No matching abilityId");
        } else {
            throw new IllegalStateException("Multiple abilities share the same id");
        }
    }

    public List<Buff> getNonAuraBuffs() {
    	return buffs.stream().filter(buff -> !buff.isAura()).collect(Collectors.toList());
    }

    public void addBuff(Buff buff, Time time, State state) {
    	buff.onAdd(this, state);
        buffs.add(buff);
        buffHealth += buff.getHealthModifier();
        checkDeathGeneral(time, state);
    }
    
    public void removeBuff(Buff buff, Time time, State state) {
    	int idx = buffs.indexOf(buff);
    	for(int i = buffs.size()-1; i >= idx; i--) {
    		Buff curr = buffs.get(i);
        	curr.onRemove(this, state);
    	}
		for(int i = idx+1; i < buffs.size(); i++) {
    		Buff curr = buffs.get(i);
        	curr.onAdd(this, state);
		}
		buffs.remove(buff);
        buffHealth -= buff.getHealthModifier();
        
    	checkDeathGeneral(time, state);
    }
    
    public void reset(State state) {
    	buffReset(state); //do first
        this.refreshable = true;
        this.damageTaken = 0;
        this.exhausted = false;
    }
    
    public void buffReset(State state) {
    	for(int i = buffs.size()-1; i >= 0; i--) {
    		Buff buff = buffs.get(i);
    		buff.onRemove(this, state);
    	}
    	buffs.clear();
    	buffHealth = 0;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags.stream()
                .forEach(this::processTagRemoval);
        this.tags = tags;
        tags.stream()
                .forEach(this::processTag);
    }

    public void addTag(Tag tag) {
        tags.add(tag);
        processTag(tag);
    }

    public abstract void processTag(Tag tag);

    public abstract void processTagRemoval(Tag tag);

    public List<CardClass> getClasses() {
        return classes;
    }

    public void setClasses(List<CardClass> classes) {
        this.classes = classes;
    }

    public boolean hasTag(Tag.TagType type) {
        return tags.stream()
                .anyMatch(tag -> tag.getType().equals(type));
    }

    public boolean isLegendaryLock() {
        return legendaryLock;
    }

    public void setLegendaryLock(boolean legendaryLock) {
        this.legendaryLock = legendaryLock;
    }
}
