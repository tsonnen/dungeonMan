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
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.utils.Array;

public class Boss extends Enemy{
    private Animation<TextureRegion> leftWalk;
    private Animation<TextureRegion> rightWalk;
    private Texture spriteSheet;

    public Boss(int x, int y){
        position = new Vector2();
        position.set(x,y);
        init();
    }

    public void init(){
        hp = 150;
        speed = 2f;
        movement = new Vector2();
        attackDmg = 3;
        id.set(position.x, position.y);

        spriteSheet = new Texture(Gdx.files.internal("boss.png"));
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, 32, 32);
        TextureRegion [] leftWalkFrames =  new TextureRegion[8];
        TextureRegion [] rightWalkFrames =  new TextureRegion[8];

        for(int i = 0; i < 8; i++){
            leftWalkFrames[i]   = tmp[1][i];
            rightWalkFrames[i]  = tmp[0][i]; 
        }

        leftWalk = new Animation<TextureRegion>(.15f, new Array<TextureRegion>(leftWalkFrames), PlayMode.LOOP);
        rightWalk = new Animation<TextureRegion>(.15f, new Array<TextureRegion>(rightWalkFrames), PlayMode.LOOP);

        facing = Facing.LEFT;
        sprite = new Sprite(leftWalkFrames[0]);
        newDirection();
        width = height = 2f;

        bounds = new Rectangle(position.x, position.y, width, height);
    }

    @Override
    public void update(float delta, float x, float y, float width, float height){
        stateTime += delta;
        if((movement.x + movement.y == 0 || Math.random() < .01) && state != State.HURT){
            newDirection();
            stateTime = 0f;
        }
        else if((position.x > x + width || position.x < x || position.y > y + height  || position.y < y) && state != State.HURT){
            movement.x *= -1;
            movement.y *= -1;
            getFacing();
        }
        else if(state == State.HURT){
            movement.set(0,0);
            if(stateTime >= .25f){
                state = State.WALKING;
                newDirection();
            }
        }


        switch(facing){
            case LEFT:
                sprite.setRegion(leftWalk.getKeyFrame(stateTime, true));
                break;
            case RIGHT:
                sprite.setRegion(rightWalk.getKeyFrame(stateTime, true));
                break;
            default:
                break;
        }
    }

    @Override
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
        }
        else{
            movement.set(0, -speed);
        }
    }

     private void getFacing(){
        if(movement.x + movement.y == 0){
            newDirection();
            return;
        }

        if(movement.x != 0){
            if(movement.x < 0)
                facing = Facing.LEFT;
            else
                facing = Facing.RIGHT;
        }
    }
}