package nl.theepicblock.serversidetetris;

import eu.pb4.polymer.item.VirtualItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class TetrisItem extends Item implements VirtualItem {

    public TetrisItem(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        TetrisState state = null;

        if (TetrisItem.isGameActive(stack)) {
            state = TetrisItem.tickGame(stack);
        }

        if (selected && entity instanceof ServerPlayerEntity player) {
            if (state == null) state = TetrisState.fromItem(stack);

            if (TetrisItem.isGameActive(stack)) {
                TetrisDisplay.displayGame(player, state);
            } else {
                TetrisDisplay.displayStartScreen(player);
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) return super.use(world, user, hand);
        var stack = user.getStackInHand(hand);

        onClick(stack, user.isSneaking(), true);

        return TypedActionResult.success(stack, world.isClient());
    }

    public static void onClick(ItemStack stack, boolean isShifting, boolean isRight) {
        TetrisState state;
        if (isGameActive(stack)) {
            state = TetrisState.fromItem(stack);

            state.onClick(isShifting, isRight);

        } else {
            // Start new game
            state = new TetrisState();
            state.newTetromino(new Random());
        }
        var stateNbt = new NbtCompound();
        state.writeToNbt(stateNbt);
        stack.setSubNbt("game", stateNbt);
    }

    public static TetrisState tickGame(ItemStack stack) {
        var state = TetrisState.fromItem(stack);
        state.tick();

        if (state.justDied) {
            stack.removeSubNbt("game");
        } else {
            var stateNbt = new NbtCompound();
            state.writeToNbt(stateNbt);
            stack.setSubNbt("game", stateNbt);
        }

        return state;
    }

    public static boolean isGameActive(ItemStack stack) {
        return stack.getSubNbt("game") != null;
    }

    @Override
    public Item getVirtualItem() {
        return Items.BAKED_POTATO;
    }

    @Override
    public ItemStack getVirtualItemStack(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return new ItemStack(getVirtualItem(), 1).setCustomName(new LiteralText("Some Console"));
    }
}
