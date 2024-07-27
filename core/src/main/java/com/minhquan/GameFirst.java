package com.minhquan;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.Sprite;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class GameFirst extends ApplicationAdapter implements InputProcessor {
    private SpriteBatch batch;
    private Texture ball;
    private Texture racket;
    private Texture background;

    private Button btnMusic;


    private float x = 140;
    private float y = 210;

    private Sound pingPong;
    private Sound fail;
    private Sound musicPath;

    private boolean moveLeft = false;
    private boolean moveRight = false;

    private float xR = 3500;

    private float speedX = 200;
    private float speedY = 150;

    private Stage stage;
    private Skin skin;

    private Label gameOverLabel;
    private Label gameStartLabel;
    private TextButton restartButton;
    private TextButton startButton;

    private boolean gameOver = false;
    private boolean gameStart = false;

    private int point = 0;
    private Label pointGame;

    private boolean music = true;
    
    @Override
    public void create() {
        
        batch = new SpriteBatch();
        // image = new Texture("libgdx.png");
        ball = new Texture("tennis.png");
        racket = new Texture("racket.gif");
        background = new Texture("back_chill.jpg");

        Gdx.input.setInputProcessor(this);


        pingPong = Gdx.audio.newSound(Gdx.files.internal("pingPong.mp3"));
        fail = Gdx.audio.newSound(Gdx.files.internal("fail.mp3"));
        musicPath = Gdx.audio.newSound(Gdx.files.internal("music.mp3"));
        musicPath.play();
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("tennisUI.json"));


        btnMusic = new Button(skin);
        btnMusic.setSize(70,70);
        btnMusic.setPosition(0, 0);
        stage.addActor(btnMusic);
        btnMusic.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playMusic();
            }
        });
        
        restartButton = new TextButton("Restart", skin);
        restartButton.setSize(200, 50);
        restartButton.setPosition(Gdx.graphics.getWidth() / 2f - restartButton.getWidth() / 2f,
                Gdx.graphics.getHeight() / 2f - 50);

        gameStartLabel = new Label("Tennis Game", skin);
        
        gameStartLabel.setPosition(Gdx.graphics.getWidth() / 2f - gameStartLabel.getWidth() / 2f ,
                Gdx.graphics.getHeight() / 2f + 50);



        startButton = new TextButton("Start", skin);
        startButton.setSize(200, 50);
        startButton.setPosition(Gdx.graphics.getWidth() / 2f - startButton.getWidth() / 2f,
                Gdx.graphics.getHeight() / 2f - 50);

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                xR = 350;
                gameStart = true;
                startGame();
            }
        });

        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                restartGame();
            }
        });

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(inputMultiplexer);

    }
    private void playMusic(){
        if(music == false){
            musicPath.resume();
            music = true;
        }else if ( music == true){
            musicPath.pause();
            music = false;
        }
    }
    private void pauseMusic(){
        musicPath.pause();
    }

    private void startGame(){
        startButton.remove();
        gameStartLabel.remove();
    }
    private void restartGame() {
        x = 140;
        y = 210;
        speedX = 200;
        speedY = 150;
        gameOver = false;
        stage.clear();

    }



    @Override
    public void render() {


        if (!gameOver && gameStart) {
            float deltaTime = Gdx.graphics.getDeltaTime();

            x += speedX * deltaTime;
            y += speedY * deltaTime;

            if (x < 0 || x + ball.getWidth() > Gdx.graphics.getWidth()) {
                speedX = -speedX;
            }
            if (y < 0) {
                speedY = -speedY;
                fail.play();
                gameOver = true;
                showGameOverScreen(point);
                point = 0;

            }
            if (y + ball.getHeight() > Gdx.graphics.getHeight()) {
                speedY = -speedY;
            }

            float moveSpeed = 300 * deltaTime;
            if (moveLeft) {
                xR -= moveSpeed;
            }
            if (moveRight) {
                xR += moveSpeed;
            }

            if (xR < 0) {
                xR = 0;
            }
            if (xR + racket.getWidth() > Gdx.graphics.getWidth()) {
                xR = Gdx.graphics.getWidth() - racket.getWidth();
            }
            if (isColliding(x, y, ball.getWidth()-20, ball.getHeight()-20, xR, 0, racket.getWidth()-50,racket.getHeight()-100)) {
                pingPong.play();
                speedX += -20;
                speedY += -20;
                System.out.println(speedY);
                speedY = -speedY;
                point += 1;
                pointGame.remove();
            }
        }else if(!gameOver){
            showGameStartScreen();
        }

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(ball, x, y);
        batch.draw(racket, xR, -50);


        pointGame = new Label(Integer.toString(point),skin);
        pointGame.setFontScale(1);
        pointGame.setPosition(Gdx.graphics.getWidth()-50, 0);
        

        showPoint();

        stage.draw();

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        ball.dispose();
        racket.dispose();
        pingPong.dispose();
        musicPath.dispose();
        fail.dispose();
        stage.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.LEFT) {
            moveLeft = true;
        }
        if (keycode == Input.Keys.RIGHT) {
            moveRight = true;
        }
        return true;

    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.LEFT) {
            moveLeft = false;
        }
        if (keycode == Input.Keys.RIGHT) {
            moveRight = false;
        }
        return true;
        // TODO Auto-generated method stub

    }

    @Override
    public boolean keyTyped(char character) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // TODO Auto-generated method stub

        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        // TODO Auto-generated method stub
        return true;
    }

    public boolean isColliding(float x1, float y1, float width1, float height1, float x2, float y2, float width2,
            float height2) {
        return x1 < x2 + width2 &&
                x1 + width1 > x2 &&
                y1 < y2 + height2 &&
                y1 + height1 > y2;
    }

    private void showPoint(){
        stage.addActor(pointGame);
    }
    private void showGameStartScreen() {

        stage.addActor(startButton);
        stage.addActor(gameStartLabel);
    }

    private void showGameOverScreen(int point) {
        gameOverLabel = new Label("Game over you had " + Integer.toString(point) +" points", skin);

        gameOverLabel.setPosition(Gdx.graphics.getWidth() / 2f - gameOverLabel.getWidth() / 2f -100,
                Gdx.graphics.getHeight() / 2f + 50);
        stage.addActor(gameOverLabel);
        stage.addActor(restartButton);
    }
}
