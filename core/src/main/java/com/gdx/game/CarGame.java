package com.gdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.LinkedList;
import java.util.Random;

public class CarGame extends com.badlogic.gdx.ScreenAdapter {

    private final Texture carTexture;
    private final Texture leftCrowdTexture;
    private final Texture rightCrowdTexture;
    private final Texture yellowEnemyTexture;
    private final Texture blueEnemyTexture;
    private Texture pauseBackground;

    private BitmapFont font;
    private SpriteBatch spriteBatch;

    private ShapeRenderer shapeRenderer;

    private LinkedList<Float> laneLines;
    private LinkedList<Float> borders;
    private LinkedList<float[]> missiles;
    private LinkedList<Float> leftCrowdPositions;
    private LinkedList<Float> rightCrowdPositions;
    private LinkedList<Enemy> enemies;

    private float carX;
    private float carY;
    private float scrollSpeed;
    private final Random random;
    private boolean isPaused = false;

    private static final int CAR_WIDTH = 100;
    private static final int CAR_HEIGHT = 160;
    private static final int BORDER_WIDTH = 20;
    private static final int GRASS_WIDTH = 200;
    private static final int LANE_LINE_HEIGHT = 50;
    private static final int LANE_LINE_WIDTH = 5;
    private static final int MISSILE_WIDTH = 10;
    private static final int MISSILE_HEIGHT = 40;
    private static final int MISSILE_SPEED = 15;
    private static final int ENEMY_WIDTH = 100;
    private static final int ENEMY_HEIGHT = 160;

    private boolean gameOver;

    private int score = 0;
    private int highScore = 0;

    public CarGame(Texture carTexture, Texture leftCrowdTexture, Texture rightCrowdTexture, Texture yellowEnemyTexture, Texture blueEnemyTexture) {
        //this.game = game;
        this.carTexture = carTexture;
        this.leftCrowdTexture = leftCrowdTexture;
        this.rightCrowdTexture = rightCrowdTexture;
        this.yellowEnemyTexture = yellowEnemyTexture;
        this.blueEnemyTexture = blueEnemyTexture;
        this.random = new Random();
    }


    @Override
    public void show() {
        loadHighScore();
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        pauseBackground = new Texture("background.jpeg");

        carX = Gdx.graphics.getWidth() / 2f - CAR_WIDTH / 2f;
        carY = 100;

        scrollSpeed = 5;
        gameOver = false;

        laneLines = new LinkedList<>();
        borders = new LinkedList<>();
        missiles = new LinkedList<>();
        leftCrowdPositions = new LinkedList<>();
        rightCrowdPositions = new LinkedList<>();
        enemies = new LinkedList<>();

        for (int i = 0; i < Gdx.graphics.getHeight() / LANE_LINE_HEIGHT + 1; i++) {
            laneLines.add((float) (i * LANE_LINE_HEIGHT));
            borders.add((float) (i * LANE_LINE_HEIGHT));
            leftCrowdPositions.add((float) (i * LANE_LINE_HEIGHT));
            rightCrowdPositions.add((float) (i * LANE_LINE_HEIGHT));
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.3f, 0.6f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Game over screen
        if (gameOver) {
            drawGameOverScreen();
            return;
        }

        // Pause screen
        if (isPaused) {
            drawPauseScreen();
            return;
        }

        handleInput();
        updateGameObjects();

        // Draw road and background elements
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // Road
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 1);
        shapeRenderer.rect(GRASS_WIDTH + BORDER_WIDTH, 0,
            Gdx.graphics.getWidth() - 2 * (GRASS_WIDTH + BORDER_WIDTH), Gdx.graphics.getHeight());

        drawLaneLines();
        drawBorders();
        shapeRenderer.end();

        // Draw game objects
        spriteBatch.begin();
        drawCrowd();
        spriteBatch.draw(carTexture, carX, carY, CAR_WIDTH, CAR_HEIGHT);
        drawMissiles();
        drawEnemies();

        // Draw the current score
        font.getData().setScale(2);
        font.setColor(1, 1, 1, 1); // White color
        font.draw(spriteBatch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);

        spriteBatch.end();
    }


    private void handleInput() {
        if (gameOver) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            isPaused = !isPaused;
        }

        if (isPaused) return;

