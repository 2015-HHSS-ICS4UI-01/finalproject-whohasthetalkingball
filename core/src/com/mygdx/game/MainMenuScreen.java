/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.mygdx.game.input.GameInputs;
import com.mygdx.game.gamescreen.ScreenManager;
import com.mygdx.game.gamescreen.MyScreen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.model.LevelLoader;

/**
 *
 * @author CaiusDmitryJafer
 */
public class MainMenuScreen extends MyScreen {

    private Skin skin;
    private Stage stageMenu, stageLoad;
    private Table tableMenu, tableLoad;
    private TextureAtlas atlas;
    private InputMultiplexer inputMultiplexMain, inputMultiplexLoad;
    private TextButton startGame, saveGame, loadGame, slot1, slot2, slot3, returnToMenu, quitGame;
    private String slot1Text, slot2Text, slot3Text;
    private TextField inputSlot1, inputSlot2, inputSlot3;
    private Label notification;
    private boolean isSaving, isLoading;
    private InputMultiplexer lastUsedMultiplexer;
    private LevelLoader loader;

    public MainMenuScreen(ScreenManager gameStateManager) {
        super(gameStateManager);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {

        //If the user clicked save/load, render the loading/saving stage. else, render the main menu
        if (isLoading || isSaving) {
            stageLoad.act(delta);
            stageLoad.draw();
        } else {
            stageMenu.act(delta);
            stageMenu.draw();
        }
    }

    @Override
    public void init() {
        //create the stage for the main menu
        stageMenu = new Stage();
        //create the sage for the loading/saving menu
        stageLoad = new Stage();

        //create level loader, for saving and loading the world 
        loader = new LevelLoader();

        //to determine whether user wants to save or load levels
        //this is so that even though the same screen is rendered,
        //the logic is different depending on these booleans
        isLoading = false;
        isSaving = false;

        //the saved text describing the 3 slots of load/save, made whatever the title saved in the save files
        slot1Text = loader.getSlotName(0);
        slot2Text = loader.getSlotName(1);
        slot3Text = loader.getSlotName(2);

        //Input multiplexer, giving priority to stageMenu over gameinput
        inputMultiplexMain = new InputMultiplexer(stageMenu, MyGdxGame.gameInput);
        inputMultiplexLoad = new InputMultiplexer(stageLoad, MyGdxGame.gameInput);

        //set the processor to the initial default one
        lastUsedMultiplexer = inputMultiplexMain;

        // set the input multiplexer as the input processor
        Gdx.input.setInputProcessor(inputMultiplexMain);

        //initialize skin by imlpementing the json file that implements the atlas
        // the json file has the buttonstyle, sliderstyle, etc already coded into it, only need to call the name to use it
        skin = new Skin(Gdx.files.internal("ui-data/uiskin.json"));

        // Create a table that fills the screen, the buttons, etc go into this table
        // Align to top center
        tableMenu = new Table();
        stageMenu.addActor(tableMenu);
        tableMenu.setFillParent(true);
        tableMenu.align(Align.center);

        //create the buttons for the table implemented in main menu
        startGame = new TextButton("Start Game", skin, "default");
        saveGame = new TextButton("Save Game", skin, "default");
        loadGame = new TextButton("Load Game", skin, "default");
        quitGame = new TextButton("Quit Game", skin);

        //add the buttons to the table
        //padding to make the buttons more spaced out
        tableMenu.add(startGame).pad(MyGdxGame.HEIGHT / 3, 20, 20, 20);
        tableMenu.row();
        tableMenu.add(saveGame).pad(20, 20, 20, 20);
        tableMenu.row();
        tableMenu.add(loadGame).pad(20, 20, 20, 20);
        tableMenu.row();
        tableMenu.add(quitGame);

        // Create a table that fills the screen, the buttons, etc go into this table
        tableLoad = new Table();
        stageLoad.addActor(tableLoad);
        tableLoad.setFillParent(true);
        tableLoad.align(Align.center);

        //create the buttons for table 2
        slot1 = new TextButton("Slot 1:\n\n" + "'" + slot1Text + "'", skin, "default");
        slot2 = new TextButton("Slot 2:\n\n" + "'" + slot2Text + "'", skin, "default");
        slot3 = new TextButton("Slot 3:\n\n" + "'" + slot3Text + "'", skin, "default");
        returnToMenu = new TextButton("Return to Menu", skin);
        notification = new Label("Input level title \n then click on the corresponding slot", skin);
        inputSlot1 = new TextField(slot1Text, skin);
        inputSlot2 = new TextField(slot2Text, skin);
        inputSlot3 = new TextField(slot3Text, skin);

        //add the buttons to the table
        tableLoad.add(returnToMenu).padTop(20);
        tableLoad.add(notification).size(100, 30);
        tableLoad.row();
        tableLoad.add(slot1).pad(20, 20, 20, 20).size(200, 100);
        tableLoad.add(inputSlot1).padTop(30);
        tableLoad.row();
        tableLoad.add(slot2).pad(20, 20, 20, 20).size(200, 100);
        tableLoad.add(inputSlot2);
        tableLoad.row();
        tableLoad.add(slot3).pad(20, 20, 0, 20).size(200, 100);
        tableLoad.add(inputSlot3);
        tableLoad.row();

    }

    @Override
    public void update(float deltaTime) {
        if (Gdx.input.getInputProcessor() != lastUsedMultiplexer) {
            Gdx.input.setInputProcessor(lastUsedMultiplexer);
        }
        //if the user loaded a level, give them the option of going to game. if they haven't loaded, let them go to main menu
        if (notification.isVisible() && notification.getText().charAt(0) == 'L') {
            returnToMenu.setText("Go to Game");
        } else if (!notification.isVisible() && !returnToMenu.getText().equals("Return to Menu")) {
            returnToMenu.setText("Return to Menu");
        }
        processInput();
    }

    @Override
    public void processInput() {
        //to easily switch between main menu and main game using the ESC key
        if (GameInputs.isKeyJustPressed(GameInputs.Keys.ESCAPE)) {
            gameStateManager.setGameScreen(ScreenManager.GameScreens.MAIN_GAME);
        }
        //if the Start Game button is pressed
        if (startGame.isPressed()) {
            startGame.setText("Resume Game"); //set the text to resume game, in case user returns to menu
            startGame.setChecked(false); //uncheck the button
            gameStateManager.setGameScreen(ScreenManager.GameScreens.MAIN_GAME); //change screens to game screen
        }
        //if the Save Game button is pressed
        if (saveGame.isPressed()) {
            saveGame.setChecked(false); //uncheck the button
            isSaving = true; // this means that the user wants to save
            Gdx.input.setInputProcessor(inputMultiplexLoad);
            lastUsedMultiplexer = inputMultiplexLoad;
        }
        if (loadGame.isPressed()) {
            loadGame.setChecked(false);
            notification.setVisible(false);
            isLoading = true;
            Gdx.input.setInputProcessor(inputMultiplexLoad);
            lastUsedMultiplexer = inputMultiplexLoad;
        }
        if (slot1.isPressed()) {
            if (isSaving) {
                slot1.setText("Slot 1:\n\n" + "'" + slot1Text + "'");
                notification.setText("Saved to slot 1");
                loader.saveLevel(0, slot1Text);

            } else if (isLoading) {
                notification.setText("Loaded slot 1");
                loader.loadLevel(0);
                slot1Text = loader.getSlotName(0);
                slot1.setText("Slot 1:\n\n" + "'" + slot1Text + "'");
            }
        }
        if (slot2.isPressed()) {
            if (isSaving) {
                slot2.setText("Slot 2:\n\n" + "'" + slot2Text + "'");
                notification.setText("Saved to slot 2");
                loader.saveLevel(1, slot2Text);

            } else if (isLoading) {
                notification.setText("Loaded slot 2");
                loader.loadLevel(1);
            }
        }
        if (slot3.isPressed()) {
            if (isSaving) {
                slot3.setText("Slot 3:\n\n" + "'" + slot3Text + "'");
                notification.setText("Saved to slot 3");
                loader.saveLevel(2, slot3Text);

            } else if (isLoading) {
                notification.setText("Loaded slot 3");
                loader.loadLevel(2);
            }
        }
        if (returnToMenu.isPressed()) {
            if (notification.isVisible() && notification.getText().charAt(0) == 'L') {
                gameStateManager.setGameScreen(ScreenManager.GameScreens.MAIN_GAME);
                Gdx.input.setInputProcessor(inputMultiplexMain);
                lastUsedMultiplexer = inputMultiplexMain;
                isLoading = false;
                isSaving = false;
            } else {
                Gdx.input.setInputProcessor(inputMultiplexMain);
                lastUsedMultiplexer = inputMultiplexMain;
                isLoading = false;
                isSaving = false;
            }

        }

    }

    public void addInputs() {

//        startGame.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                startGame.setText("Resume Game");
//                startGame.setChecked(false);
//                gameStateManager.setGameScreen(ScreenManager.GameScreens.MAIN_GAME);
//
//            }
//        });
//        saveGame.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                saveGame.setChecked(false);
//                notification.setVisible(false);
//                isSaving = true;
//                Gdx.input.setInputProcessor(inputMultiplexLoad);
//                lastUsedMultiplexer = inputMultiplexLoad;
//            }
//        });
//        loadGame.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                loadGame.setChecked(false);
//                notification.setVisible(false);
//                isLoading = true;
//                Gdx.input.setInputProcessor(inputMultiplexLoad);
//                lastUsedMultiplexer = inputMultiplexLoad;
//            }
//        });
//        slot1.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                if (isSaving) {
//                    slot1.setText("Slot 1:\n\n" + "'" + slot1Text + "'");
//                    notification.setVisible(true);
//                    notification.setText("Saved to slot 1");
//                    loader.saveLevel(0, slot1Text);
//
//                } else if (isLoading) {
//                    notification.setVisible(true);
//                    notification.setText("Loaded slot 1");
//                    loader.loadLevel(0);
//                    slot1Text = loader.getSlotName(0);
//                    slot1.setText("Slot 1:\n\n" + "'" + slot1Text + "'");
//                }
//            }
//        });
//        slot2.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                if (isSaving) {
//                    slot2.setText("Slot 2:\n\n" + "'" + slot2Text + "'");
//                    notification.setVisible(true);
//                    notification.setText("Saved to slot 2");
//                    loader.saveLevel(1, slot2Text);
//
//                } else if (isLoading) {
//                    notification.setVisible(true);
//                    notification.setText("Loaded slot 2");
//                    loader.loadLevel(1);
//                }
//            }
//        });
//        slot3.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                if (isSaving) {
//                    slot3.setText("Slot 3:\n\n" + "'" + slot3Text + "'");
//                    notification.setVisible(true);
//                    notification.setText("Saved to slot 3");
//                    loader.saveLevel(2, slot3Text);
//
//                } else if (isLoading) {
//                    notification.setVisible(true);
//                    notification.setText("Loaded slot 3");
//                    loader.loadLevel(2);
//                }
//            }
//        });
//        returnToMenu.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                if (notification.isVisible() && notification.getText().charAt(0) == 'L') {
//                    gameStateManager.setGameScreen(ScreenManager.GameScreens.MAIN_GAME);
//                    Gdx.input.setInputProcessor(inputMultiplexMain);
//                    lastUsedMultiplexer = inputMultiplexMain;
//                    isLoading = false;
//                    isSaving = false;
//                } else {
//                    Gdx.input.setInputProcessor(inputMultiplexMain);
//                    lastUsedMultiplexer = inputMultiplexMain;
//                    isLoading = false;
//                    isSaving = false;
//                }
//
//            }
//        });
    }

    @Override
    public void resize(int width, int height) {
        stageMenu.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}
