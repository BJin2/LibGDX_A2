package com.mygdx.game;

import com.badlogic.gdx.maps.MapProperties;

public class Pickup extends ActorBeta
{
	private int keyID;

	public Pickup(MapProperties props)
	{
		keyID = (int)props.get("keyID");
		loadTexture("Tilemap/Images/key_"+Integer.toString(keyID) + ".png");
		setSize((float)props.get("width"), (float)props.get("height"));
		setPosition((float)props.get("x"), (float)props.get("y"));
		setBoundaryRectangle();
	}

	public int getKeyID()
	{
		return keyID;
	}
}
