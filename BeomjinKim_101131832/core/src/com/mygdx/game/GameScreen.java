package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.utils.Align;

import java.util.LinkedList;


public class GameScreen extends ScreenBeta
{
	float timer;

	TilemapActor tma;
	LinkedList<Collider> colliders;
	LinkedList<Pickup> pickups;
	LinkedList<Door> doors;
	LinkedList<Character> enemies;

	Character player;

	Touchpad stick;
	Label time_label;
	Label health_label;
	Label score_label;
	ImageTextButton attack;
	ImageTextButton jump;

	@Override
	public void initialize()
	{
		timer = 180;

		Skin skin = new Skin(Gdx.files.internal("clean-crispy/skin/clean-crispy-ui.json"));

		tma = new TilemapActor("Tilemap/Maps/level_"+Integer.toString(level)+".tmx", mainStage);
		mainStage.getViewport().setCamera(tma.tiledCamera);

		colliders = new LinkedList<Collider>();
		pickups = new LinkedList<Pickup>();
		doors = new LinkedList<Door>();
		enemies = new LinkedList<Character>();
		float spawn_x = -32;
		float spawn_y = 0;
		for(MapObject obj : tma.getRectangleList("collider"))
		{
			Collider col = new Collider(obj.getProperties());
			colliders.add(col);
			mainStage.addActor(col);
		}
		for(MapObject obj : tma.getRectangleList("pickup"))
		{
			Pickup key = new Pickup(obj.getProperties());
			pickups.add(key);
			mainStage.addActor(key);
		}
		for(MapObject obj : tma.getRectangleList("locked"))
		{
			Door door = new Door(obj.getProperties());
			doors.add(door);
			mainStage.addActor(door);
		}
		for(MapObject obj : tma.getRectangleList("enemy"))
		{
			Character enemy = new Character(8, 5);
			SetEnemy(enemy, obj.getProperties());
			enemies.add(enemy);
			mainStage.addActor(enemy);
		}
		for(MapObject obj : tma.getRectangleList("spawn"))
		{
			MapProperties props = obj.getProperties();
			spawn_x = (float)props.get("x");
			spawn_y = (float)props.get("y");
		}

		player = new Character(8, 5);
		SetPlayer();
		player.setPosition(spawn_x-32, spawn_y);

//UI Label
		time_label = new Label("3:00", skin);
		time_label.setAlignment(Align.center);

		health_label = new Label("", skin);
		health_label.setAlignment(Align.center);

		score_label = new Label("0", skin);
		score_label.setAlignment(Align.center);

		Table table = new Table(skin);
		table.setSize(TilemapActor.windowWidth, TilemapActor.windowHeight*0.1f);
		table.setPosition(0, TilemapActor.windowHeight-table.getHeight());

		table.add(health_label);
		table.add(time_label).pad(TilemapActor.windowWidth*0.07f);
		table.add(score_label);



//UI controller
		stick = new Touchpad(0.1f, skin);
		stick.setSize(TilemapActor.windowWidth*0.15f, TilemapActor.windowWidth*0.15f);
		stick.setPosition(0, 0);
		stick.setColor(1, 1, 1, 0.7f);

		attack = new ImageTextButton("A", skin);
		attack.setSize(TilemapActor.windowWidth * 0.1f, TilemapActor.windowWidth * 0.1f);
		attack.setPosition(TilemapActor.windowWidth-attack.getWidth()*2, 0);
		attack.setColor(1, 1, 1, 0.7f);

		jump = new ImageTextButton("B", skin);
		jump.setSize(TilemapActor.windowWidth * 0.1f, TilemapActor.windowWidth * 0.1f);
		jump.setPosition(TilemapActor.windowWidth-jump.getWidth(), 0);
		jump.setColor(1, 1, 1, 0.7f);

		AddEventListener();

		mainStage.addActor(table);
		mainStage.addActor(player);
		mainStage.addActor(attack);
		mainStage.addActor(jump);
		mainStage.addActor(stick);
	}

