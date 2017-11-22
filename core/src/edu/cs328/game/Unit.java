package edu.cs328.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;

public abstract class Unit{
	public Sprite sprite;
	public Vector2 position;
	public int hp;
	public Rectangle bounds;
	public Vector2 movement;
	public float speed;
	public float width;
	public float height;
	public boolean wallCollide = true;
	public boolean atWall = false;

	public enum State{
		WALKING, ATTACK, HURT
	}
	public State state = State.WALKING;

	public enum Facing{
		UP, DOWN, LEFT, RIGHT
	}

	public Facing facing = Facing.UP;

	public Unit(){

	}

	public void setDimensions(){
		width = sprite.getTexture().getWidth()/16f;
		height = sprite.getTexture().getHeight()/16f;
	}

	public void render(Batch batch, float delta, TiledMap map){
		if(wallCollide){
			getCollision((TiledMapTileLayer)map.getLayers().get("walls"), delta);
			getCollision((TiledMapTileLayer)map.getLayers().get("treasure"), delta);
		}

		position.x += movement.x * speed * delta;
		position.y += movement.y * speed * delta;
		batch.draw(new TextureRegion(sprite.getTexture(), sprite.getRegionX(), sprite.getRegionY(), sprite.getRegionWidth(), sprite.getRegionHeight()), position.x, position.y, width, height);
		bounds = new Rectangle(position.x, position.y, width, height);
	}

	/* Get the collisions with the walls.
	 * Using a rectpool is more effective than
	 * using the cells. (I don't know why)
	 */
	
	public void getCollision(TiledMapTileLayer layer, float delta){
		Array<Rectangle> rectPool = new Array<Rectangle>();
		atWall = false;
		/* Get the tiles in a 3 tile radius. Using a smaller radius fails greatly! */
		for(int x = (int)position.x - 3; x < (int)position.x + 3; x++){
			for(int y = (int)position.y - 3; y < (int)position.y + 3; y++){
				Cell cell = layer.getCell(x,y);
				if(cell != null){
					rectPool.add(new Rectangle(x,y,1,1));
				}
			}
		}

		Rectangle xBounds = new Rectangle(position.x + (delta * movement.x * speed) + .1f, position.y +.1f, width - .2f, height - .2f);
		Rectangle yBounds = new Rectangle(position.x + .1f, position.y + (delta * movement.y * speed) + .1f, width -.2f, height - .2f);

		for(Rectangle tile : rectPool){
			if(xBounds.overlaps(tile)){
				movement.x = 0;
				atWall = true;
			}
			if(yBounds.overlaps(tile)){
				movement.y = 0;
				atWall = true;
			}
		}
	}
}