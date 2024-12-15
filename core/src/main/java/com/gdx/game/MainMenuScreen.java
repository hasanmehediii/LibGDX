package com.gdx.game;

import com.badlogic.gdx.Game;  // Import Game class
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

    private final Game game; // Reference to the Game instance
    private Stage stage;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;

    // Constructor to receive the Game instance
    public MainMenuScreen(Game game) {
        this.game = game;
    }

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

    private BitmapFont getDefaultFontWithIncreasedSize() {
        BitmapFont font = new BitmapFont();
        font.getData().setScale(4.0f); // Scale the font to increase the size
        return font;
    }

    private TextButton.TextButtonStyle getButtonStyle(BitmapFont font) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;  // Use the default font with increased size
        style.fontColor = Color.WHITE;  // Set the font color to white
        return style;
    }

    private void createButtons() {
        Table table = new Table();
        table.top().center();
        table.setFillParent(true);

        TextButton startButton = new TextButton("START", getButtonStyle(font));
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new CarSelectionScreen(game));
            }
        });

        TextButton exitButton = new TextButton("EXIT", getButtonStyle(font));
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        table.row().pad(20, 0, 20, 0);
        table.add(startButton).fillX().uniformX();
        table.row().pad(20, 0, 20, 0);
        table.add(exitButton).fillX().uniformX();

        stage.addActor(table);
    }

    @Override
    public void hide() {
        batch.dispose();
        backgroundTexture.dispose();
        font.dispose();
        shapeRenderer.dispose();
        stage.dispose();
    }
}
