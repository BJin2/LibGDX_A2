package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class TempScreen extends ScreenBeta
{
    @Override
    public void initialize()
    {
		TilemapActor tma = new TilemapActor("Tilemap/Maps/Temp_level.tmx", mainStage);

        mainStage.getCamera().position.x = halfWidht;
        mainStage.getCamera().position.y = halfHeight;

        TempActor temp = new TempActor();
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
            mainStage.getCamera().position.x += Gdx.graphics.getWidth() * 0.05f;
        }
    }
}
