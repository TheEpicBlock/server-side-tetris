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
    /**
     * Stores an array of points for each of the 4 rotations
     */
    private final Vec2i[][] points;

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

        this.points = new Vec2i[4][];
        this.points[0] = points.toArray(new Vec2i[0]);

        // Do different rotations
        // 90
        var rotPoints = new ArrayList<Vec2i>();
        for (var p : points) {
            rotPoints.add(new Vec2i(p.y(), -p.x()));
        }
        this.points[1] = rotPoints.toArray(new Vec2i[0]);

        // 180
        rotPoints = new ArrayList<>();
        for (var p : points) {
            rotPoints.add(new Vec2i(-p.x(), -p.y()));
        }
        this.points[2] = rotPoints.toArray(new Vec2i[0]);

        // 270
        rotPoints = new ArrayList<>();
        for (var p : points) {
            rotPoints.add(new Vec2i(-p.y(), p.x()));
        }
        this.points[3] = rotPoints.toArray(new Vec2i[0]);
    }

    public Vec2i[] getPoints(byte rotation) {
        return points[rotation];
    }
}
