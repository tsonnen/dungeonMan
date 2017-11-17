package edu.cs328.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g2d.Batch;
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
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class DungeonMan extends ApplicationAdapter {
	private TiledMap map;
 	private OrthogonalTiledMapRenderer tiledMapRenderer;
 	private OrthographicCamera camera;
 	private Dungeon dungeon;
 	private Player player;
 	private Enemy enemy;
 	private Vector2 position;
 	private MiniMap miniMap;
 	private ScreenViewport screenViewport;
	
	@Override
	public void create () {
		dungeon = new Dungeon(500, 100, 16, 16);
		map = dungeon.map;
		miniMap = new MiniMap(map);
		tiledMapRenderer = new OrthogonalTiledMapRenderer(map, 1 / 16f);
		player = new Player();
		enemy = new Enemy();
		player.position = new Vector2(dungeon.spawn.x, dungeon.spawn.y);
		enemy.position  = new Vector2(player.position.x + 10, player.position.y + 10);
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 500, 100);
        camera.update();
        //screenViewport = new ScreenViewport(camera);
	}

	@Override
	public void render () {
		float delta = Gdx.graphics.getDeltaTime();
		TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(0);
		//screenViewport.apply();
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.position.x = player.position.x;
      	camera.position.y = player.position.y;
      	camera.update();

      	//dungeon.doSimulation(100, 100);
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        Batch batch = tiledMapRenderer.getBatch();
        batch.begin();
        player.update(layer);
        enemy.update(player.position.x, player.position.y, layer);
        player.render(batch, delta, layer);
        enemy.render(batch, delta, layer);
        batch.end();
        
        //miniMap.update(position.x, position.y);
        //miniMap.render();

	}
	
	@Override
	public void dispose () {
		
	}
}
