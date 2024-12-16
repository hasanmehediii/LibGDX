package com.gdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;

public class AboutScreen extends com.badlogic.gdx.ScreenAdapter {
    private Stage stage;
    private SpriteBatch batch;
    private Texture photoTexture;  // For the owner's photo
    private BitmapFont font;
    private Texture backgroundTexture; // Background image texture
    private Music backgroundMusic; // Background music
    private final Game game;

    public AboutScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        // Load the photo (owner's photo)
        try {
            photoTexture = new Texture("owner.png"); // Replace with your photo file
        } catch (Exception e) {
            Gdx.app.error("AboutScreen", "Error loading photo: " + e.getMessage());
        }

        // Load the background image
        try {
            backgroundTexture = new Texture("back.jpeg"); // Replace with your background image
        } catch (Exception e) {
            Gdx.app.error("AboutScreen", "Error loading background image: " + e.getMessage());
        }

        // Load the default font
        font = new BitmapFont();
        font.getData().setScale(2.0f);

        // Load and play background music
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3")); // Replace with your music file
        backgroundMusic.setLooping(true);
        backgroundMusic.play();

        // Create a table for the layout
        Table table = new Table();
        table.top().center();
        table.setFillParent(true);

        // Create a label style without using uiskin.json
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;

        // Create a label with your details
        String aboutText = "Hello, I'm Mehedi Hasan. This is my personal game project. "
            + "I am an undergraduate student at the University of Dhaka. I enjoy coding and game development.";
        Label aboutLabel = new Label(aboutText, labelStyle);
        aboutLabel.setWrap(true); // Enable text wrapping
        aboutLabel.setAlignment(Align.center);

        // Create a back button to return to the main menu
        TextButton backButton = new TextButton("BACK", getButtonStyle(font));
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                backgroundMusic.stop(); // Stop music when returning to main menu
                game.setScreen(new MainMenuScreen(game)); // Switch to MainMenuScreen
            }
        });

        // Center the content vertically
        table.defaults().align(Align.center); // Align all table contents to center

        // Add photo, text, and back button to the table
        table.add(new Image(photoTexture)).width(300).height(300).center().padBottom(10).row(); // Reduced padBottom for the image
        table.add(aboutLabel).expand().fill().center().padBottom(200).row(); // Reduced padBottom to move the text up
        table.add(backButton).padTop(20).expandX().fillX().center();


        // Add the table to the stage
        stage.addActor(table);
    }

    private TextButton.TextButtonStyle getButtonStyle(BitmapFont font) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = Color.WHITE;
        return style;
    }

    @Override
    public void render(float delta) {
        // Clear screen and draw the background and UI
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw the background image
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Full-screen background
        batch.end();

        // Draw the rest of the stage (i.e., text and photo)
        batch.begin();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
        batch.end();
    }

    @Override
    public void hide() {
        batch.dispose();
        if (photoTexture != null) {
            photoTexture.dispose();  // Dispose of the photo texture
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose(); // Dispose of the background texture
        }
        if (backgroundMusic != null) {
            backgroundMusic.stop(); // Stop the music when the screen is hidden
            backgroundMusic.dispose(); // Dispose of the music resource
        }
        font.dispose();
        stage.dispose();
    }
}
