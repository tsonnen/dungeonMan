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
 	private float stateTime = 0;
 	final DungeonMan game;
 	private int roomWidth = 16;
	private int roomHeight = 12;
	private GameScreen gameScreen;

 	public DungeonScreen(final DungeonMan game, Dungeon dungeon, GameScreen gameScreen) {
		this.game = game;
		this.gameScreen = gameScreen;
		this.dungeon = dungeon;
		map = dungeon.map;
		miniMap = new MiniMap(map);
		tiledMapRenderer = new OrthogonalTiledMapRenderer(map, 1 / 16f);
		player = new Player();
		player.position = new Vector2(dungeon.spawn.x, dungeon.spawn.y);
		camera = new OrthographicCamera();
		camera.setToOrtho(false, roomWidth, roomHeight);
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
        int x = (int)player.position.x / (roomWidth-1);
        int y = (int)player.position.y / (roomHeight -1);
        //camera.position.x =  x * 9 + 5;
      	//camera.position.y = y * 9 + 5;
      	Vector3 destPos = new Vector3(x * (roomWidth -1) + (roomWidth/2), y * (roomHeight -1) + (roomHeight/2), 0);

      	camera.position.lerp(destPos, 4 * delta);
      	camera.update();

      	//dungeon.doSimulation(100, 100);
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        if((camera.position.x != destPos.x || camera.position.y != destPos.y) && stateTime < 1f){
        	stateTime += delta;
        	delta = 0;
        }
        else{
        	stateTime = 0;
        }

         if(stateTime >= 1f){
            camera.position.x  = destPos.x;
            camera.position.y = destPos.y;
            TiledMapTileLayer enemyLayer = (TiledMapTileLayer)map.getLayers().get("enemies");
            enemies = new Array<Enemy>();
            for(int i = (int)destPos.x - roomWidth/2; i < (int)destPos.x + roomWidth/2; i++){
                for(int j = (int)destPos.y - roomHeight/2; j < (int)destPos.y + roomHeight/2; j++){
                    Cell cell = enemyLayer.getCell(i, j);
                    if(cell != null){
                        enemies.add(new Enemy(i,j));
                    }
                }
            }
        }

        Batch batch = tiledMapRenderer.getBatch();
        batch.begin();
        player.update(delta);
        player.render(batch, delta, map);
        Rectangle hitBox = player.getAttackBox();
        

        if(player.state == Unit.State.ATTACK){
        	//dungeon.hitTreasure((int)hitBox.x, (int)player.position.y, hitBox);
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