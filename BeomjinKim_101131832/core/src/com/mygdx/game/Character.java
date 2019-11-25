package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.awt.BufferCapabilities;

public class Character extends ActorBeta
{
	public class ANIM_STATE
	{
		static final int idle = 0,
				left = 1,
				atk1 = 2,
				jump = 3,
				hit = 4,
				death = 5;
	}
	Animation<TextureRegion>[] animations;

	private float width;
	private float height;

	public float offset_left;
	public float offset_right;
	public float offset_top;
	public float offset_bottom;

	public boolean attacking;
	public boolean attacked;
	public boolean jumping;
	public float health;
	public float cooltime;

	private int[] keyIDs;
	private  int numKeys;

	float jumpForce;

	Sound hit;
	Sound attack1;
	Sound swing1;
	Sound walk;
	Sound death;

	Character(float _width, float _height)
	{
		animations = new Animation[6];
		attacking = false;
		attacked = false;
		jumping = true;

		health = 100;
		cooltime = 0;
		jumpForce = 0;
		numKeys = 0;
		keyIDs = new int[3];
		for(int i = 0; i < 3; i++)
		{
			keyIDs[i] = -1;
		}
	}
	public void setOffsetRect(float _width, float _height)
	{
		width = _width;
		height = _height;
		offset_left = 0.46f * width;
		offset_right = 0.41f * width;
		offset_top = 0.3f * height;
		offset_bottom = 0.25f * height;
		setSize(width, height);
		setOffsetBoundary(offset_left, offset_right, offset_top, offset_bottom);
	}

	public int GetKey(int i)
	{
		return keyIDs[i];
	}
	public void Attack(Character[] target, Stage s)
	{
		SetCurrentAnimation(ANIM_STATE.atk1, true);
		float atk_x;
		float atk_y = getY() + (offset_top*1.5f);
		float atk_width = width*0.2f;
		float atk_height = atk_width;

		if(flip)
		{
			atk_x = getX()+getWidth()-offset_right;
		}
		else
		{
			atk_x = (getX()+offset_left)-atk_width;
		}

		AttackRange ar = new AttackRange(atk_x, atk_y, atk_width, atk_height, 0.4f, 10, target);
		s.addActor(ar);
		attacking = true;
		swing1.play();
		attack1.play();
	}
	public void Attack(Character target, Stage s)
	{
		SetCurrentAnimation(ANIM_STATE.atk1, true);
		float atk_x;
		float atk_y = getY() + (offset_top*1.5f);
		float atk_width = width*0.2f;
		float atk_height = atk_width;

		if(flip)
		{
			atk_x = getX()+getWidth()-offset_right;
		}
		else
		{
			atk_x = (getX()+offset_left)-atk_width;
		}

		AttackRange ar = new AttackRange(atk_x, atk_y, atk_width, atk_height, 0.4f, 10, target);
		s.addActor(ar);
		attacking = true;
		swing1.play();
		attack1.play();
	}
	public void Jump()
	{
		if(jumping)
			return;
		jumping = true;
		jumpForce = 250.0f;
	}
	public void StopJump()
	{
		jumping = false;
		jumpForce = 0;
	}
	public void PickupItem(int keyID)
	{
		if(numKeys == keyIDs.length)
			return;
		keyIDs[numKeys] = keyID;
		numKeys++;
	}
	public void TakeDamage(float damage)
	{
		health -= damage;
		if(health <= 0)
		{
			health = 0;
			SetCurrentAnimation(ANIM_STATE.death, true);
			death.play();
			return;
		}
		SetCurrentAnimation(ANIM_STATE.hit, true);
		attacked = true;
		hit.play();
	}
	public void LoadAnimation(int index, String[] sprite, float frameDuration, boolean loop)
	{
		animations[index] = loadAnimationFromFiles(sprite, frameDuration, loop);
	}
	public void LoadSound(String hitSound, String atk1Sound, String walkSound, String deathSound)
	{
		String path = "Audio/";
		String format = ".wav";
		swing1 = Gdx.audio.newSound(Gdx.files.internal(path + "attack_1" + format));
		hit = Gdx.audio.newSound(Gdx.files.internal(path + hitSound + format));
		attack1 = Gdx.audio.newSound(Gdx.files.internal(path + atk1Sound + format));
		walk = Gdx.audio.newSound(Gdx.files.internal(path + walkSound + format));
		death =Gdx.audio.newSound(Gdx.files.internal(path + deathSound + format));
	}
	public void SetCurrentAnimation(int index, boolean reset)
	{
		if(reset)
			setAnimationWithReset(animations[index]);
		else
			setAnimation(animations[index]);
		setSize(width, height);
	}
	public void Flip()
	{
		Flip(true);

		//Flip side offset
		offset_left =  0.41f * width;
		offset_right = 0.46f * width;
		setOffsetBoundary(offset_left, offset_right, offset_top, offset_bottom);
	}
	public void UnFlip()
	{
		Flip(false);
		offset_left = 0.46f * width;
		offset_right = 0.41f * width;
		setOffsetBoundary(offset_left, offset_right, offset_top, offset_bottom);
	}

	@Override
	public void act(float dt)
	{
		super.act(dt);
		if(jumping)
		{
			setPosition(getX(), getY()+(jumpForce*dt));
			jumpForce -= 250.0f * dt;
			if(jumpForce <= 0.0f)
			{
				jumpForce = 0.0f;
			}
		}
	}
}
