package nl.theepicblock.serversidetetris;

import java.util.ArrayList;

public enum Tetromino {
    LINE("xzxx"),
    L("xoo\nzxx"),
    REVERSE_L("oox\nxxz"),
    SQUARE("xz\nxx"),
    WIGGLY("xxo\nozx"),
    REVERSE_WIGGLY("oxx\nxzo"),
    T("oxo\nxzx");

    public static final Tetromino[] VALUES = Tetromino.values();
    private final Vec2i[] points;

    Tetromino(String template) {
        var points = new ArrayList<Vec2i>();
        Vec2i center = null;

        // Iterate template and add points
        int y = 0;
        for (String row : template.split("\n")) {
            int x = 0;
            for (char c : row.toCharArray()) {
                if (c != 'o') points.add(new Vec2i(x, y));
                if (c == 'z') center = new Vec2i(x, y);
                x++;
            }
            y--;
        }

        // Center points
        if (center == null) throw new NullPointerException();
        for (int i = 0; i < points.size(); i++) {
            points.set(i, points.get(i).subtract(center));
        }

        this.points = points.toArray(new Vec2i[0]);
    }

    public Vec2i[] getPoints() {
        return points;
    }
}
