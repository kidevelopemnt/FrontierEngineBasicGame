package org.game.ecs;

import frontier.engine.ecs.components.Component;

import java.util.Map;

public class SlidingBlock extends Component {
    private float speed = 5f;

    private Float bounds;

    @Override
    public void update(float deltaTime) {
        entity.getTransform().position.z -= speed * deltaTime;

        if (bounds != null && entity.getTransform().position.z < bounds) {
            entity.getScene().destroyEntity(entity);
        }
    }

    public void setBounds(float bounds) {
        this.bounds = bounds;
    }

    @Override
    public Map<String, Object> serialize() {
        return Map.of();
    }
}
