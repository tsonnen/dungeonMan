package edu.cs328.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.InputProcessor;

public class HelpScreen implements Screen, InputProcessor {

    final DungeonMan game;
    private Texture helpTexture;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    public HelpScreen(final DungeonMan game) {
        this.game = game;
        helpTexture = new Texture(Gdx.files.internal("help.png"));
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480);
        camera.update();
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void show(){
    
    }
 
    @Override
    public void render(float delta) {
        // clear the screen ready for next set of images to be drawn
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(helpTexture, 0,0, 640, 480);
        batch.end();

    }
 
    @Override
    public void resize(int width, int height) {
    
    }
 
    @Override
    public void pause() {
        // TODO Auto-generated method stub
        
    }
 
    @Override
    public void resume() {
        // TODO Auto-generated method stub
        
    }
 
    @Override
    public void hide() {
        // TODO Auto-generated method stub
        
    }
 
    @Override
    public void dispose() {
        // dispose of assets when not needed anymore

    }

        @Override 
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        game.setScreen(new MainMenuScreen(game));
        return true;
    }

    @Override
    public boolean scrolled (int amount) {
        return false;
    }

    @Override 
    public boolean mouseMoved (int screenX, int screenY) {
        return false;
    }

    @Override 
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        return true;
    }

    @Override 
    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        return true;
    }

    @Override 
    public boolean keyDown (int keycode) {
        return true;
    }
    
    @Override 
    public boolean keyUp (int keycode) {
        return false;
    }
    
    @Override 
    public boolean keyTyped (char character) {
        return false;
    }
}