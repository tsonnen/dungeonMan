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
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.audio.Sound;

public class Whelp extends Enemy{
    private Animation<TextureRegion> leftWalk;
    private Animation<TextureRegion> rightWalk;
    private Texture spriteSheet;
    private float projectileTimer = 0f;
    private TextureRegion leftAttack;
    private TextureRegion rightAttack;
    private Sound fireBallSound;

    public Whelp(int x, int y){
        position = new Vector2();
        position.set(x,y);
        init();
    }

    public void init(){
        hp = 6;
        speed = 2f;
        attackDmg = 1;
        movement = new Vector2();
        id.set(position.x, position.y);
        projectile = new FireBall();

        fireBallSound = Gdx.audio.newSound(Gdx.files.internal("fireBall.mp3"));

        spriteSheet = new Texture(Gdx.files.internal("whelp.png"));
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, 32, 32);
        TextureRegion [] leftWalkFrames =  new TextureRegion[4];
        TextureRegion [] rightWalkFrames =  new TextureRegion[4];

        for(int i = 0; i < 4; i++){
            leftWalkFrames[i]   = tmp[1][i];
            rightWalkFrames[i]  = tmp[0][i]; 
        }

        leftWalk = new Animation<TextureRegion>(.15f, new Array<TextureRegion>(leftWalkFrames), PlayMode.LOOP);
        rightWalk = new Animation<TextureRegion>(.15f, new Array<TextureRegion>(rightWalkFrames), PlayMode.LOOP);
        leftAttack = tmp[1][4];
        rightAttack = tmp[0][4];
        facing = Facing.LEFT;
        sprite = new Sprite(leftWalkFrames[0]);
        newDirection();
        width = height = 1f;

        bounds = new Rectangle(position.x, position.y, width, height);
    }

    @Override
    public void update(float delta, float x, float y, float width, float height){
        stateTime += delta;
        projectileTimer += delta;
        if((movement.x + movement.y == 0 || Math.random() < .01) && state != State.HURT && projectile == null){
            newDirection();
            stateTime = 0f;
        }
        else if((position.x > x + width || position.x < x || position.y > y + height  || position.y < y) && state != State.HURT && projectile == null){
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


        if(!projectile.inAir){
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
        else{
            switch(facing){
                case LEFT:
                    sprite.setRegion(leftAttack);
                    break;
                case RIGHT:
                    sprite.setRegion(rightAttack);
                    break;
                default:
                    break;
            }
        }

        if(projectileTimer >= 1.5f && !projectile.inAir){
            movement.set(0,0);
            projectileTimer = 0f;
            projectile.setOrientation(position.x + .5f, position.y + .5f, facing);
            projectile.inAir = true;
            fireBallSound.play();
        }
        else if(projectile.inAir){
            projectile.update(delta, x, y, width, height);
            projectileTimer = 0f;
        }
    }

    @Override
    public void render(Batch batch, float delta, TiledMap map){
        if(wallCollide){
            getCollision((TiledMapTileLayer)map.getLayers().get("walls"), delta);
            //getCollision((TiledMapTileLayer)map.getLayers().get("treasure"), delta);
        }

        position.x += movement.x * speed * delta;
        position.y += movement.y * speed * delta;
        batch.draw(new TextureRegion(sprite.getTexture(), sprite.getRegionX(), sprite.getRegionY(), sprite.getRegionWidth(), sprite.getRegionHeight()), position.x, position.y, width, height);
        bounds = new Rectangle(position.x, position.y, width, height);


        if(projectile.inAir){
            projectile.render(batch, delta, map);
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

    /* There are no UP or DOWN animations, so facing should never be set to those */
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
}