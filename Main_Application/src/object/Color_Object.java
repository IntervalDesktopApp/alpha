package object;

import javafx.scene.paint.Color;

/**
 * Created by mac97 on 30.06.17.
 */
public class Color_Object {

    private String name;
    private Color color;

    public Color_Object(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }
}