        float leftBoundary = GRASS_WIDTH + BORDER_WIDTH;
        float rightBoundary = Gdx.graphics.getWidth() - GRASS_WIDTH - BORDER_WIDTH - CAR_WIDTH;
        float bottomBoundary = 0;
        float topBoundary = Gdx.graphics.getHeight() - CAR_HEIGHT;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) carX = Math.max(carX - 10, leftBoundary);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) carX = Math.min(carX + 10, rightBoundary);
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) carY = Math.min(carY + 10, topBoundary);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) carY = Math.max(carY - 10, bottomBoundary);

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            missiles.add(new float[]{carX + CAR_WIDTH / 2f - MISSILE_WIDTH / 2f, carY + CAR_HEIGHT});
        }
    }

    private void updateGameObjects() {
        updateLaneLines();
        updateBorders();
        updateMissiles();
        updateCrowd();
        spawnEnemies();
        updateEnemies();
        checkCollisions();

        if (System.currentTimeMillis() % 10000 < 50) { // Every 10 seconds
            scrollSpeed += 0.5F;
        }
    }

    private void drawLaneLines() {
        shapeRenderer.setColor(1, 1, 1, 1);
        for (Float laneLineY : laneLines) {
            shapeRenderer.rect(Gdx.graphics.getWidth() / 2f - LANE_LINE_WIDTH / 2f,
                laneLineY, LANE_LINE_WIDTH, LANE_LINE_HEIGHT);
        }
    }

    private void drawBorders() {
        for (int i = 0; i < borders.size(); i++) {
            float borderY = borders.get(i);
            shapeRenderer.setColor(1, i % 2 == 0 ? 0 : 1, i % 2 == 0 ? 0 : 1, 1);
            shapeRenderer.rect(GRASS_WIDTH, borderY, BORDER_WIDTH, LANE_LINE_HEIGHT);
            shapeRenderer.rect(Gdx.graphics.getWidth() - GRASS_WIDTH - BORDER_WIDTH, borderY, BORDER_WIDTH, LANE_LINE_HEIGHT);
        }
    }

    private void drawMissiles() {
        for (float[] missile : missiles) {
            spriteBatch.draw(carTexture, missile[0], missile[1], MISSILE_WIDTH, MISSILE_HEIGHT);
        }
    }

    private void drawCrowd() {
        float crowdHeight = Gdx.graphics.getHeight();

        for (Float position : leftCrowdPositions) {
            spriteBatch.draw(leftCrowdTexture, 0, position, GRASS_WIDTH, crowdHeight);
        }

        for (Float position : rightCrowdPositions) {
            spriteBatch.draw(rightCrowdTexture, Gdx.graphics.getWidth() - GRASS_WIDTH, position, GRASS_WIDTH, crowdHeight);
        }
    }

    private void drawPauseScreen() {
        spriteBatch.begin();
        spriteBatch.draw(pauseBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        font.getData().setScale(3);
        font.draw(spriteBatch, "Game Paused. Press P to Resume", Gdx.graphics.getWidth() / 2f - 300, Gdx.graphics.getHeight() / 2f + 150);

        spriteBatch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            isPaused = !isPaused;
        }
    }

    private void drawEnemies() {
        for (Enemy enemy : enemies) {
            spriteBatch.draw(enemy.texture, enemy.x, enemy.y, ENEMY_WIDTH, ENEMY_HEIGHT);
        }
    }

    private void updateLaneLines() {
        LinkedList<Float> newLaneLines = new LinkedList<>();
        for (Float laneLineY : laneLines) {
            laneLineY -= scrollSpeed;
            if (laneLineY + LANE_LINE_HEIGHT > 0) {
                newLaneLines.add(laneLineY);
            }
        }

        while (newLaneLines.isEmpty() || newLaneLines.getLast() <= Gdx.graphics.getHeight()) {
            float newLineY = (newLaneLines.isEmpty() ? Gdx.graphics.getHeight() : newLaneLines.getLast() + LANE_LINE_HEIGHT * 2);
            newLaneLines.add(newLineY);
        }

        laneLines = newLaneLines;
    }

    private void updateBorders() {
        LinkedList<Float> newBorders = new LinkedList<>();
        for (Float borderY : borders) {
            borderY -= scrollSpeed;
            if (borderY > 0) newBorders.add(borderY);
        }
        borders = newBorders;

        // Add new border at the top if needed
        if (borders.isEmpty() || borders.getLast() <= Gdx.graphics.getHeight() - LANE_LINE_HEIGHT) {
            borders.add((float) Gdx.graphics.getHeight());
        }
    }

    private void updateMissiles() {
        LinkedList<float[]> updatedMissiles = new LinkedList<>();
        for (float[] missile : missiles) {
            missile[1] += MISSILE_SPEED;
            if (missile[1] <= Gdx.graphics.getHeight()) updatedMissiles.add(missile);
        }
        missiles = updatedMissiles;
    }

    private void updateCrowd() {
        LinkedList<Float> newLeftCrowdPositions = new LinkedList<>();
        LinkedList<Float> newRightCrowdPositions = new LinkedList<>();

        for (Float position : leftCrowdPositions) {
            position -= scrollSpeed;
            if (position + LANE_LINE_HEIGHT > 0) newLeftCrowdPositions.add(position);
        }

        for (Float position : rightCrowdPositions) {
            position -= scrollSpeed;
            if (position + LANE_LINE_HEIGHT > 0) newRightCrowdPositions.add(position);
        }

        while (newLeftCrowdPositions.isEmpty() || newLeftCrowdPositions.getLast() <= Gdx.graphics.getHeight()) {
            float newY = (newLeftCrowdPositions.isEmpty() ? Gdx.graphics.getHeight() : newLeftCrowdPositions.getLast() + LANE_LINE_HEIGHT);
            newLeftCrowdPositions.add(newY);
        }

        while (newRightCrowdPositions.isEmpty() || newRightCrowdPositions.getLast() <= Gdx.graphics.getHeight()) {
            float newY = (newRightCrowdPositions.isEmpty() ? Gdx.graphics.getHeight() : newRightCrowdPositions.getLast() + LANE_LINE_HEIGHT);
            newRightCrowdPositions.add(newY);
        }

        leftCrowdPositions = newLeftCrowdPositions;
        rightCrowdPositions = newRightCrowdPositions;
    }

    private void spawnEnemies() {
        if (random.nextInt(100) < 2) {
            if (enemies.isEmpty()) {
                Texture enemyTexture = random.nextBoolean() ? yellowEnemyTexture : blueEnemyTexture;
                float enemyX = random.nextInt(Gdx.graphics.getWidth() - 2 * (GRASS_WIDTH + BORDER_WIDTH) - ENEMY_WIDTH) + GRASS_WIDTH + BORDER_WIDTH;
                enemies.add(new Enemy(enemyX, Gdx.graphics.getHeight(), enemyTexture));
            }
        }
    }

    private void updateEnemies() {
        LinkedList<Enemy> updatedEnemies = new LinkedList<>();
        for (Enemy enemy : enemies) {
            enemy.y -= scrollSpeed + 3;
            if (enemy.y + ENEMY_HEIGHT > 0) {
                updatedEnemies.add(enemy);
            }
        }
        enemies = updatedEnemies;
    }

    private void checkCollisions() {
        LinkedList<Enemy> updatedEnemies = new LinkedList<>();
        LinkedList<float[]> updatedMissiles = new LinkedList<>();

        for (Enemy enemy : enemies) {
            boolean hit = false;

            // Check missile collision
            for (float[] missile : missiles) {
                if (missile[0] < enemy.x + ENEMY_WIDTH && missile[0] + MISSILE_WIDTH > enemy.x &&
                    missile[1] < enemy.y + ENEMY_HEIGHT && missile[1] + MISSILE_HEIGHT > enemy.y) {
                    hit = true;
                    score += 10; // Award score for hitting an enemy
                    break;
                } else {
                    updatedMissiles.add(missile);
                }
            }

            missiles = updatedMissiles;

            // Check car collision
            if (carX < enemy.x + ENEMY_WIDTH && carX + CAR_WIDTH > enemy.x &&
                carY < enemy.y + ENEMY_HEIGHT && carY + CAR_HEIGHT > enemy.y) {
                gameOver = true;
                saveHighScore();
                break;
            }

            // Add remaining enemies to the updated list
            if (!hit && enemy.y + ENEMY_HEIGHT > 0) {
                updatedEnemies.add(enemy);
            }
        }

        enemies = updatedEnemies;
    }


    private void drawGameOverScreen() {
        spriteBatch.begin();
        spriteBatch.draw(pauseBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        font.getData().setScale(5);
        font.setColor(1, 0, 0, 1); // Red color
        font.draw(spriteBatch, "Game Over!", Gdx.graphics.getWidth() / 2f - 200, Gdx.graphics.getHeight() / 2f + 250);
        font.getData().setScale(3);
        font.draw(spriteBatch, "Press R to Restart or Q to Quit", Gdx.graphics.getWidth() - 800, Gdx.graphics.getHeight() / 2f + 150);
        font.getData().setScale(2);
        font.draw(spriteBatch, "Your Score: " + score, Gdx.graphics.getWidth() - 600, Gdx.graphics.getHeight() / 2f + 90);
        spriteBatch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            gameOver = false;
            resetGame();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            Gdx.app.exit();
        }
    }

    // Save high score method
    private void saveHighScore() {
        try {
            FileHandle file = Gdx.files.local("highscore.txt");
            file.writeString(String.valueOf(highScore), false);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void loadHighScore() {
        try {
            FileHandle file = Gdx.files.local("highscore.txt");
            if (file.exists()) {
                highScore = Integer.parseInt(file.readString());
            }
        } catch (Exception e) {
            highScore = 0;
        }
    }

    private void resetGame() {
        carX = Gdx.graphics.getWidth() / 2f - CAR_WIDTH / 2f;
        carY = 100;

        enemies.clear();
        missiles.clear();
        laneLines.clear();
        borders.clear();
        leftCrowdPositions.clear();
        rightCrowdPositions.clear();

        for (int i = 0; i < Gdx.graphics.getHeight() / LANE_LINE_HEIGHT + 1; i++) {
            laneLines.add((float) (i * LANE_LINE_HEIGHT));
            borders.add((float) (i * LANE_LINE_HEIGHT));
            leftCrowdPositions.add((float) (i * LANE_LINE_HEIGHT));
            rightCrowdPositions.add((float) (i * LANE_LINE_HEIGHT));
        }

        scrollSpeed = 5;
        gameOver = false;
    }

    @Override
    public void hide() {
        shapeRenderer.dispose();
        spriteBatch.dispose();
        font.dispose();
    }

    private static class Enemy {
        float x, y;
        Texture texture;

        public Enemy(float x, float y, Texture texture) {
            this.x = x;
            this.y = y;
            this.texture = texture;
        }
    }
}
