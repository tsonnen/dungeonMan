package edu.cs328.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import java.lang.Math;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;

public class Enemy extends Unit{
    private int awareness;
    public Enemy(){
        init();
    }

    public Enemy(int x, int y){
        position = new Vector2();
        position.set(x,y);
        init();
    }

    public void init(){
        speed = 2f;
        Texture texture = new Texture(Gdx.files.internal("devil.png"));
        sprite = new Sprite(texture);
        movement = new Vector2();
        awareness = 100;
        setDimensions();
        newDirection();
    }

    public void update(){
        if(movement.x + movement.y == 0 || Math.random() < .01){
            newDirection();
        }
    }

    public void newDirection(){
        double seed = Math.random();
        if(seed < .25){
            movement.set(speed, 0);
            facing = Facing.RIGHT;
        }
        else if(seed < .5){
            movement.set(-speed, 0);
            facing = Facing.LEFT;
        }
        else if(seed < .75){
            movement.set(0, speed);
            facing = Facing.UP;
        }
        else{
            movement.set(0, -speed);
            facing = Facing.DOWN;
        }
    }
}