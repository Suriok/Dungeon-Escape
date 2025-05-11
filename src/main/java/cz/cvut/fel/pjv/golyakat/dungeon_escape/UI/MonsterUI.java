package cz.cvut.fel.pjv.golyakat.dungeon_escape.UI;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite.Entity;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.Boss.Boss_Eye;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.Boss.Boss_Goblin;

import java.awt.*;

/**
 * Třída {@code MonsterUI} zajišťuje vykreslení zdravotního panelu nad monstry
 * a jejich efekt při zániku (průhlednutí).
 * <p>
 * Podporuje jak běžná monstra, tak i speciální panely pro bossy.
 * </p>
 */
public class MonsterUI {

    /** Odkaz na hlavní herní panel, který obsahuje informace o hráči a mapě. */
    private gamePanel gp;

    /** Šířka zdravotního panelu pro běžné monstrum. */
    private final int regularBarWidth = 30;

    /** Výška zdravotního panelu pro běžné monstrum. */
    private final int regularBarHeight = 5;

    /** Svislý posun zdravotního panelu vůči monstru. */
    private final int offsetY = -10;

    /** Šířka zdravotního panelu pro bosse. */
    private final int bossBarWidth = 300;

    /** Výška zdravotního panelu pro bosse. */
    private final int bossBarHeight = 10;

    /** Vzdálenost panelu bosse od horního okraje obrazovky. */
    private final int bossBarY = 20;

    /**
     * Konstruktor vytvoří instanci {@code MonsterUI} pro daný {@link gamePanel}.
     *
     * @param gp hlavní herní panel
     */
    public MonsterUI(gamePanel gp) {
        this.gp = gp;
    }

    /**
     * Vykreslí zdravotní panel daného monstra v závislosti na jeho typu a životě.
     *
     * @param g2      grafický kontext pro vykreslení
     * @param monster entita (monstrum), která má být vykreslena
     */
    public void draw(Graphics2D g2, Entity monster) {
        // Pokud je monstrum mrtvé a zcela zmizelo, neprovádíme nic
        if (monster.isDead && monster.fadeAlpha <= 0) return;

        // Přepočet pozice monstra na obrazovku
        int screenX = monster.worldX - gp.player.worldX + gp.player.screenX;
        int screenY = monster.worldY - gp.player.worldY + gp.player.screenY;

        // Určení, zda je monstrum na viditelné části obrazovky
        boolean isVisible = screenX >= -gp.tileSize && screenX <= gp.screenWidth &&
                screenY >= -gp.tileSize && screenY <= gp.screenHeight;

        // Zjištění, zda jde o bosse
        boolean isBossGoblin = monster instanceof Boss_Goblin;
        boolean isBossEye = monster instanceof Boss_Eye;

        if (!monster.isDead && isVisible) {
            if (isBossGoblin || isBossEye) {
                // === Boss panel ===
                int bossBarX = (gp.screenWidth - bossBarWidth) / 2;

                // Pozadí
                g2.setColor(Color.GRAY);
                g2.fillRect(bossBarX, bossBarY, bossBarWidth, bossBarHeight);

                // Výpočet zdraví v procentech
                float healthPercentage = (float) monster.life / monster.maxLife;
                int redValue = (int) (255 * healthPercentage);
                int grayValue = (int) (128 * (1 - healthPercentage));

                // Výplň podle zdraví
                g2.setColor(new Color(redValue, grayValue, grayValue));
                int healthWidth = (int) (bossBarWidth * healthPercentage);
                g2.fillRect(bossBarX, bossBarY, healthWidth, bossBarHeight);

                // Rámeček
                g2.setColor(Color.WHITE);
                g2.drawRect(bossBarX, bossBarY, bossBarWidth, bossBarHeight);

                // Jméno bosse
                String bossName = isBossGoblin ? "Goblin" : "Eye";
                g2.setFont(new Font("Arial", Font.BOLD, 20));
                g2.setColor(Color.WHITE);
                int textWidth = g2.getFontMetrics().stringWidth(bossName);
                int textX = (gp.screenWidth - textWidth) / 2;
                int textY = bossBarY + bossBarHeight + 25;
                g2.drawString(bossName, textX, textY);

            } else {
                // === Běžné monstrum ===
                g2.setColor(Color.GRAY);
                g2.fillRect(screenX + (gp.tileSize - regularBarWidth) / 2, screenY + offsetY,
                        regularBarWidth, regularBarHeight);

                float healthPercentage = (float) monster.life / monster.maxLife;
                int redValue = (int) (255 * healthPercentage);
                int grayValue = (int) (128 * (1 - healthPercentage));

                g2.setColor(new Color(redValue, grayValue, grayValue));
                int healthWidth = (int) (regularBarWidth * healthPercentage);
                g2.fillRect(screenX + (gp.tileSize - regularBarWidth) / 2, screenY + offsetY,
                        healthWidth, regularBarHeight);

                g2.setColor(Color.WHITE);
                g2.drawRect(screenX + (gp.tileSize - regularBarWidth) / 2, screenY + offsetY,
                        regularBarWidth, regularBarHeight);
            }
        }

        // === Efekt mizení (fade out) po smrti ===
        if (monster.isDead && monster.fadeAlpha > 0) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, monster.fadeAlpha));
            g2.drawImage(monster.image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
    }
}
