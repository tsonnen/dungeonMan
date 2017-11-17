package edu.cs328.game;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class MiniMap {
	private OrthogonalTiledMapRenderer  renderer;
	private OrthographicCamera          camera = new OrthographicCamera();
	private MinimapViewport viewPort;
	
	public MiniMap(TiledMap map) {
	
    	renderer = new OrthogonalTiledMapRenderer(map, 1 / 32f);
	
    	camera.setToOrtho(false, 100, 100);
    	camera.translate(camera.viewportWidth / 2, camera.viewportHeight);
	
    	camera.zoom = 10;

    	viewPort = new MinimapViewport(100, 100, 960 ,80, camera);
	}
	
	public void update(){
	
	}
	
	public void update(float x, float y){
	
    	//Pixventure.instance.gameScreen.getCamera()
	
    	camera.position.x = x;
    	camera.position.y = y;
    	camera.update();

    	renderer.setView(camera);  
    	//renderer.setView(camera); 
	}
	
	public void render(){
		//viewPort.apply();
		camera.update();
    	renderer.render();
	}

}