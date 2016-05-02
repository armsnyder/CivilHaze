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
    private boolean layersLock = false;

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        // Render all registered entities, layer by layer, such that lower layers are rendered first:
        layersLock = true;
        for (Collection<Entity> c : layers) {
            for (Entity e : c) {
                e.render(container, game, g);
            }
        }
        layersLock = false;
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        // Update entity registrations:
        while (!entityRegisterQueue.isEmpty()) {
            Registration r = entityRegisterQueue.pop();
            if (r.register) {
                finalizeRegisterEntity(r.entity, r.layer);
            } else {
                finalizeUnregisterEntity(r.entity);
            }
        }
        // Update all registered entities:
        layersLock = true;
        for (Collection<Entity> c : layers) {
            for (Entity e : c) {
                e.update(container, game, delta);
            }
        }
        layersLock = false;
    }

    /**
     * Queue an entity to be registered with the global update/render loop, on a specified layer
     * @param entity Entity to be registered
     * @param layer Layer the entity will be rendered on. Higher layers are rendered above lower layers.
     */
    public Entity registerEntity(Entity entity, int layer) {
        if (entity != null) {
            if (layersLock) {
                entityRegisterQueue.add(new Registration(entity, layer));
            } else {
                finalizeRegisterEntity(entity, layer);
            }
        }
        return entity;
    }

    /**
     * Queue a collection of entities to be registered with the global update/render loop, on a specified layer
     * @param entities Entities to be registered
     * @param layer Layer the entities will be rendered on. Higher layers are rendered above lower layers.
     */
    public void registerEntities(Collection<Entity> entities, int layer) {
        for (Entity e : entities) {
            registerEntity(e, layer);
        }
    }

    /**
     * Remove an entity from the global update/render loop
     * @param entity Entity to be removed
     */
    public void unregisterEntity(Entity entity) {
        if (layersLock) {
            entityRegisterQueue.add(new Registration(entity));
        } else {
            finalizeUnregisterEntity(entity);
        }
    }

    /**
     * Get all entities registered to a specified layer. Useful for retrieving groups of entities for collision
     * detection.
     * @param layer Layer to retrieve
     * @return Collection of Entity objects on the specified layer
     */
    public Collection<Entity> getEntities(int layer) {
        if (layer < layers.size()) {
            return new HashSet<>(layers.get(layer));
        } else {
            return new HashSet<>();
        }
    }

    /**
     * Get all entities registered to any of the specified layers. Useful for retrieving groups of entities for
     * collision detection.
     * @param layers Array of layers to retrieve
     * @return Collection of Entity objects on any of the specified layers (concatenated)
     */
    public Collection<Entity> getEntities(int[] layers) {
        HashSet<Entity> result = new HashSet<>();
        for (int i : layers) {
            result.addAll(getEntities(i));
        }
        return result;
    }

    /**
     * Get all entities registered in the global update/render loop
     * @return Collection of all registered Entity objects
     */
    public Collection<Entity> getEntities() {
        Collection<Entity> allEntities = new HashSet<>();
        for (Collection<Entity> c : layers) {
            allEntities.addAll(c);
        }
        return allEntities;
    }

    private void finalizeRegisterEntity(Entity entity, int layer) {
        // If the entity is already registered, remove it first:
        finalizeUnregisterEntity(entity);
        // Make sure the layer exists, and create it if it doesn't:
        while (layers.size() <= layer) {
            layers.add(new HashSet<Entity>());
        }
        // Complete registration:
        layers.get(layer).add(entity);
    }

    private void finalizeUnregisterEntity(Entity entity) {
        for (Collection<Entity> c : layers) {
            while (c.contains(entity)) {
                c.remove(entity);
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
