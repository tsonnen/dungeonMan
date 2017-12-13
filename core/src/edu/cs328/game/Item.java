package edu.cs328.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;

public abstract class Item{
    public TextureRegion textureRegion;
    public Vector2 position = new Vector2();
    public Rectangle bounds;
    public float width;
    public float height;

    public Item(){
    }

    /* Place the Unit without regard to the a map */
    public void render(Batch batch){
        batch.draw(textureRegion, position.x, position.y, width, height);
    }

    public void dispose(){
    }
}