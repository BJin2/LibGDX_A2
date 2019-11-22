package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;

import java.util.LinkedList;


public class TempScreen extends ScreenBeta
{
	TilemapActor tma;
	LinkedList<Collider> colliders;

	Character player;

	Touchpad stick;
	ImageTextButton attack;
	ImageTextButton attack2;

	boolean once = false;

	@Override
	public void initialize()
	{
		Skin skin = new Skin(Gdx.files.internal("clean-crispy/skin/clean-crispy-ui.json"));

		tma = new TilemapActor("Tilemap/Maps/Temp_level.tmx", mainStage);
		mainStage.getViewport().setCamera(tma.tiledCamera);

		colliders = new LinkedList<Collider>();
		for(MapObject obj : tma.getRectangleList("collider"))
		{
			Collider col = new Collider(obj.getProperties());
			colliders.add(col);
			mainStage.addActor(col);
		}
		player = new Character(8, 5);
		SetPlayer();

		player.setPosition(-64, TilemapActor.windowHeight*0.5f-8);


		stick = new Touchpad(0.1f, skin);
		stick.setSize(TilemapActor.windowWidth*0.15f, TilemapActor.windowWidth*0.15f);
		stick.setPosition(0, 0);
		stick.setColor(1, 1, 1, 0.7f);


		mainStage.addActor(player);
		mainStage.addActor(stick);
	}

	@Override
	public void update(float dt)
	{
		player.setPosition(player.getX(), player.getY()-19.0f*dt);
		for(int i = 0; i < colliders.size(); i++)
		{
			if(player.overlaps(colliders.get(i)))
			{
				player.preventOverlap(colliders.get(i));
			}
		}

		ProcessInput(dt);
	}

	public void SetPlayer()
	{
		String path = "Shizuru Fujina/Shizuru Fujina_0";
		String[] idle = {path+"523.png", path+"524.png", path+"525.png", path+"526.png"};
		String[] left = {path+"001.png", path+"002.png", path+"003.png", path+"004.png", path+"005.png", path+"006.png", path+"007.png", path+"008.png"};
		String[] atk1 = new String[13];
		for(int i = 139; i < 152; i++)
		{
			atk1[i-139] = path+Integer.toString(i) + ".png";
		}
		String[] death = new String[11];
		for(int i = 326; i < 337; i++)
		{
			death[i-326] = path + Integer.toString(i) + ".png";
		}
		String[] hit = {path+"502.png", path+"501.png", path+"502.png", path+"503.png"};

		player.LoadAnimation(Character.ANIM_STATE.idle, idle, 0.5f, true);
		player.LoadAnimation(Character.ANIM_STATE.left, left, 0.1f, true);
		player.LoadAnimation(Character.ANIM_STATE.atk1, atk1, 0.05f, false);
		//player.LoadAnimation(Character.ANIM_STATE.atk3, atk3, 0.05f, false);
		player.LoadAnimation(Character.ANIM_STATE.hit, hit, 0.1f, false);
		player.LoadAnimation(Character.ANIM_STATE.death, death, 0.1f, false);

		player.LoadSound("cv_20_062",  "cv_20_035", "walk_0", "cv_20_029");

		player.Flip();
		player.setOffsetRect(32*5, 22*5);
	}

	public void ProcessInput(float dt)
	{
		if(player.health <= 0)
		{
			//Lose();
			//gameover = true;
			return;
		}

		if(player.attacking || player.attacked)
		{
			if(player.isAnimationFinished())
			{
				player.SetCurrentAnimation(Character.ANIM_STATE.idle, false);
				player.attacking = false;
				player.attacked = false;
			}
			else
				return;
		}
		else
		{
			//Movement
			float x = stick.getKnobPercentX();
			float y = stick.getKnobPercentY();

			//When not moving
			if (Math.abs(x) <= 0.2f && Math.abs(y) <= 0.2f)
			{
				player.SetCurrentAnimation(Character.ANIM_STATE.idle, false);
			}
			else// When moving
			{
				//Calculate movement
				float cur_x = player.getX() + (x * dt * TilemapActor.windowWidth * 0.1f);
				float cur_y = player.getY() + (y * dt * TilemapActor.windowHeight * 0.1f);

				//Clamp position
				if (cur_x + player.offset_left < 0)
					cur_x = -player.offset_left;
				else if (cur_x + player.getWidth() - player.offset_right > screenWidth)
					cur_x = screenWidth - player.getWidth() + player.offset_right;

				if (cur_y + player.offset_bottom < 0)
					cur_y = -player.offset_bottom;
				else if (cur_y + player.offset_bottom > screenHeight * 0.25f)
					cur_y = screenHeight * 0.25f - player.offset_bottom;

				//Set animation
				player.SetCurrentAnimation(Character.ANIM_STATE.left, false);

				//Move the actor
				player.setPosition(cur_x, cur_y);

				//Decide direction
				if (x < 0)
				{
					player.UnFlip();
				}
				else
				{
					player.Flip();
				}
			}
		}
	}
}
