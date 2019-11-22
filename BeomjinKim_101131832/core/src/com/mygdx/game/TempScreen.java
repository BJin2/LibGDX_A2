package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;

import java.util.LinkedList;


public class TempScreen extends ScreenBeta
{
	TilemapActor tma;
	TempActor temp;
	LinkedList<Collider> colliders;
	boolean once = false;
	@Override
	public void initialize()
	{
		tma = new TilemapActor("Tilemap/Maps/Temp_level.tmx", mainStage);
		mainStage.getViewport().setCamera(tma.tiledCamera);
		temp = new TempActor();
		temp.loadTexture("badlogic.jpg");
		temp.setSize(16, 16);
		temp.setPosition(TilemapActor.windowWidth*0.5f - 8, TilemapActor.windowHeight*0.5f-8);
		temp.setBoundaryRectangle();

		colliders = new LinkedList<Collider>();
		for(MapObject obj : tma.getRectangleList("collider"))
		{
			Collider col = new Collider(obj.getProperties());
			colliders.add(col);
			mainStage.addActor(col);
		}

		mainStage.addActor(temp);
	}

	@Override
	public void update(float dt)
	{
		temp.setPosition(temp.getX(), temp.getY()-10.0f*dt);
		for(int i = 0; i < colliders.size(); i++)
		{
			if(temp.overlaps(colliders.get(i)))
			{
				temp.preventOverlap(colliders.get(i));
			}
		}

		if(Gdx.input.isTouched())
		{
			if(!once)
			{
				once = true;
			}
			else
			{
				temp.setPosition(temp.getX()+(Gdx.graphics.getWidth() * 0.0005f), temp.getY());
			}
		}
	}
}
