package com.planetdefender.game.entities;
import static com.planetdefender.game.Spot.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.planetdefender.game.events.BoundaryChecker;
import com.planetdefender.game.events.InputHandler;

public class Player implements Entity {
    private final Vector2 position;
    private Sprite sprite;
    private final float speed = 500f;
    private int kills = 0;
    Bullet bullet;

    public Player() {
        // Player sprite generation
        int ship_width = 16, ship_height = 16;
        int ship_x = 48, ship_y = 64;
        String ship_path = "SpaceShooterAssets/SpaceShooterAssetPack_Ships.png";
        textureManager.loadTexture("player", ship_path, ship_x, ship_y, ship_width, ship_height);
        TextureRegion playerTexture = textureManager.getTexture("player");
        sprite = new Sprite(playerTexture);
        float player_scale = 6.0f;
        sprite.setScale(player_scale);



        // Positioning
        float player_y_offset = 50f;
        position = new Vector2((float) Gdx.graphics.getWidth()/2 - sprite.getWidth() * sprite.getScaleX()/2, sprite.getScaleY() + player_y_offset);
        bullet = new Bullet(position);
    }

    public void Update(float deltaTime) { // pass deltaTime to any "move" methods to determine how far they should move.
        InputHandler inputHandler = new InputHandler();
        // Bullet reset (fire on SPACEBAR)
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && bullet.getPosition().y > Gdx.graphics.getHeight()) {
            bullet.reset(position);
        }
        // Player movement
        if (inputHandler.isLeftPressed()) movePlayerLeft(deltaTime); // move left on A
        if (inputHandler.isRightPressed()) movePlayerRight(deltaTime); // move right on D
        // Player boundaries
        BoundaryChecker boundaryChecker = new BoundaryChecker(0, Gdx.graphics.getWidth());
        if (boundaryChecker.isOutOfLeftBound(position, sprite.getWidth() * sprite.getScaleX())) {
            position.x = sprite.getWidth() * sprite.getScaleX();
        }
        int right_bound_offset = 4;
        if (boundaryChecker.isOutOfRightBound(position, sprite.getWidth() * sprite.getScaleX() / right_bound_offset)) {
            position.x = Gdx.graphics.getWidth() - sprite.getWidth() * sprite.getScaleX() / right_bound_offset;
        }
        // Bullet movement
        bullet.move(deltaTime);
    }

    public void Draw(SpriteBatch batch) {
        Update(Gdx.graphics.getDeltaTime());
        sprite.setPosition(position.x - sprite.getWidth() * sprite.getScaleX()/2, position.y);
        sprite.draw(batch);
        bullet.Draw(batch);
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    private void movePlayerLeft(float deltaTime) {
        position.x -= deltaTime*speed;
    }

    private void movePlayerRight(float deltaTime) {
        position.x += deltaTime*speed;
    }

    public void setKills() {
        this.kills++;
    }

    public int getKills() {
        return kills;
    }

    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public void setPosition(Vector2 position) {
        this.position.x = position.x;
        this.position.y = position.y;
    }

    @Override
    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public Bullet getBullet() {
        return bullet;
    }
}

