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

public class Lancer extends Enemy{
    private Animation<TextureRegion> upWalk;
    private Animation<TextureRegion> leftWalk;
    private Animation<TextureRegion> rightWalk;
    private Animation<TextureRegion> downWalk;
    private Texture spriteSheet;
    private float stateTime = 0f;

    public Lancer(int x, int y){
        position = new Vector2();
        position.set(x,y);
        init();
    }

    public void init(){
        speed = 2f;
        movement = new Vector2();

        spriteSheet = new Texture(Gdx.files.internal("lancer.png"));
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, 16, 16);
        TextureRegion [] upWalkFrames = new TextureRegion[4];
        TextureRegion [] downWalkFrames =  new TextureRegion[4];
        TextureRegion [] leftWalkFrames =  new TextureRegion[4];
        TextureRegion [] rightWalkFrames =  new TextureRegion[4];

        for(int i = 0; i < 4; i++){
            upWalkFrames[i]     = tmp[1][i];
            leftWalkFrames[i]   = tmp[2][i];
            rightWalkFrames[i]  = tmp[0][i]; 
            downWalkFrames[i]   = tmp[3][i];
        }

        upWalk = new Animation<TextureRegion>(.15f, new Array<TextureRegion>(upWalkFrames), PlayMode.LOOP);
        downWalk = new Animation<TextureRegion>(.15f, new Array<TextureRegion>(downWalkFrames), PlayMode.LOOP);
        leftWalk = new Animation<TextureRegion>(.15f, new Array<TextureRegion>(leftWalkFrames), PlayMode.LOOP);
        rightWalk = new Animation<TextureRegion>(.15f, new Array<TextureRegion>(rightWalkFrames), PlayMode.LOOP);

        facing = Facing.UP;
        sprite = new Sprite(upWalkFrames[0]);
        width = height = 1f;
        newDirection();
    }

    public void update(float delta, float x, float y, float width, float height){
        stateTime += delta;
        if(movement.x + movement.y == 0 || Math.random() < .01){
            newDirection();
            stateTime = 0f;
        }
        else if(position.x > x + width || position.x < x || position.y > y + height  || position.y < y){
            movement.x *= -1;
            movement.y *= -1;
            getFacing();
        }

        switch(facing){
            case UP:
                sprite.setRegion(upWalk.getKeyFrame(stateTime, true));
                break;
            case LEFT:
                sprite.setRegion(leftWalk.getKeyFrame(stateTime, true));
                break;
            case DOWN:
                sprite.setRegion(downWalk.getKeyFrame(stateTime, true));
                break;
            case RIGHT:
                sprite.setRegion(rightWalk.getKeyFrame(stateTime, true));
                break;
            default:
                break;
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
        else{
            if(movement.y < 0)
                facing = Facing.DOWN;
            else
                facing = Facing.UP; 
        }
    }
}