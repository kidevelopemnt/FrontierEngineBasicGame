package org.game.ecs;

import frontier.engine.ecs.components.Component;
import frontier.engine.ecs.components.physics.RigidBody;
import org.joml.Vector3f;

import java.util.Map;

public class SlidingBlock extends Component {
    private float speed = 5f;

    private Float bounds;
    private int[] scorePtr;

    @Override
    public void update(float deltaTime) {
        entity.getTransform().position.z -= speed * deltaTime;

        if (bounds != null && entity.getTransform().position.z < bounds) {
            scorePtr[0] += 1;
            entity.getScene().destroyEntity(entity);
        }
    }

    public void setBounds(float bounds) {
        this.bounds = bounds;
    }


    public void setScorePtr(int[] ptr) {
        scorePtr = ptr;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public Map<String, Object> serialize() {
        return Map.of();
    }
}
