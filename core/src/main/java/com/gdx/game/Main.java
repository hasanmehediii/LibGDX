package com.gdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.LinkedList;

public class Main extends ApplicationAdapter {
    private SpriteBatch spriteBatch;
    private Texture[] horseFrames;
    private Texture obstacleTexture;
    private Texture backgroundTexture;
    private BitmapFont font;
    private int currentFrame;
    private float frameTimer;
    private float horseX, horseY;
    private float velocityY;
    private boolean isJumping;

    private LinkedList<Float> obstacles;
    private float obstacleSpeed;

    private boolean gameOver;
    private float gameOverTimer;
    private float spawnTimer; // Timer to control the spawning of obstacles

    private float backgroundX1, backgroundX2; // For background movement

    private static final int HORSE_WIDTH = 200;
    private static final int HORSE_HEIGHT = 200;
    private static final int OBSTACLE_WIDTH = 48;
    private static final int OBSTACLE_HEIGHT = 48;
    private static final int GROUND_Y = 100;
    private static final float GRAVITY = -0.5f;
    private static final float JUMP_VELOCITY = 15;
    private static final float GAME_OVER_DELAY = 3f;
    private static final float OBSTACLE_SPAWN_INTERVAL = 2f; // Time interval to spawn new obstacle
    private static final float BACKGROUND_SPEED = 2f; // Speed of background movement

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();

        // Load horse animation frames
        horseFrames = new Texture[]{
            new Texture("h1.png"),
            new Texture("h2.png"),
            new Texture("h3.png"),
            new Texture("h4.png")
        };

        // Load obstacle and background textures
        obstacleTexture = new Texture("obstacle.png");
        backgroundTexture = new Texture("background.jpg");

        // Initialize game variables
        horseX = 400;
        horseY = GROUND_Y;
        velocityY = 0;
        isJumping = false;

        obstacles = new LinkedList<>();
        obstacleSpeed = 5; // Obstacles move right

        // Game over state variables
        gameOver = false;
        gameOverTimer = 0;

        // Initialize spawn timer
        spawnTimer = 0;

        // Initialize background positions
        backgroundX1 = 0;
        backgroundX2 = backgroundTexture.getWidth();

        // Start the first obstacle spawn
        spawnObstacle();
    }

    @Override
    public void render() {
        // Clear the screen
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Game over logic
        if (gameOver) {
            gameOverTimer += Gdx.graphics.getDeltaTime();
            spriteBatch.begin();
            spriteBatch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            font.draw(spriteBatch, "Game Over", Gdx.graphics.getWidth() / 2f - 50, Gdx.graphics.getHeight() / 2f);
            spriteBatch.end();

            if (gameOverTimer > GAME_OVER_DELAY) {
                Gdx.app.exit();
            }
            return;
        }

        // Handle user input and update game objects
        handleInput();
        updateGameObjects();

        // Update background positions
        updateBackground();

        // Draw everything on the screen
        spriteBatch.begin();
        // Draw the two backgrounds for the scrolling effect
        spriteBatch.draw(backgroundTexture, backgroundX1, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.draw(backgroundTexture, backgroundX2, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw the horse and obstacles
        spriteBatch.draw(horseFrames[currentFrame], horseX, horseY, HORSE_WIDTH, HORSE_HEIGHT);

        // Draw obstacles
        for (Float obstacleX : obstacles) {
            spriteBatch.draw(obstacleTexture, obstacleX, GROUND_Y, OBSTACLE_WIDTH, OBSTACLE_HEIGHT);
        }
        spriteBatch.end();
    }

    private void handleInput() {
        // Jumping logic: when SPACE is pressed and horse is on the ground
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && horseY == GROUND_Y) {
            isJumping = true;
            velocityY = JUMP_VELOCITY;
        }

        // Move left or right
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            horseX = Math.max(0, horseX - 5);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            horseX = Math.min(Gdx.graphics.getWidth() - HORSE_WIDTH, horseX + 5);
        }
    }

    private void updateGameObjects() {
        // Update animation frames
        frameTimer += Gdx.graphics.getDeltaTime();
        if (frameTimer > 0.1f) {
            currentFrame = (currentFrame + 1) % horseFrames.length;
            frameTimer = 0;
        }

        // Update horse's vertical position and apply gravity
        if (isJumping) {
            velocityY += GRAVITY;
            horseY += velocityY;
            if (horseY <= GROUND_Y) {
                horseY = GROUND_Y;
                isJumping = false;
                velocityY = 0;
            }
        }

        // Move obstacles to the right
        LinkedList<Float> newObstacles = new LinkedList<>();
        for (Float obstacleX : obstacles) {
            obstacleX += obstacleSpeed;
            if (obstacleX < Gdx.graphics.getWidth()) { // Keep obstacles within the screen
                newObstacles.add(obstacleX);
            }
        }
        obstacles = newObstacles;

        // Update the spawn timer and spawn new obstacles at fixed intervals
        spawnTimer += Gdx.graphics.getDeltaTime();
        if (spawnTimer >= OBSTACLE_SPAWN_INTERVAL) {
            spawnObstacle();
            spawnTimer = 0; // Reset the spawn timer
        }

        // Collision detection: check if horse collides with any obstacle
        for (Float obstacleX : obstacles) {
            if (horseX + HORSE_WIDTH > obstacleX && horseX < obstacleX + OBSTACLE_WIDTH && horseY < GROUND_Y + OBSTACLE_HEIGHT) {
                gameOver = true;
                break;
            }
        }
    }

    private void spawnObstacle() {
        // Spawn an obstacle at the left edge of the screen
        float x = 0;
        obstacles.add(x);
    }

    private void updateBackground() {
        // Move both background layers to create scrolling effect
        backgroundX1 += BACKGROUND_SPEED;
        backgroundX2 += BACKGROUND_SPEED;

        // If the first background layer moves completely off-screen, reset its position to the left side of the second layer
        if (backgroundX1 >= Gdx.graphics.getWidth()) {
            backgroundX1 = backgroundX2 - backgroundTexture.getWidth();
        }

        // If the second background layer moves completely off-screen, reset its position to the left side of the first layer
        if (backgroundX2 >= Gdx.graphics.getWidth()) {
            backgroundX2 = backgroundX1 - backgroundTexture.getWidth();
        }
    }

    @Override
    public void dispose() {
        // Dispose of all resources
        spriteBatch.dispose();
        font.dispose();
        for (Texture frame : horseFrames) {
            frame.dispose();
        }
        obstacleTexture.dispose();
        backgroundTexture.dispose();
    }
}
