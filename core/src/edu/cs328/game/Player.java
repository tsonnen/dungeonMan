package edu.cs328.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import java.lang.Math;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.graphics.Color;

public class Player extends Unit implements InputProcessor{
    private Animation<TextureRegion> upWalk;
    private Animation<TextureRegion> leftWalk;
    private Animation<TextureRegion> rightWalk;
    private Animation<TextureRegion> downWalk;
    private TextureRegion upAttack;
    private TextureRegion leftAttack;
    private TextureRegion rightAttack;
    private TextureRegion downAttack;
    private Texture spriteSheet;
    public Projectile projectile;
    private int dirX, dirY  = 0;
    public int attackDmg = 3;
    public int maxHp = 3;

    public Player(){
        speed = 2.5f;
        hp = maxHp;
        spriteSheet = new Texture(Gdx.files.internal("notlink.png"));
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, 16, 16);
        TextureRegion [] upWalkFrames = new TextureRegion[4];
        TextureRegion [] downWalkFrames =  new TextureRegion[4];
        TextureRegion [] leftWalkFrames =  new TextureRegion[4];
        TextureRegion [] rightWalkFrames =  new TextureRegion[4];

        Gdx.input.setInputProcessor(this);

        for(int i = 0; i < 4; i++){
            upWalkFrames[i] = tmp[0][i];
            leftWalkFrames[i] = tmp[2][i];
            rightWalkFrames[i] = tmp[1][i]; 
            downWalkFrames[i] = tmp[3][i];
        }

        upAttack = tmp[4][0];
        rightAttack = tmp[4][1];
        leftAttack = tmp[4][2];
        downAttack = tmp[4][3];

        upWalk = new Animation<TextureRegion>(.15f, new Array<TextureRegion>(upWalkFrames), PlayMode.LOOP);
        downWalk = new Animation<TextureRegion>(.15f, new Array<TextureRegion>(downWalkFrames), PlayMode.LOOP);
        leftWalk = new Animation<TextureRegion>(.15f, new Array<TextureRegion>(leftWalkFrames), PlayMode.LOOP);
        rightWalk = new Animation<TextureRegion>(.15f, new Array<TextureRegion>(rightWalkFrames), PlayMode.LOOP);

        sprite = new Sprite(upWalkFrames[0]);

        movement = new Vector2();
        width = height = 1f;
    }

    public void update(float delta){
        stateTime += delta;
        if(state == Unit.State.ATTACK && stateTime >= .15f){
            state = Unit.State.WALKING;
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
            movement.x = dirX * speed;
            movement.y = dirY * speed;
        }
        else if(state == State.ATTACK)
            movement.x = movement.y = 0;
        else if(state == Unit.State.HURT && stateTime >= 1f){
            stateTime = 0f;
            state = State.WALKING;
        }

        if(movement.x + movement.y != 0 && state != State.ATTACK){
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
    }

    @Override
    public void render(Batch batch, float delta, TiledMap map){
        if(wallCollide){
            getCollision((TiledMapTileLayer)map.getLayers().get("walls"), delta);
            //getCollision((TiledMapTileLayer)map.getLayers().get("treasure"), delta);
        }

        position.x += movement.x * speed * delta;
        position.y += movement.y * speed * delta;
        Color oldColor = batch.getColor();
         if(state == State.HURT){
            batch.setColor((float)Math.random(), (float)Math.random(), (float)Math.random(), 1);
        }
        batch.draw(new TextureRegion(sprite.getTexture(), sprite.getRegionX(), sprite.getRegionY(), sprite.getRegionWidth(), sprite.getRegionHeight()), position.x, position.y, width, height);
        batch.setColor(oldColor);
        bounds = new Rectangle(position.x, position.y, width, height);

        if(projectile != null){
            projectile.render(batch, delta, map);
            if(projectile.atWall || projectile.dead){
                projectile = null;
            }
        }
    }

    private void getFacing(){
        if(movement.x + movement.y == 0){
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

    public Rectangle getAttackBox(){
        if(state == State.ATTACK){
            switch(facing){
                case UP:
                    return new Rectangle(position.x, position.y, width, height * 2f);
                case LEFT:
                    return new Rectangle(position.x - width, position.y, width * 2f, height);
                case DOWN:
                    return new Rectangle(position.x, position.y - width, width, height * 2f);
                case RIGHT:
                    return new Rectangle(position.x, position.y, width * 2f, height);
                default:
                    return new Rectangle(position.x, position.y, width/4f, height/4f);
            }
        }
        return new Rectangle();
    }

    @Override 
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
        switch(keycode){
            case Keys.W:
                movement.y = speed;
                movement.x = 0;
                facing = Facing.UP;
                stateTime = 0;
                dirY = 1;
                break;
            case Keys.A:
                movement.x = -speed;
                movement.y = 0;
                facing = Facing.LEFT;
                stateTime = 0;
                dirX = -1;
                break;
            case Keys.S:
                movement.y = -speed;
                movement.x = 0;
                facing = Facing.DOWN;
                stateTime = 0;
                dirY = -1;
                break;
            case Keys.D:
                movement.x = speed;
                movement.y = 0;
                facing = Facing.RIGHT;
                stateTime = 0;
                dirX = 1;
                break;
            case Keys.L:
                state = State.ATTACK;
                stateTime = 0;
                switch (facing) {
                    case UP:
                        sprite.setRegion(upAttack);
                        break;
                    case LEFT:
                        sprite.setRegion(leftAttack);
                        break;
                    case DOWN:
                        sprite.setRegion(downAttack);
                        break;
                    case RIGHT:
                        sprite.setRegion(rightAttack);
                        break;
                    default:
                        break;                  
                }
                break;
            case Keys.K:
                if(projectile == null)
                    projectile = new Knife(position.x + width/2, position.y + height/2, facing);
                break;
            default:
                break;

        }
        return true;
    }
    
    @Override 
    public boolean keyUp (int keycode) {
        switch(keycode){
            case Keys.W:
                if(dirY == 1){
                    movement.x = dirX * speed;
                    movement.y = 0;
                    
                    dirY = 0;
                    getFacing();
                }
                break;
            case Keys.A:
                if(dirX == -1){
                    movement.y = dirY * speed;
                    movement.x = 0;

                    dirX = 0;
                    getFacing();
                }
                break;
            case Keys.S:
                if(dirY == -1){
                    movement.x = dirX * speed;
                    movement.y = 0;
                    
                    dirY = 0;
                    getFacing();
                }
                break;
            case Keys.D:
                if(dirX == 1){
                    movement.y = dirY * speed;
                    movement.x = 0;
    
                    dirX = 0;
                    getFacing();
                }
                break;
            default:
                break;

        }
        return false;
    }
    
    @Override 
    public boolean keyTyped (char character) {
        return false;
    }
}