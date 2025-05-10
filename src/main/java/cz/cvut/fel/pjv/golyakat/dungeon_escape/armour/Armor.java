package cz.cvut.fel.pjv.golyakat.dungeon_escape.armour;

public interface Armor {
    float getDefensAmount();
    SlotType getSlot();          // NEW
    enum SlotType { HEAD, CHEST, LEGS, FEET }
}