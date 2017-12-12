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

public class LifeHeart extends Item{

    public LifeHeart(float x, float y){
        Texture hearts = new Texture(Gdx.files.internal("lifeHeart.png"));
        textureRegion = new TextureRegion(hearts, 0, 0, 16, 16);
        width = height = 1f;
        position.set(x,y);
        bounds = new Rectangle(position.x, position.y, width, height);
    }
}