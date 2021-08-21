package nl.theepicblock.serversidetetris;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;

public class TetrisState {
    private static final int WIDTH  = 10;
    private static final int HEIGHT = 20;

    private final byte[] area = new byte[WIDTH * HEIGHT];
    private Tetromino currentTetromino = null;
    private Vec2i tetrominoPos = null;
    private DyeColor tetrominoColour;

    public TetrisState(NbtCompound nbt) {
        var area = nbt.getByteArray("area");
        System.arraycopy(area, 0, this.area, 0, Math.min(area.length, WIDTH * HEIGHT));
    }

    public TetrisState() {

    }

    public TetrisState fromItem(ItemStack stack) {
        var stateNbt = stack.getSubNbt("state");
        return stateNbt == null ? new TetrisState() : new TetrisState(stateNbt);
    }

    public byte[] getTruncatedAreaArray() {
        int lastNonZeroByteIndex = 0;
        int i = 0;
        for (byte b : area) {
            if (b != 0) lastNonZeroByteIndex = i;
            i++;
        }

        var newArray = new byte[lastNonZeroByteIndex];
        System.arraycopy(area, 0, newArray, 0, lastNonZeroByteIndex);
        return newArray;
    }

    public void writeToNbt(NbtCompound nbt) {
        nbt.putByteArray("area", getTruncatedAreaArray());
    }
}
