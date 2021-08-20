package nl.theepicblock.serversidetetris;

import eu.pb4.polymer.item.VirtualItem;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TetrisItem extends Item implements VirtualItem {
    public static Vec3f BLACK = new Vec3f(0, 0, 0);
    public static Vec3f WHITE = new Vec3f(1, 1, 1);
    public static Vec3f RED = new Vec3f(1, 0, 0);
    public static Vec3f GREEN = new Vec3f(0, 1, 0);

    public static String[] GRID = {
            "BBBBBBBBBBBBBBBBBB",
            "BBBBBBBBBBWWWWBBBB",
            "BBBBBBBBBBWWWWBBBB",
            "BBBBBRRRRRWWWWBBBB",
            "BBBBBRRRRRWWWWBBBB",
            "BBBBBRRRRRRRBBBBBB",
            "BBBBBRRRRRRRBBBBBB",
            "BBBBBRRRRRRRBBBBBB",
            "BBBBBBBBBBBBBBBBBB",
            "BBBBBBBBBBBBBBBBBB",
            "BBBBBBBBBGGGGGBBBB",
            "BBBBBBBBBGGGGGBBBB",
            "BBBBBBBBBBBBBBBBBB",
            "BBBBBBBBBBBBBBBBBB",
            "BBBBBBBBBBBBBBBBBB",
            "BBBBBBBBBBBBBBBBBB",
            "BBBBBBBBBBBBBBBBBB",
            "BBBBBBBBBBBBBBBBBB",
            "BBBBBRBBBRBBBBBBBB",
            "BBBBBBBBBBBBBBBBBB",
            "BBBBBRBBBRBBBBBBBB",
            "BBBBBBRRRBBBBBBBBB",
            "BBBBBBBBBBBBBBBBBB",
            "BBBBBBBBBBBBBBBBBB",
            "BBBBBBBBBBBBBBBBBB",
    };

    public TetrisItem(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        selected = true; //Debugging
        if (selected && entity instanceof ServerPlayerEntity player) {
            int y = 7;
            double scale = 0.02;
            for (String row : GRID) {
                y--;
                var chars = row.toCharArray();
                int x = -9;
                for (char c : chars) {
                    x++;
                    var colour = switch (c) {
                        case 'B' -> BLACK;
                        case 'W' -> WHITE;
                        case 'G' -> GREEN;
                        case 'R' -> RED;
                        default -> WHITE;
                    };
                    ParticleUtil.sendRelative(colour, 0.1f, x * scale, y * scale, 1, 0, 0, 0, 0, 6, player);
                }
            }
//            ParticleUtil.sendRelative(WHITE, 0.1f, 0, 0, 1, 0, 0, 0, 0, 500, player);
//            ParticleUtil.sendRelative(RED, 0.1f, 0.1, 0, 1,0, 0, 0, 0, 500, player);
//            ParticleUtil.sendRelative(GREEN, 0.1f, 0, 0.1, 1, 0, 0, 0, 0, 500, player);
        }
    }

    private void drawLine() {

    }

    @Override
    public Item getVirtualItem() {
        return Items.BAKED_POTATO;
    }

    public static final ItemStack STACK = new ItemStack(Items.BAKED_POTATO, 1);
    @Override
    public ItemStack getVirtualItemStack(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return STACK;
    }

    static {
        STACK.setCustomName(new LiteralText("Some Console"));
    }
}
