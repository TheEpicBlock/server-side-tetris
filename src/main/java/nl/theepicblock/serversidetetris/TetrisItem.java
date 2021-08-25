package nl.theepicblock.serversidetetris;

import eu.pb4.polymer.item.VirtualItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TetrisItem extends Item implements VirtualItem {
    private static final float PROXIMITY = 0.02f;
    private static final float SCALE     = 0.1f;

    public TetrisItem(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        var state = TetrisState.fromItem(stack);
        state.tick();

        var stateNbt = new NbtCompound();
        state.writeToNbt(stateNbt);
        stack.setSubNbt("state", stateNbt);

        if (selected && entity instanceof ServerPlayerEntity player) {
            var area = state.getArea();
            var x = 0;
            var y = 0;

            for (byte b : area) {
                var colour = TetrisState.fromColourId(b);
                var colourComponents = new Vec3f(colour.getColorComponents()[0], colour.getColorComponents()[1], colour.getColorComponents()[2]);
                ParticleUtil.sendRelative(colourComponents, SCALE, x * PROXIMITY, y * PROXIMITY, 1, 0, 0, 0, 0, 6, player);

                x++;
                if (x >= TetrisState.WIDTH) {
                    x = 0; y++;
                }
            }
        }
    }

    @Override
    public boolean canMine(BlockState blockState, World world, BlockPos pos, PlayerEntity miner) {
        var stack = miner.getMainHandStack();
        var state = TetrisState.fromItem(stack);

        state.onClick(miner.isSneaking(), false);

        var stateNbt = new NbtCompound();
        state.writeToNbt(stateNbt);
        stack.setSubNbt("state", stateNbt);

        miner.equipStack(EquipmentSlot.MAINHAND, stack);
        return false;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) return super.use(world, user, hand);
        var stack = user.getStackInHand(hand);
        var state = TetrisState.fromItem(stack);

        state.onClick(user.isSneaking(), true);

        var stateNbt = new NbtCompound();
        state.writeToNbt(stateNbt);
        stack.setSubNbt("state", stateNbt);

        return TypedActionResult.success(stack, world.isClient());
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