	@Override
	public void update(float dt)
	{
		timer -= dt;
		UpdateLabel();
		player.setPosition(player.getX(), player.getY()-98.0f*dt);

		for(int i = 0; i < colliders.size(); i++)
		{
			if(player.overlaps(colliders.get(i)))
			{
				player.preventOverlap(colliders.get(i));
				player.StopJump();
			}
		}

		for(int i = 0; i < pickups.size(); i++)
		{
			if(player.overlaps(pickups.get(i)))
			{
				Pickup temp = pickups.get(i);
				player.PickupItem(temp.getKeyID());
				pickups.remove(i);
				temp.remove();
				score++;
			}
		}

		for(int i = 0; i < doors.size(); i++)
		{
			Door door = doors.get(i);
			if(player.overlaps(door))
			{
				boolean open = false;
				for(int j = 0; j < 3; j++)
				{
					if(door.openDoor(player.GetKey(j)))
					{
						open = true;
						door.remove();
					}
				}
				if(!open)
					player.preventOverlap(door);
			}
		}

		if(enemies.size() > 0)
			SimulateAI(dt);

		ProcessInput(dt);

		if(player.getX()+player.offset_left >= TilemapActor.windowWidth)
		{
			level++;
			level %= 5;
			MyGame.setActiveScreen(new GameScreen());
		}
		if(player.getY()+player.getHeight()-player.offset_top < 0 || player.health <= 0)
		{
			MyGame.setActiveScreen(new GameScreen());
		}
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

	public void UpdateLabel()
	{
		health_label.setText(Integer.toString((int)player.health));
		time_label.setText(FloatToTime(timer));
		score_label.setText(Integer.toString(score));
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
				//if (cur_x + player.offset_left < 0)
				//	cur_x = -player.offset_left;
				//else if (cur_x + player.getWidth() - player.offset_right > screenWidth)
				//	cur_x = screenWidth - player.getWidth() + player.offset_right;
				//
				//if (cur_y + player.offset_bottom < 0)
				//	cur_y = -player.offset_bottom;
				//else if (cur_y + player.offset_bottom > screenHeight * 0.25f)
				//	cur_y = screenHeight * 0.25f - player.offset_bottom;

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

	//*/
	public void SetEnemy(Character enemy, MapProperties props)
	{
		String path = "Midori Sugiura/Midori Sugiura_0";
		String[] idle = new String[25];
		String[] left = new String[8];
		String[] atk1 = new String[7];
		String[] hit = {path+"025.png", path+"026.png", path+"027.png"};
		String[] death = new String[11];

		for(int i = 141; i < 148; i++)
		{
			atk1[i-141] = path + i + ".png";
		}
		for(int i = 0; i < 25; i++)
		{
			idle[i] = path + "0" + String.format("%02d",i) + ".png";
		}
		for(int i = 51; i < 59; i++)
		{
			left[i-51] = path + "0" + i + ".png";
		}
		for(int i = 31; i < 42; i++)
		{
			death[i-31] = path + "0" + i + ".png";
		}

		enemy.LoadAnimation(Character.ANIM_STATE.idle, idle, 0.1f, true);
		enemy.LoadAnimation(Character.ANIM_STATE.left, left, 0.1f, true);
		enemy.LoadAnimation(Character.ANIM_STATE.atk1, atk1, 0.05f, false);
		enemy.LoadAnimation(Character.ANIM_STATE.hit, hit, 0.1f, false);
		enemy.LoadAnimation(Character.ANIM_STATE.death, death, 0.1f, false);

		enemy.LoadSound("cv_23_085", "cv_23_041", "walk_1", "cv_23_090");

		enemy.setOffsetRect(32*5, 22*5);
		float spawn_x = (float)props.get("x");
		float spawn_y = (float)props.get("y");
		enemy.setPosition(spawn_x-32, spawn_y-48);
	}
	public void SimulateAI(float dt)
	{
		float WIDTH = TilemapActor.windowWidth;

		for(int i = 0; i < enemies.size(); i++)
		{
			Character enemy = enemies.get(i);
			for(int j = 0; j < colliders.size(); j++)
			{
				if(enemy.overlaps(colliders.get(j)))
				{
					enemy.preventOverlap(colliders.get(j));
				}
			}
			if(enemy.health <= 0)
			{
				enemies.remove(i);
				return;
			}

			enemy.setPosition(enemy.getX(), enemy.getY()-98.0f*dt);

			//AI character
			if(enemy.attacked || enemy.attacking)
			{
				if(enemy.isAnimationFinished())
				{
					enemy.attacking = false;
					enemy.attacked = false;
					return;
				}
				else
					return;
			}
			else
			{
				enemy.cooltime += dt;
				Vector2 dir = new Vector2(player.getX() - enemy.getX(), player.getY() - enemy.getY());
				float distance = dir.len();

				if(distance <= WIDTH * 0.1f && enemy.cooltime >= 2.5f)
				{
					enemy.Attack(player, mainStage);
					enemy.cooltime = 0;
				}
				else if(distance <= WIDTH * 0.2f)
				{
					enemy.SetCurrentAnimation(Character.ANIM_STATE.idle, false);
				}
				else
				{
					dir.nor();
					//Calculate movement
					float cur_x = enemy.getX() + (dir.x * dt * TilemapActor.windowWidth * 0.1f);
					float cur_y = enemy.getY();

					//Set animation
					enemy.SetCurrentAnimation(Character.ANIM_STATE.left, false);

					//Move the actor
					enemy.setPosition(cur_x, cur_y);

					//Decide direction
					if (dir.x < 0)
					{
						enemy.UnFlip();
					}
					else
					{
						enemy.Flip();
					}
				}
			}


		}
	}
//*/
	public void AddEventListener()
	{
		attack.addListener(new EventListener()
		{
			@Override
			public boolean handle(Event event)
			{
				if (!player.attacked && !player.attacking)
				{
					Character[] targets = new Character[enemies.size()];
					enemies.toArray(targets);
					player.Attack(targets, mainStage);
				}
				return false;
			}
		});
		jump.addListener(new EventListener()
		{
			@Override
			public boolean handle(Event event)
			{
				if (!player.attacked && !player.attacking)
				{
					player.Jump( );
				}
				return false;
			}
		});
	}
	public String FloatToTime(float t)
	{
		int min = (int)(t/60);
		int sec = (int)(t%60);

		String time = Integer.toString(min)+":"+String.format("%02d",sec);
		return time;
	}
}
