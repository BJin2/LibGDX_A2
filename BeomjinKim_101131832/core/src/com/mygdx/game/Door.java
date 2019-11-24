package com.mygdx.game;

import com.badlogic.gdx.maps.MapProperties;

public class Door extends ActorBeta
{
	private int doorID;

	public Door(MapProperties props)
	{
		doorID = (int)props.get("doorID");
		loadTexture("Tilemap/Images/door_"+Integer.toString(doorID) + ".png");
		setSize((float)props.get("width"), (float)props.get("height"));
		setPosition((float)props.get("x"), (float)props.get("y"));
		setBoundaryRectangle();
	}

	public int getDoorID()
	{
		return doorID;
	}
	public boolean openDoor(int keyID)
	{
		if(doorID == keyID)
		{
			return true;
		}

		return false;
	}
}
