package com.gdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.LinkedList;

public class Horse extends ApplicationAdapter {

    private SpriteBatch spriteBatch;
    private Texture[] horseFrames;
    private Texture obstacleTexture;
    private Texture backgroundTexture;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;

    private int currentFrame;
    private float frameTimer;
    private float horseX, horseY;
    private float velocityY;
    private boolean isJumping;

    private LinkedList<Float> obstacles;
    private float obstacleSpeed;

    private boolean gameOver;
    private float gameOverTimer;
    private float spawnTimer;

    private float backgroundX1;
    private float backgroundX2;
    private float backgroundSpeed;

    private static final int HORSE_WIDTH = 200;
    private static final int HORSE_HEIGHT = 200;
    private static final int OBSTACLE_WIDTH = 48;
    private static final int OBSTACLE_HEIGHT = 48;
    private static final int GROUND_Y = 100;
    private static final float GRAVITY = -0.5f;
    private static final float JUMP_VELOCITY = 15;
    private static final float GAME_OVER_DELAY = 3f;
    private static final float OBSTACLE_SPAWN_INTERVAL = 2f;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();

        // Load horse animation frames
        horseFrames = new Texture[]{
            new Texture("h1.png"),
            new Texture("h2.png"),
            new Texture("h3.png"),
            new Texture("h4.png")
        };

        // Load obstacle texture
        obstacleTexture = new Texture("obstacle.png");

        // Load background texture
        backgroundTexture = new Texture("b22.png");

        // Initialize game variables
        horseX = 400;
        horseY = GROUND_Y;
        velocityY = 0;
        isJumping = false;

        obstacles = new LinkedList<>();
        obstacleSpeed = 5;

        // Background variables
        backgroundX1 = 0;
        backgroundX2 = Gdx.graphics.getWidth();
        backgroundSpeed = 2;

        // Game over state variables
        gameOver = false;
        gameOverTimer = 0;
        spawnTimer = 0;

        // Start the first obstacle spawn
        spawnObstacle();
    }

    @Override
    public void render() {
        // Clear the screen
        Gdx.gl.glClearColor(0.5f, 0.8f, 1f, 1); // Sky blue color
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Game over logic
        if (gameOver) {
            gameOverTimer += Gdx.graphics.getDeltaTime();
            spriteBatch.begin();
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

        // Draw the background
        spriteBatch.begin();
        spriteBatch.draw(backgroundTexture, backgroundX1, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.draw(backgroundTexture, backgroundX2, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.end();

        // Draw game objects
        spriteBatch.begin();
        // Draw the horse
        spriteBatch.draw(horseFrames[currentFrame], horseX, horseY, HORSE_WIDTH, HORSE_HEIGHT);

        // Draw obstacles
        for (Float obstacleX : obstacles) {
            spriteBatch.draw(obstacleTexture, obstacleX, GROUND_Y, OBSTACLE_WIDTH, OBSTACLE_HEIGHT);
        }
        spriteBatch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && horseY == GROUND_Y) {
            isJumping = true;
            velocityY = JUMP_VELOCITY;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            horseX = Math.max(0, horseX - 5);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            horseX = Math.min(Gdx.graphics.getWidth() - HORSE_WIDTH, horseX + 5);
        }
    }

    private void updateGameObjects() {
        frameTimer += Gdx.graphics.getDeltaTime();
        if (frameTimer > 0.1f) {
            currentFrame = (currentFrame + 1) % horseFrames.length;
            frameTimer = 0;
        }

        if (isJumping) {
            velocityY += GRAVITY;
            horseY += velocityY;
            if (horseY <= GROUND_Y) {
                horseY = GROUND_Y;
                isJumping = false;
                velocityY = 0;
            }
        }

        LinkedList<Float> newObstacles = new LinkedList<>();
        for (Float obstacleX : obstacles) {
            obstacleX += obstacleSpeed; // Move right
            if (obstacleX <= Gdx.graphics.getWidth()) { // Keep obstacles within the screen
                newObstacles.add(obstacleX);
            }
        }
        obstacles = newObstacles;

        spawnTimer += Gdx.graphics.getDeltaTime();
        if (spawnTimer >= OBSTACLE_SPAWN_INTERVAL) {
            spawnObstacle();
            spawnTimer = 0;
        }

        for (Float obstacleX : obstacles) {
            if (horseX + HORSE_WIDTH > obstacleX && horseX < obstacleX + OBSTACLE_WIDTH && horseY < GROUND_Y + OBSTACLE_HEIGHT) {
                gameOver = true;
                break;
            }
        }

        // Update background positions
        backgroundX1 += backgroundSpeed;
        backgroundX2 += backgroundSpeed;
        if (backgroundX1 >= Gdx.graphics.getWidth()) {
            backgroundX1 = backgroundX2 - Gdx.graphics.getWidth();
        }
        if (backgroundX2 >= Gdx.graphics.getWidth()) {
            backgroundX2 = backgroundX1 - Gdx.graphics.getWidth();
        }

    }

    private void spawnObstacle() {
        obstacles.add(0f); // Spawn at the left of the screen
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        shapeRenderer.dispose();
        font.dispose();
        for (Texture frame : horseFrames) {
            frame.dispose();
        }
        obstacleTexture.dispose();
        backgroundTexture.dispose();
    }
}
