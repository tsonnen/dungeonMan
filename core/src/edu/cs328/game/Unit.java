package edu.cs328.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;


public abstract class Unit{
	public Texture texture;
	public Vector2 position;
	public int hp;
	public Rectangle bounds;
	public Vector2 movement;
	public float speed;
	public float width;
	public float height;

	public Unit(){

	}

	public void render(Batch batch, float delta, TiledMapTileLayer layer){
		getCollision(layer, delta);
		position.x += movement.x * speed * delta;
		position.y += movement.y * speed * delta;
		batch.draw(texture, position.x, position.y, texture.getWidth()/16f, texture.getHeight()/16f);
		bounds = new Rectangle(position.x, position.y, texture.getWidth()/16f, texture.getHeight()/16f);
	}

	/* Get the collisions with the walls.
	 * Using a rectpool is more effective than
	 * using the cells. (I don't know why)
	 */
	
	public void getCollision(TiledMapTileLayer layer, float delta){
		Array<Rectangle> rectPool = new Array<Rectangle>();
		/* Get the tiles in a 3 tile radius. Using a smaller radius fails greatly! */
		for(int x = (int)position.x - 3; x < (int)position.x + 3; x++){
			for(int y = (int)position.y - 3; y < (int)position.y + 3; y++){
				Cell cell = layer.getCell(x,y);
				if(cell != null){
					rectPool.add(new Rectangle(x,y,1,1));
				}
			}
		}

		Rectangle xBounds = new Rectangle(position.x + (delta * movement.x * speed) + .1f, position.y +.1f, texture.getWidth()/16f - .2f, texture.getHeight()/16f - .2f);
		Rectangle yBounds = new Rectangle(position.x + .1f, position.y + (delta * movement.y * speed) + .1f, texture.getWidth()/16f -.2f, texture.getHeight()/16f - .2f);

		for(Rectangle tile : rectPool){
			if(xBounds.overlaps(tile)){
				movement.x = 0;
			}
			if(yBounds.overlaps(tile)){
				movement.y = 0;
			}
		}
	}
}