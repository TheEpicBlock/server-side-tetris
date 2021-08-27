package nl.theepicblock.serversidetetris;

import eu.pb4.polymer.item.VirtualItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TetrisItem extends Item implements VirtualItem {

    public TetrisItem(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (entity instanceof ServerPlayerEntity player) {
            TetrisState state = null;

            if (TetrisItem.isGameActive(stack)) {
                state = TetrisItem.tickGame(stack, player);
            }

            if (selected) {
                if (state == null) state = TetrisState.fromItem(stack);

                if (TetrisItem.isGameActive(stack)) {
                    TetrisDisplay.displayGame(player, state);
                } else {
                    TetrisDisplay.displayStartScreen(player);
                }
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) return super.use(world, user, hand);
        var stack = user.getStackInHand(hand);

        onClick(stack, user, user.isSneaking(), true);

        return TypedActionResult.success(stack, world.isClient());
    }

    public static void onClick(ItemStack stack, PlayerEntity playerEntity, boolean isShifting, boolean isRight) {
        if (isGameActive(stack)) {
            var state = TetrisState.fromItem(stack);

            state.onClick(isShifting, isRight);

            var stateNbt = new NbtCompound();
            state.writeToNbt(stateNbt);
            stack.setSubNbt("game", stateNbt);
        } else {
            if (isRight) {
                // Start new game
                var state = new TetrisState();
                state.newTetromino(playerEntity.getRandom());

                var stateNbt = new NbtCompound();
                state.writeToNbt(stateNbt);
                stack.setSubNbt("game", stateNbt);
            } else {
                playerEntity.sendMessage(new LiteralText("The current stored highscore is: "+getHighscore(stack) + " by " + getHighscorer(stack)), false);
            }
        }
    }

    public static TetrisState tickGame(ItemStack stack, ServerPlayerEntity playerEntity) {
        var state = TetrisState.fromItem(stack);
        state.tick(playerEntity.getRandom());

        if (state.scoreChanged) {
            playerEntity.sendMessage(new LiteralText("Your score: "+state.getScore()), true);
        }

        if (state.justDied) {
            if (state.getScore() > getHighscore(stack)) {
                playerEntity.sendMessage(new LiteralText("New highscore! "+state.getScore()), false);
                setHighscore(stack, playerEntity.getEntityName(), state.getScore());
            } else {
                playerEntity.sendMessage(new LiteralText("Your score was "+state.getScore()), false);
            }

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

    public static void setHighscore(ItemStack stack, String name, int v) {
        var nbt = stack.getSubNbt("state");
        if (nbt == null) nbt = new NbtCompound();
        nbt.putInt("highscore", v);
        nbt.putString("highscorer", name);
        stack.setSubNbt("state", nbt);
    }

    public static int getHighscore(ItemStack stack) {
        var nbt = stack.getSubNbt("state");
        if (nbt == null) return 0;
        return nbt.getInt("highscore");
    }

    public static String getHighscorer(ItemStack stack) {
        var nbt = stack.getSubNbt("state");
        if (nbt == null) return "nobody";
        var res = nbt.getString("highscorer");
        return res.equals("") ? "nobody" : res;
    }

    @Override
    public Item getVirtualItem() {
        return Items.CAULDRON;
    }

    public static final ItemStack stack;

    @Override
    public ItemStack getVirtualItemStack(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return stack.copy();
    }

    static {
        stack = new ItemStack(Items.CAULDRON, 1);
        stack.setCustomName(new LiteralText("Some Old Console"));

        var loreList = new NbtList();
        var displayNbt = stack.getOrCreateSubNbt(ItemStack.DISPLAY_KEY);

        loreList.add(NbtString.of(Text.Serializer.toJson(new LiteralText("left/right click to rotate").formatted(Formatting.BLUE))));
        loreList.add(NbtString.of(Text.Serializer.toJson(new LiteralText("shift + left/right click to move").formatted(Formatting.BLUE))));

        displayNbt.put("Lore", loreList);
    }

    @Override
    public void modifyTooltip(List<Text> tooltip, ItemStack stack, @Nullable ServerPlayerEntity player) {
        VirtualItem.super.modifyTooltip(tooltip, stack, player);
    }
}
