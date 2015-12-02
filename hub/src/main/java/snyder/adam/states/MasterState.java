/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package snyder.adam.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import snyder.adam.entity.Entity;

import java.util.*;


public abstract class MasterState extends BasicGameState {

    private final List<Collection<Entity>> layers = new ArrayList<>();
    private final Deque<Registration> entityRegisterQueue = new ArrayDeque<>();

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        // Render all registered entities, layer by layer, such that lower layers are rendered first:
        for (Collection<Entity> c : layers) {
            for (Entity e : c) {
                e.render(container, game, g);
            }
        }
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        // Update entity registrations:
        while (!entityRegisterQueue.isEmpty()) {
            Registration r = entityRegisterQueue.pop();
            if (r.register) {
                finalizeRegisterEntity(r);
            } else {
                finalizeUnregisterEntity(r);
            }
        }
        // Update all registered entities:
        for (Collection<Entity> c : layers) {
            for (Entity e : c) {
                e.update(container, game, delta);
            }
        }
    }

    /**
     * Queue an entity to be registered with the global update/render loop, on a specified layer
     * @param entity Entity to be registered
     * @param layer Layer the entity will be rendered on. Higher layers are rendered above lower layers.
     */
    protected void registerEntity(Entity entity, int layer) {
        entityRegisterQueue.add(new Registration(entity, layer));
    }

    /**
     * Queue a collection of entities to be registered with the global update/render loop, on a specified layer
     * @param entities Entities to be registered
     * @param layer Layer the entities will be rendered on. Higher layers are rendered above lower layers.
     */
    protected void registerEntities(Collection<Entity> entities, int layer) {
        for (Entity e : entities) {
            registerEntity(e, layer);
        }
    }

    /**
     * Remove an entity from the global update/render loop
     * @param entity Entity to be removed
     */
    protected void unregisterEntity(Entity entity) {
        entityRegisterQueue.add(new Registration(entity));
    }

    /**
     * Get all entities registered to a specified layer. Useful for retrieving groups of entities for collision
     * detection.
     * @param layer Layer to retrieve
     * @return Collection of Entity objects on the specified layer
     */
    protected Collection<Entity> getEntities(int layer) {
        if (layer < layers.size()) {
            return new HashSet<>(layers.get(layer));
        } else {
            return new HashSet<>();
        }
    }

    /**
     * Get all entities registered in the global update/render loop
     * @return Collection of all registered Entity objects
     */
    protected Collection<Entity> getEntities() {
        Collection<Entity> allEntities = new HashSet<>();
        for (Collection<Entity> c : layers) {
            allEntities.addAll(c);
        }
        return allEntities;
    }

    private void finalizeRegisterEntity(Registration registration) {
        // If the entity is already registered, remove it first:
        finalizeUnregisterEntity(registration);
        // Make sure the layer exists, and create it if it doesn't:
        while (layers.size() <= registration.layer) {
            layers.add(new HashSet<Entity>());
        }
        // Complete registration:
        layers.get(registration.layer).add(registration.entity);
    }

    private void finalizeUnregisterEntity(Registration registration) {
        for (Collection<Entity> c : layers) {
            while (c.contains(registration.entity)) {
                c.remove(registration.entity);
            }
        }
    }

    private class Registration {
        Entity entity;
        int layer;
        boolean register;

        // Constructor for register entity:
        public Registration(Entity entity, int layer) {
            this.entity = entity;
            this.layer = layer;
            this.register = true;
        }

        // Constructor for unregister entity:
        public Registration(Entity entity) {
            this.entity = entity;
            this.register = false;
        }
    }
}
