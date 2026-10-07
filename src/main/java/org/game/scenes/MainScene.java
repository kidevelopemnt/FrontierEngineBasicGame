package org.game.scenes;

import frontier.engine.Engine;
import frontier.engine.assets.Material;
import frontier.engine.ecs.Entity;
import frontier.engine.ecs.components.Camera;
import frontier.engine.ecs.components.CameraController;
import frontier.engine.ecs.components.MeshRenderer;
import frontier.engine.ecs.components.physics.BoxCollider;
import frontier.engine.ecs.components.physics.RigidBody;
import frontier.engine.events.Event;
import frontier.engine.events.OnCollisionEntered;
import frontier.engine.events.UpdateEvent;
import frontier.engine.scene.Scene;
import org.game.ecs.PlayerCubeController;
import org.game.ecs.SlidingBlock;
import org.game.events.OnPlayerCollision;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.Random;

public class MainScene {
    private Engine engine;
    private Scene scene;

    private Random random;
    private float groundLength = 15f;
    private float groundWidth = 2f;

    private final float baseSpawnDelayMin = 0.5f;
    private final float baseSpawnDelayMax = 2.0f;
    // Limits to prevent the game from becoming literally impossible
    private final float minAllowedDelayMin = 0.15f;
    private final float minAllowedDelayMax = 0.4f;

    private float spawnDelay;
    private float timeSinceLastSpawn = 0f;
    private int[] score = { 0 };

    private Material groundMaterial;
    private Material blockMaterial;

    public MainScene(Engine engine) {
        this.engine = engine;
        scene = engine.createScene("Main Scene");
        random = new Random();

        initializeMaterials();
        setupScene();
    }

    private void initializeMaterials() {
        groundMaterial = new Material(engine.getAssets().loadTexture("textures/ground.jpg"));
        blockMaterial = new Material(engine.getAssets().loadTexture("textures/crate.jpg"));
    }

    private void setupScene() {
        // Camera
        scene.getCamera().getEntity().getTransform().position.set(0, 1f, groundLength / 2f - 1f);
        scene.getCamera().getEntity().getTransform().rotation.set(0, 180, 0);

        // Ground
        Entity ground = new Entity("Ground", scene);
        ground.addComponent(BoxCollider.class);
        MeshRenderer meshRenderer = ground.addComponent(MeshRenderer.class);
        try {
            meshRenderer.setModel(engine.getAssets().loadModel("models/cube.obj"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        meshRenderer.setMaterial(groundMaterial);

        ground.getTransform().position.set(0, 0, groundLength);
        ground.getTransform().scale.set(groundWidth, 0.1, groundLength);
        scene.addEntity(ground);

        // Player
        Entity playerCube = new Entity("Player", scene);
        BoxCollider playerCollider = playerCube.addComponent(BoxCollider.class);
        playerCollider.setTrigger(true);
        MeshRenderer playerRenderer = playerCube.addComponent(MeshRenderer.class);
        try {
            playerRenderer.setModel(engine.getAssets().loadModel("models/cube.obj"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        playerCube.addComponent(PlayerCubeController.class);

        playerCube.getTransform().scale.set(0.25f, 0.25f, 0.25f);
        playerCube.getTransform().position.set(new Vector3f(0, 0.25f, groundLength*0.51f));
        scene.addEntity(playerCube);

        // Block spawning
        spawnBlock();
        calculateNextSpawnDelay();

        engine.getEventBus().subscribe(null, UpdateEvent.class, this::update);
        engine.getEventBus().subscribe(null, OnPlayerCollision.class, this::showScore);
    }

    private void update(Event event) {
        float deltaTime = (float) event.getContext();

        timeSinceLastSpawn += deltaTime;

        if (timeSinceLastSpawn >= spawnDelay) {
            spawnBlock();
            timeSinceLastSpawn = 0f; // Reset the timer
            calculateNextSpawnDelay();
        }
    }

    private void spawnBlock() {
        Entity block = new Entity("Block", scene);
        BoxCollider collider = block.addComponent(BoxCollider.class);
        RigidBody rb = block.addComponent(RigidBody.class);

        MeshRenderer mr = block.addComponent(MeshRenderer.class);
        try {
            mr.setModel(engine.getAssets().loadModel("models/cube.obj"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mr.setMaterial(blockMaterial);

        block.getTransform().scale.set(0.25f, 0.25f, 0.25f);
        block.getTransform().position.set(random.nextFloat(-groundWidth/2, groundWidth/2), 0.5f, groundLength * 1.5f);
        SlidingBlock sliding = block.addComponent(SlidingBlock.class);
        sliding.setScorePtr(score);
        sliding.setSpeed((float) (5 + (1.5f * Math.sqrt(score[0]))));  // TODO: better gradual speed up
        sliding.setBounds(scene.getCamera().getEntity().getTransform().position.z);

        scene.addEntity(block);
    }

    private void calculateNextSpawnDelay() {
        // As score goes up, standard deviation and scale shrink using a square root modifier
        float scalingFactor = (float) Math.sqrt(score[0]);

        // Dynamically shrink the spawn windows
        float dynamicMin = baseSpawnDelayMin / (1.0f + 0.15f * scalingFactor);
        float dynamicMax = baseSpawnDelayMax / (1.0f + 0.20f * scalingFactor);

        // Clamp to prevent values from approaching 0 completely
        float finalMin = Math.max(dynamicMin, minAllowedDelayMin);
        float finalMax = Math.max(dynamicMax, minAllowedDelayMax);

        spawnDelay = random.nextFloat(finalMin, finalMax);
    }

    private void showScore(Event event) {
        engine.getLogger().logInfo("Score: " + score[0]);
    }

    public Scene getScene() {
        return scene;
    }
}
