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

public class DungeonScreen implements Screen{

	private TiledMap map;
 	private OrthogonalTiledMapRenderer tiledMapRenderer;
 	private OrthographicCamera camera;
 	private Dungeon dungeon;
 	private Player player;
 	private Array<Enemy> enemies = new Array<Enemy>();
 	private Vector2 position;
 	private MiniMap miniMap;
 	final DungeonMan game;

 	public DungeonScreen(final DungeonMan game, Dungeon dungeon) {
		this.game = game;
		this.dungeon = dungeon;
		map = dungeon.map;
		miniMap = new MiniMap(map);
		tiledMapRenderer = new OrthogonalTiledMapRenderer(map, 1 / 16f);
		player = new Player();
		player.position = new Vector2(dungeon.spawn.x, dungeon.spawn.y);
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 10, 10);
        camera.update();
        TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get("enemies");

        for(int x = 0; x < layer.getWidth(); x++){
        	for(int y = 0; y < layer.getHeight(); y++){
        		Cell cell = layer.getCell(x, y);
        		if(cell != null){
        			enemies.add(new Enemy(x,y));
        		}
        	}
        }
	}
	

	@Override
	public void render (float delta) {
		//float delta = Gdx.graphics.getDeltaTime();
		TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get("walls");
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
        player.update(delta);
        player.render(batch, delta, map);
        Rectangle hitBox = player.getAttackBox();
        if(player.state == Unit.State.ATTACK){
        	dungeon.hitTreasure((int)hitBox.x, (int)player.position.y, hitBox);
    	}
        for(Enemy enemy: enemies){
        	enemy.update(player.position.x, player.position.y, layer);
        	enemy.render(batch, delta, map);
        	if(player.state == Unit.State.ATTACK){
        		if(enemy.bounds.overlaps(hitBox))
        			enemies.removeValue(enemy, true);
        	}
    	}
        batch.end();
        
        //miniMap.update(position.x, position.y);
        //miniMap.render();

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