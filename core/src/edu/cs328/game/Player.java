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
import com.badlogic.gdx.audio.Sound;

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
    public Projectile projectile = new Knife();
    private int dirX, dirY  = 0;
    public int maxHp = 6;
    public int numKnife = 10;
    private Sound deflect;
    private Sound melee1;
    private Sound melee2;
    private Sound melee3;

    public Player(){
        speed = 2.5f;
        hp = maxHp;
        attackDmg = 6;
        deflect = Gdx.audio.newSound(Gdx.files.internal("deflect.mp3"));
        melee1 = Gdx.audio.newSound(Gdx.files.internal("melee1.mp3"));
        melee2 = Gdx.audio.newSound(Gdx.files.internal("melee2.mp3"));
        melee3 = Gdx.audio.newSound(Gdx.files.internal("melee3.mp3"));
        hitSound = Gdx.audio.newSound(Gdx.files.internal("hit.mp3"));
        
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

        bounds = new Rectangle(position.x, position.y, width, height);
    }

    public void update(float delta, float x, float y, float width, float height){
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
        else if(state == Unit.State.HURT){
            if(stateTime >= .5f){
                stateTime = 0f;
                state = State.WALKING;
            }
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

        if(projectile != null)
            projectile.update(delta, x, y, width, height);
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

        if(projectile.inAir){
            projectile.render(batch, delta, map);
        }
    }

    /* Allow for the player to block the projectile */
    @Override
    public void takeHit(int dmg, Facing hitFacing){
        switch(hitFacing){
            case UP:
                if(facing == Facing.DOWN){
                    deflect.play();
                    return;
                }
                break;
            case DOWN:
                if(facing == Facing.UP){
                    deflect.play();
                    return;
                }
                break;
            case LEFT:
                if(facing == Facing.RIGHT){
                    deflect.play();
                    return;
                }
                break;
            case RIGHT:
                if(facing == Facing.LEFT){
                    deflect.play();
                    return;
                }
                break;
            default:
                break;
        }
        hp -= dmg;
        state = State.HURT;
        stateTime = 0f;
        if(hitSound != null)
            hitSound.play();
    }

    public void Die(){
        facing = Facing.UP;
        state = State.WALKING;
        movement.set(0,0);
        dirX = dirY = 0;

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
        double seed = Math.random();
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
                
                if(seed < .33){
                    melee1.play(1f, (float)(Math.random() * 2 + .5), -1);
                }else if(seed < .66){
                    melee2.play(1f, (float)(Math.random() * 2 + .5), -1);
                }else{
                    melee3.play(1f, (float)(Math.random() * 2 + .5), -1);
                }
                break;
            case Keys.K:
                if(!projectile.inAir && numKnife > 0){
                    projectile.setOrientation(position.x + width/2, position.y + height/2, facing);
                    projectile.inAir = true;
                    numKnife--;
                    if(seed < .33){
                        melee1.play(1f, (float)(Math.random() * 2 + .5), -1);
                    }else if(seed < .66){
                        melee2.play(1f, (float)(Math.random() * 2 + .5), -1);
                    }else{
                        melee3.play(1f, (float)(Math.random() * 2 + .5), -1);
                    }
                }
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