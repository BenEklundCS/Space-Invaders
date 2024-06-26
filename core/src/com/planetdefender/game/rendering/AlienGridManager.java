package com.planetdefender.game.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.planetdefender.game.entities.Alien;
import com.planetdefender.game.entities.Player;
import com.planetdefender.game.utils.BoundaryChecker;

import java.util.Random;

import static com.planetdefender.game.Spot.*;

public class AlienGridManager {
    private Alien[] aliens;
    private final int spacing = 100;
    private int minX_aliens = 10000;
    private int maxX_aliens = 0;
    private int direction_aliens = 1;
    private float speed_aliens = 8.0f;
    private float base_speed_aliens = 8.0f;
    private Vector2 offset_aliens;
    private Vector2 dimensions;
    private TextureRegion alienTexture;
    private final Player player;
    public boolean game_over = false;
    private final Random random = new Random();

    public AlienGridManager(Player player) {
        this.alienTexture = textureManager.getTexture("blue_alien");
        this.player = player;
        this.dimensions = new Vector2(6, 4);
        createAliens();
    }

    public void Update() {
        checkBulletCollisions();
        setAlienDimensions();
        checkEdgeConditions();
        updateAlienOffsets();
    }

    private void checkBulletCollisions() {
        for (Alien alien : aliens) {
            if (player.getBullet().getCollide(alien.getSprite()) && alien.alive) {
                alien.alive = false;
                player.setKills();
                player.getBullet().getPosition().y += 10000;
            }
        }
    }

    private void setAlienDimensions() {
        float rightmost = 0;
        float leftmost = Gdx.graphics.getWidth();

        for (int i = 0; i < aliens.length; i++) {
            if (aliens[i].alive) {
                if (aliens[i].getPosition().x > rightmost) {
                    rightmost = aliens[i].getPosition().x;
                    maxX_aliens = i;
                }
                if (aliens[i].getPosition().x < leftmost) {
                    leftmost = aliens[i].getPosition().x;
                    minX_aliens = i;
                }
            }
        }
    }

    private void checkEdgeConditions() {
        BoundaryChecker boundaryChecker = new BoundaryChecker(0, Gdx.graphics.getWidth());
        int alien_offset = 40;
        if (boundaryChecker.isOutOfLeftBound(aliens[minX_aliens].getPosition(),
                aliens[minX_aliens].getSprite().getWidth() + alien_offset)) {
            toggleDirection();
            dropdown();
            speedup();
        }

        if (boundaryChecker.isOutOfRightBound(aliens[maxX_aliens].getPosition(),
                aliens[maxX_aliens].getSprite().getWidth() + alien_offset)) {
            toggleDirection();
            dropdown();
            speedup();
        }
    }

    private void speedup() {
        speed_aliens *= 1.05f;
    }
    public void resetSpeed() {
        base_speed_aliens *= 1.15f;
        speed_aliens = base_speed_aliens;
    }

    private void dropdown() {
        for (Alien alien : aliens) {
            alien.getPosition().y -= spacing;
        }
    }

    private void toggleDirection() {
        direction_aliens *= -1;
    }

    private void updateAlienOffsets() {
        offset_aliens.x = direction_aliens * speed_aliens;
    }

    public void createAliens() {
        int width_aliens = (int) dimensions.x;
        int height_aliens = (int) dimensions.y;
        aliens = new Alien[width_aliens * height_aliens];
        offset_aliens = new Vector2(speed_aliens, 0);
        int i = 0;
        for (int y = 0; y < height_aliens; y++) {
            for (int x = 0; x < width_aliens; x++) {
                Vector2 position = new Vector2(x * spacing, y * spacing);
                position.x += (float) Gdx.graphics.getWidth() /2;
                position.y += Gdx.graphics.getHeight();
                position.x -= ((float) width_aliens /2) * spacing;
                position.y -= height_aliens * spacing;

                // Add a safe zone for the player
                if (position.x < player.getSprite().getWidth() || position.x > Gdx.graphics.getWidth() - player.getSprite().getWidth()) {
                    continue;
                }

                aliens[i] = new Alien(position, alienTexture);
                i++;
            }
        }
    }

    void checkPlayerDeath(Alien alien) {
        if (alien.getSprite().getBoundingRectangle().overlaps(player.getSprite().getBoundingRectangle())) {
            game_over = true;
        }
        if (alien.getPosition().y < 0) {
            game_over = true;
        }
    }

    public Alien[] getAliens() {
        return aliens;
    }

    public Vector2 getOffsetAliens() {
        return offset_aliens;
    }

    public void setAlienTexture(TextureRegion alienTexture) {
        this.alienTexture = alienTexture;
    }

    public TextureRegion getAlienTexture() {
        return alienTexture;
    }

    public Vector2 getDimensions() {
        return dimensions;
    }

    public void setDimensions(Vector2 dimensions) {
        this.dimensions = dimensions;
    }
    public void dispose() {
        for (Alien alien : aliens) {
            alien.dispose();
        }
    }
}
