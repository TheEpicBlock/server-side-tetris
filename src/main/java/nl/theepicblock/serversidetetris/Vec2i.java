package nl.theepicblock.serversidetetris;

public record Vec2i(int x, int y) {
    public Vec2i subtract(Vec2i other) {
        return new Vec2i(x-other.x, y-other.y);
    }

    public Vec2i add(Vec2i other) {
        return new Vec2i(x+other.x, y+other.y);
    }
}
