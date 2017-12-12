package edu.cs328.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;

public class DungeonMan extends Game implements InputProcessor {
    public ShapeRenderer shapeRenderer;
    public SpriteBatch spriteBatch;
    public Music music;
    public int dungeonsCleared = 0;

    public enum State{
        PLAYING, PAUSED
    }

    State gameState = State.PLAYING;
    
    public void create () {
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        this.setScreen(new MainMenuScreen(this));
    }

    public void render(float delta){
        super.render();
    }
    
    @Override
    public void dispose () {
        shapeRenderer.dispose();
        spriteBatch.dispose();
        music.dispose();
    }


     public boolean touchDown (int screenX, int screenY, int pointer, int button) {
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
       return false;
    }
    
    @Override 
    public boolean keyUp (int keycode) {
        return false;
    }
    
    @Override 
    public boolean keyTyped (char character) {
        switch(character){
            case '\t':
                if(gameState == State.PLAYING){
                    gameState = State.PAUSED;
                }else{
                    gameState = State.PLAYING;
                }
                break;
            default:
                break;
        }
        return false;
    }
}
