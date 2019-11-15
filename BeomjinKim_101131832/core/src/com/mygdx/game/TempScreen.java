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
    }

    @Override
    public void update(float dt)
    {
        if(Gdx.input.justTouched())
        {
            if(!once)
            {
                once = true;
                tma.SetTilesToShow(10, 5);
            }
            else
            {
                temp.setPosition(temp.getX()+(Gdx.graphics.getWidth() * 0.0005f), temp.getY());
                //mainStage.getCamera().position.x += Gdx.graphics.getWidth() * 0.0005f;
            }
        }
        mainStage.getCamera().position.x = temp.getX() + (temp.getWidth()*0.5f);
        mainStage.getCamera().position.y = temp.getY() + (temp.getHeight() * 0.5f);
    }
}
