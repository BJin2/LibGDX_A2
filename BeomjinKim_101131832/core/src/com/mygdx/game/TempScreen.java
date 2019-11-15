package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class TempScreen extends ScreenBeta
{
    TilemapActor tma;
    TempActor temp;
    boolean once = false;
    @Override
    public void initialize()
    {
		tma = new TilemapActor("Tilemap/Maps/Temp_level.tmx", mainStage);

        temp = new TempActor();
        temp.loadTexture("badlogic.jpg");
        temp.setSize(200, 200);
        temp.setToCenter();
        mainStage.addActor(temp);
        startFollowing(temp);
    }

    @Override
    public void update(float dt)
    {
        if(Gdx.input.isTouched())
        {
            if(!once)
            {
                once = true;
                tma.SetTilesToShow(12, 8);
            }
            else
            {
                temp.setPosition(temp.getX()+(Gdx.graphics.getWidth() * 0.0005f), temp.getY());
            }
        }
    }
}
