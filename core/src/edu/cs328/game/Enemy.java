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
    public Projectile projectile = new Knife();
    public Vector2 id = new Vector2();
    public Enemy(){
        init();
    }

    public Enemy(int x, int y){
        position = new Vector2();
        position.set(x,y);
        init();
    }

    public void init(){
        hp = 10;
        speed = 2f;
        Texture texture = new Texture(Gdx.files.internal("devil.png"));
        sprite = new Sprite(texture);
        movement = new Vector2();
        setDimensions();
        newDirection();
        bounds = new Rectangle(position.x, position.y, width, height);
    }

    public void update(float delta, float x, float y, float width, float height){
        stateTime += delta;
        if((movement.x + movement.y == 0 || Math.random() < .01) && state != State.HURT){
            newDirection();
            stateTime = 0f;
        }
        else if((position.x + this. width > x + width || position.x < x || position.y + this.height > y + height  || position.y < y) && state != State.HURT){
            movement.x *= -1;
            movement.y *= -1;
        }
        else if(state == State.HURT){
            movement.set(0,0);
            if(stateTime >= .25f){
                state = State.WALKING;
                newDirection();
            }
        }
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