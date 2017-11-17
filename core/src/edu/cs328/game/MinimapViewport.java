package edu.cs328.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * 
 * A MinimapViewport usually has a size smaller than the screen and can be positioned
 * anywhere on screen. It also uses a virtual size for the camera
 * @author Linggify
 *
 */
public class MinimapViewport extends Viewport{

   private float m_xfac;
   private float m_yfac;
   private float m_wfac;
   private float m_hfac;
   
   /**
    * Creates a viewport at the given position on screen with the given dimensions
    * The parameters are between 0 and 1, where 0 is 0 and 1 is full screen size.
    * (e.g. new MinimapViewport(0, 0, 0.5f, 0.5f, cam) would create a viewport
    * at the top left of the screen, filling the top left quater of the screen,
    * no matter how big the screen is
    * @param sx
    * @param sy
    * @param w
    * @param h
    * @param camera
    */
   public MinimapViewport(float sx, float sy, float w, float h, Camera camera) {
      setPosition(sx, sy);
      setSize(w, h);
      setCamera(camera);
   }
   
   /**
    * Sets the position of the viewport on screen
    * The parameters are between 0 and 1, where 0 is 0 and 1 is full screen size.
    * @param x
    * @param y
    */
   public void setPosition(float x, float y) {
      m_xfac = x;
      m_yfac = y;
   }
   
   /**
    * Sets the size of the viewport on screen
    * The parameters are between 0 and 1, where 0 is 0 and 1 is full screen size.
    * @param width
    * @param height
    */
   public void setSize(float width, float height) {
      m_wfac = width;
      m_hfac = height;
   }
   
   /**
    * Sets the virtual width and hight if this viewport.
    * @param width
    * @param height
    */
   public void setVirtualSize(float width, float height) {
      setWorldSize(width, height);
   }
   
   public void update(int width, int height, boolean centercam) {
      setScreenSize((int) (width * m_wfac), (int) (height * m_hfac));
      setScreenPosition((int) (width * m_xfac), (int) (height * m_yfac));
      
      super.update(width, height, centercam);
   }
}
