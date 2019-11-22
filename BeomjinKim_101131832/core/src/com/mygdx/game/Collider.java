package com.mygdx.game;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class Collider extends ActorBeta
{
	public Collider(MapProperties props)
	{
		setSize((float)props.get("width"), (float)props.get("height"));
		setPosition((float)props.get("x"), (float)props.get("y"));
		setBoundaryRectangle();
	}
}
