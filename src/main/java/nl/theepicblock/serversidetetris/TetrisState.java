package nl.theepicblock.serversidetetris;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Util;

import java.util.Arrays;
import java.util.Random;

public class TetrisState {
    public static final int WIDTH  = 10;
    public static final int HEIGHT = 20;
    private static final Vec2i TETROMINO_SPAWN = new Vec2i(WIDTH/2, HEIGHT-1);
    private static final Vec2i DOWN = new Vec2i(0, -1);
    private static final Vec2i LEFT = new Vec2i(-1, 0);
    private static final Vec2i RIGHT = new Vec2i(1, 0);

    private final byte[] area = new byte[WIDTH * HEIGHT];
    private Tetromino currentTetromino = null;
    private Vec2i tetrominoPos = null;
    private Colour tetrominoColour = null;
    private byte tetronimoRotation = 0;
    private byte tick;
    private int score = 0;
    public boolean justDied = false;
    public boolean scoreChanged = false;

    public TetrisState(NbtCompound nbt) {
        var area = nbt.getByteArray("area");
        System.arraycopy(area, 0, this.area, 0, Math.min(area.length, WIDTH * HEIGHT));

        this.currentTetromino = Tetromino.VALUES[nbt.getByte("tetromino")];
        this.tetrominoPos = new Vec2i(nbt.getInt("tetrominoX"), nbt.getInt("tetrominoY"));
        this.tetrominoColour = fromColourId(nbt.getByte("terominoColour"));
        this.tetronimoRotation = nbt.getByte("terominoRotation");
        this.tick = nbt.getByte("tick");
        this.score = nbt.getInt("score");

        if (tetronimoRotation > 4 || tetronimoRotation < 0) tetronimoRotation = 0;
    }

    public TetrisState() {

    }

    public void onClick(boolean isShifting, boolean isRight) {
        if (isShifting) {
            var newPos = this.tetrominoPos.add(isRight ? RIGHT : LEFT);
            if (!collides(currentTetromino, newPos, tetronimoRotation)) {
                this.tetrominoPos = newPos;
            }
        } else {
            if (currentTetromino == Tetromino.SQUARE) return;

            byte newRot = (byte)(tetronimoRotation + (isRight ? 1 : -1));
            if (newRot == -1) newRot = 3;
            if (newRot == 4)  newRot = 0;

            if (!collides(this.currentTetromino, this.tetrominoPos, newRot)) {
                tetronimoRotation = newRot;
            }
        }
    }

    public void newTetromino(Random random) {
        currentTetromino = Util.getRandom(Tetromino.VALUES, random);
        tetrominoColour = Util.getRandom(Colour.COLOURS, random);

        tetrominoPos = TETROMINO_SPAWN;
    }

    public void tick() {
        if (currentTetromino == null) newTetromino(new Random());
        tick++;

        if (tick % 5 == 0) {
            tick = 0;
            var prevPos = tetrominoPos;
            tetrominoPos = tetrominoPos.add(DOWN);

            if (collides(currentTetromino, tetrominoPos, tetronimoRotation)) {
                tetrominoPos = prevPos;
                var colourId = getColourId(tetrominoColour);
                for (var point : currentTetromino.getPoints(tetronimoRotation)) {
                    var transfPos = point.add(tetrominoPos);
                    if (transfPos.x() < 0 || transfPos.x() >= WIDTH) continue;

                    if (transfPos.y() >= HEIGHT-1) {
                        justDied = true;
                        return;
                    }

                    areaSet(transfPos, colourId);
                }
                newTetromino(new Random());

                checkClearLines();
            }
        }
    }

    private boolean collides(Tetromino tetromino, Vec2i pos, byte rot) {
        for (Vec2i point : tetromino.getPoints(rot)) {
            point = point.add(pos);
            if (point.y() < 0) return true;
            if (point.x() < 0 || point.x() >= WIDTH) return true;
            if (point.y() >= HEIGHT) continue;
            if (areaGet(point) != 0) return true;
        }
        return false;
    }

    private void checkClearLines() {
        var linesCleared = new IntArrayList();
        for (int y = HEIGHT-1; y >= 0; y--) {
            var count = 0;
            for (int x = 0; x < WIDTH; x++) {
                var val = areaGet(x, y);
                if (val != 0) count++;
            }

            if (count == WIDTH) {
                linesCleared.add(y);
            }
        }

        var pointsEarned = switch (linesCleared.size()) {
            case 1 -> 10;
            case 2 -> 25;
            case 3 -> 50;
            case 4 -> 100;
            default -> 0;
        };

        this.score += pointsEarned;
        if (pointsEarned != 0) scoreChanged = true;

        for (int i : linesCleared) {
            System.arraycopy(this.area, (i+1)*WIDTH, this.area, i*WIDTH, this.area.length-(i+1)*WIDTH); // Shift down
            Arrays.fill(this.area, this.area.length-WIDTH, this.area.length, (byte)0); // Clear top line
        }
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

    public Tetromino getCurrentTetromino() {
        return currentTetromino;
    }

    public Colour getTetrominoColour() {
        return tetrominoColour;
    }

    public Vec2i getTetrominoPos() {
        return tetrominoPos;
    }

    public byte getTetronimoRotation() {
        return tetronimoRotation;
    }

    public int getScore() {
        return score;
    }

    public static byte getColourId(Colour colour) {
        return (byte)(colour.ordinal());
    }

    public static Colour fromColourId(byte v) {
        if (v < 0 || v > Colour.VALUES.length) return Colour.RED;
        return Colour.VALUES[v];
    }

    public static TetrisState fromItem(ItemStack stack) {
        var stateNbt = stack.getSubNbt("game");
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
        nbt.putByte("terominoRotation", tetronimoRotation);
        nbt.putByte("tick", tick);
        nbt.putInt("score", score);
    }
}
