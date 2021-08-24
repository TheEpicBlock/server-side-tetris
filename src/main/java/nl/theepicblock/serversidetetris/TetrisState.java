package nl.theepicblock.serversidetetris;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Util;

import java.util.Random;

public class TetrisState {
    public static final int WIDTH  = 10;
    public static final int HEIGHT = 20;
    private static final Vec2i TETROMINO_SPAWN = new Vec2i(WIDTH/2, HEIGHT-1);
    private static final Vec2i DOWN = new Vec2i(0, -1);
    private static final DyeColor[] COLOURS = {DyeColor.ORANGE, DyeColor.MAGENTA, DyeColor.LIGHT_BLUE, DyeColor.YELLOW, DyeColor.LIME, DyeColor.PINK, DyeColor.CYAN, DyeColor.PURPLE, DyeColor.BLUE, DyeColor.GREEN, DyeColor.RED};

    private final byte[] area = new byte[WIDTH * HEIGHT];
    private Tetromino currentTetromino = null;
    private Vec2i tetrominoPos = null;
    private DyeColor tetrominoColour = null;
    private byte tick;

    public TetrisState(NbtCompound nbt) {
        var area = nbt.getByteArray("area");
        System.arraycopy(area, 0, this.area, 0, Math.min(area.length, WIDTH * HEIGHT));

        this.currentTetromino = Tetromino.VALUES[nbt.getByte("tetromino")];
        this.tetrominoPos = new Vec2i(nbt.getInt("tetrominoX"), nbt.getInt("tetrominoY"));
        this.tetrominoColour = fromColourId(nbt.getByte("terominoColour"));
        this.tick = nbt.getByte("tick");
    }

    public TetrisState() {

    }

    public void newTetromino(Random random) {
        currentTetromino = Util.getRandom(Tetromino.VALUES, random);
        tetrominoColour = Util.getRandom(COLOURS, random);

        tetrominoPos = TETROMINO_SPAWN;
    }

    public void tick() {
        if (currentTetromino == null) newTetromino(new Random());
        tick++;

        if (tick % 40 == 0) {
            tick = 0;
            var prevPos = tetrominoPos;
            tetrominoPos = tetrominoPos.add(DOWN);

            if (doesCurrentTetrominoCollide()) {
                tetrominoPos = prevPos;
                var colourId = getColourId(tetrominoColour);
                for (Vec2i point : currentTetromino.getPoints()) {
                    areaSet(point.add(tetrominoPos), colourId);
                }
                newTetromino(new Random());
            }
        }
    }

    private boolean doesCurrentTetrominoCollide() {
        var points = currentTetromino.getPoints();

        for (Vec2i point : points) {
            point = point.add(tetrominoPos);
            if (point.y() < 0) return true;
            if (areaGet(point) != 0) return true;
        }
        return false;
    }

    public void areaSet(Vec2i pos, byte v) {
        areaSet(pos.x(), pos.y(), v);
    }

    public void areaSet(int x, int y, byte v) {
        area[x + y*WIDTH] = v;
    }

    public byte areaGet(Vec2i pos) {
        return areaGet(pos.x(), pos.y());
    }

    public byte areaGet(int x, int y) {
        return area[x + y*WIDTH];
    }

    public byte[] getArea() {
        return area;
    }

    public static byte getColourId(DyeColor colour) {
        if (colour == DyeColor.BLACK) return 0;
        return (byte)(colour.getId()+1);
    }

    public static DyeColor fromColourId(byte v) {
        if (v == 0) return DyeColor.BLACK;
        return DyeColor.byId(v-1);
    }

    public static TetrisState fromItem(ItemStack stack) {
        var stateNbt = stack.getSubNbt("state");
        return stateNbt == null ? new TetrisState() : new TetrisState(stateNbt);
    }

    public byte[] getTruncatedAreaArray() {
        int lastNonZeroByteIndex = 0;
        int i = 0;
        for (byte b : area) {
            i++;
            if (b != 0) lastNonZeroByteIndex = i;
        }

        var newArray = new byte[lastNonZeroByteIndex];
        System.arraycopy(area, 0, newArray, 0, lastNonZeroByteIndex);
        return newArray;
    }

    public void writeToNbt(NbtCompound nbt) {
        nbt.putByteArray("area", getTruncatedAreaArray());
        nbt.putByte("tetromino", (byte)currentTetromino.ordinal());
        nbt.putInt("tetrominoX", tetrominoPos.x());
        nbt.putInt("tetrominoY", tetrominoPos.y());
        nbt.putByte("terominoColour", getColourId(tetrominoColour));
        nbt.putByte("tick", tick);
    }
}
