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
		wallCollide = false;
		speed = 2f;
		Texture texture = new Texture(Gdx.files.internal("devil.png"));
		sprite = new Sprite(texture);
		movement = new Vector2();
		awareness = 10;
		setDimensions();
	}

	public Enemy(int x, int y){
		//wallCollide = false;
		speed = 2f;
		Texture texture = new Texture(Gdx.files.internal("devil.png"));
		sprite = new Sprite(texture);
		movement = new Vector2();
		awareness = 100;
		position = new Vector2();
		position.set(x,y);
		setDimensions();
	}

	public void setAwareness(int awareness){
		this.awareness = awareness;
	}

	public void update(float x, float y, TiledMapTileLayer layer){
		int dist = (int)Math.sqrt((double)(Math.pow((x - this.position.x),2) + Math.pow((y - position.y),2)));
		if(dist <= awareness){
			int xDif = (int)(x - position.x);
			int yDif = (int)(y - position.y);

			if(Math.abs(xDif) > Math.abs(yDif)){
				if(atWall){
					movement.y = speed * Integer.signum(yDif);
				}
				else{
					movement.x = speed * Integer.signum(xDif);
					movement.y = 0;
				}
			}
			else{
				if(atWall){
					movement.x = speed * Integer.signum(xDif);
				}
				else{
					movement.y = speed * Integer.signum(yDif);
					movement.x = 0;
				}
			}
		}
	}
}