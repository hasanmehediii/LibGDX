package com.gdx.game.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.gdx.game.MainMenuScreen;

public class Game {
    public static void main(String[] args) {
        createApplication();
    }

    private static void createApplication() {
        new Lwjgl3Application(new com.badlogic.gdx.Game() {
            @Override
            public void create() {
                this.setScreen(new MainMenuScreen());
            }
        }, getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();

        configuration.setTitle("LibGDX Game");
        configuration.useVsync(true);
        configuration.setForegroundFPS(60);
        configuration.setWindowedMode(1080, 800);
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");

        return configuration;
    }

}
