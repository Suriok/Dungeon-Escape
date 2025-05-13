module cz.cvut.fel.pjv.golyakat.dungeon_escape {
    requires java.desktop;
    requires java.logging;


    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.dataformat.xml;

    exports cz.cvut.fel.pjv.golyakat.dungeon_escape;

    opens cz.cvut.fel.pjv.golyakat.dungeon_escape
            to com.fasterxml.jackson.databind, com.fasterxml.jackson.dataformat.xml;

}