package com.mygdx.game;

public class MyGame extends GameBeta
{
    @Override
    public void create()
    {
        super.create();
        setActiveScreen(new GameScreen());
    }
}
