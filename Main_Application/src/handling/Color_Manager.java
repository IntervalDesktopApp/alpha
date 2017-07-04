package handling;

import javafx.scene.paint.Color;
import object.Color_Object;

import java.util.ArrayList;

/**
 * Created by mac97 on 30.06.17.
 */
public class Color_Manager {

    private ArrayList<Color_Object> colors = new ArrayList<>();

    public Color_Manager() {

        colors.add(new Color_Object("Blutrot", new Color(c(c(151)),c(14),c(14),0)));
        colors.add(new Color_Object("Dunkelrot", new Color(c(c(91)),c(19),c(19),0)));
        colors.add(new Color_Object("Rosa", new Color(c(226),c(95),c(95),0)));
        colors.add(new Color_Object("Orange", new Color(c(c(212)),c(91),c(11),0)));
        colors.add(new Color_Object("Blau", new Color(0,0,0.8,0)));

    }

    private double c(double value) {
        double rgb = value / 255;
        return rgb;
    }

    public void addColorObject(String name, Color color) {
        colors.add(new Color_Object(name, color));
    }

    public void setColors(ArrayList<Color_Object> colors) {
        this.colors = colors;
    }

    public ArrayList<Color_Object> getColors() {
        return colors;
    }
}
