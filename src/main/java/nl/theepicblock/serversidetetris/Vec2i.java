package nl.theepicblock.serversidetetris;

public record Vec2i(int x, int y) {
    public Vec2i subtract(Vec2i other) {
        return new Vec2i(x-other.x, y-other.y);
    }

    public Vec2i add(Vec2i other) {
        return new Vec2i(x+other.x, y+other.y);
    }

    public Vec2i clamp(int minx, int miny, int maxx, int maxy) {
        boolean changed = false;
        int newx = x;
        int newy = y;

        if (x < minx) {
            newx = minx; changed = true;
        } else if (x > maxx) {
            newx = maxx; changed = true;
        }

        if (y < miny) {
            newy = miny; changed = true;
        } else if (y > maxy) {
            newy = maxy; changed = true;
        }

        return changed ? new Vec2i(newx, newy) : this;
    }
}
