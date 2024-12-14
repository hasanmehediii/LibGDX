package com.gdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MainMenuScreen extends com.badlogic.gdx.ScreenAdapter {

    private Stage stage;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;

    @Override
    public void show() {
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());

        // Load the background image
        backgroundTexture = new Texture("background.jpg");

        // Get the default font with increased size
        font = getDefaultFontWithIncreasedSize();

        // Create a ShapeRenderer for custom drawing
        shapeRenderer = new ShapeRenderer();

        // Set up the stage input listener
        Gdx.input.setInputProcessor(stage);

        // Create and position the buttons
        createButtons();
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw the background image
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        // Draw the UI elements (buttons)
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    // Method to get the default font with increased size
    private BitmapFont getDefaultFontWithIncreasedSize() {
        BitmapFont font = new BitmapFont();
        font.getData().setScale(4.0f); // Scale the font by 2x to increase the size
        return font;
    }

    // Method to get button style with increased font size
    private TextButton.TextButtonStyle getButtonStyle(BitmapFont font) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;  // Use the default font with increased size
        style.fontColor = Color.WHITE;  // Set the font color to white
        return style;
    }

    // Create buttons for Start and Exit
    private void createButtons() {
        // Create a table to lay out the buttons
        Table table = new Table();
        table.top().center();
        table.setFillParent(true);

        // Create Start button with increased font size
        TextButton startButton = new TextButton("START", getButtonStyle(font));
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Action to take when Start button is clicked
                // game.setScreen(new GameScreen());  // You can change this to the game screen
            }
        });

        // Create Exit button with increased font size
        TextButton exitButton = new TextButton("EXIT", getButtonStyle(font));
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();  // Close the game when Exit button is clicked
            }
        });

        // Add the buttons to the table
        table.row().pad(20, 0, 20, 0);  // Increased padding for better visual separation
        table.add(startButton).fillX().uniformX();
        table.row().pad(20, 0, 20, 0);
        table.add(exitButton).fillX().uniformX();

        // Add the table to the stage
        stage.addActor(table);
    }

    @Override
    public void hide() {
        // Dispose of assets when the screen is hidden
        batch.dispose();
        backgroundTexture.dispose();
        font.dispose();
        shapeRenderer.dispose();
        stage.dispose();
    }
}
