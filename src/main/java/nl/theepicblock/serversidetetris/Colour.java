package nl.theepicblock.serversidetetris;

import net.minecraft.util.math.Vec3f;

public enum Colour {
    NONE(0x000000),
    RED(0xfc5560),
    LIME(0x20f90c),
    BLUE(0x6af7f7),
    PINK(0xfc71fa);

    public static final Colour[] VALUES = Colour.values();
    public static final Colour[] COLOURS = {RED, LIME, BLUE, PINK};
    public final Vec3f asVec;

    @SuppressWarnings("PointlessBitwiseExpression")
    Colour(int hex) {
        int r = (hex & 0xFF0000) >> 16;
        int g = (hex & 0x00FF00) >> 8;
        int b = (hex & 0x0000FF) >> 0;

        asVec = new Vec3f(r / 255f, g / 255f, b / 255f);
    }
}
