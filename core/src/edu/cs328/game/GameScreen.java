package edu.cs328.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.Screen;

public class GameScreen implements Screen{

 	final DungeonMan game;
 	private TiledMap map;
 	private OrthogonalTiledMapRenderer tiledMapRenderer;
 	private OrthographicCamera camera;
 	private Player player;


 	public GameScreen(final DungeonMan game) {
 		this.game = game;
 		map = new TiledMap();
 		MapLayers layers = map.getLayers();
 		TiledMapTileLayer layer = new TiledMapTileLayer(10,10,16,16);
 		Texture groundTexture = new Texture(Gdx.files.internal("ground.png"));
 		Cell cell = new Cell();
		cell.setTile(new StaticTiledMapTile(new TextureRegion(groundTexture)));

		for(int x = 0; x < 20; x++){
			for(int y = 0; y < 10; y++){
				layer.setCell(x,y,cell);
			}
		}

		layers.add(layer);

 		tiledMapRenderer = new OrthogonalTiledMapRenderer(map, 1 / 16f);

 		player = new Player();
 		player.position = new Vector2(5, 5);

 		camera = new OrthographicCamera();
		camera.setToOrtho(false, 10, 10);
        camera.update();
	}

	@Override
	public void render (float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        if(player.position.x < 0){
        	camera.position.x = -5;
        	camera.update();
        }

		Batch batch = tiledMapRenderer.getBatch();
        batch.begin();
        player.update(delta);
        player.render(batch, delta);
        batch.end();
		//game.setScreen(new DungeonScreen(game, new Dungeon(500,100,16,16)));
	}
	
	@Override
	public void dispose () {
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show(){
	}

	public void resize(int width, int height) {
		// change the stage's viewport when teh screen size is changed
	}
 
	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}
 
	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
}