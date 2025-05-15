module cz.cvut.fel.pjv.golyakat.dungeon_escape {
    requires java.desktop;
    requires java.logging;

    requires com.fasterxml.jackson.dataformat.xml;
    requires com.fasterxml.jackson.databind;
    exports cz.cvut.fel.pjv.golyakat.dungeon_escape;
    exports cz.cvut.fel.pjv.golyakat.dungeon_escape.object;
    exports cz.cvut.fel.pjv.golyakat.dungeon_escape.sprite;
    exports cz.cvut.fel.pjv.golyakat.dungeon_escape.saveData;
    exports cz.cvut.fel.pjv.golyakat.dungeon_escape.ui;
    exports cz.cvut.fel.pjv.golyakat.dungeon_escape.bars;
    exports cz.cvut.fel.pjv.golyakat.dungeon_escape.tile;
    exports cz.cvut.fel.pjv.golyakat.dungeon_escape.monster;

    opens cz.cvut.fel.pjv.golyakat.dungeon_escape to
            com.fasterxml.jackson.databind,
            com.fasterxml.jackson.dataformat.xml;

    opens cz.cvut.fel.pjv.golyakat.dungeon_escape.saveData to
            com.fasterxml.jackson.databind,
            com.fasterxml.jackson.dataformat.xml;


}