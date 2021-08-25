package nl.theepicblock.serversidetetris.mixin;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import nl.theepicblock.serversidetetris.ServerSideTetris;
import nl.theepicblock.serversidetetris.TetrisState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class MixinSwingHand {
    @Inject(method = "swingHand(Lnet/minecraft/util/Hand;)V", at = @At("HEAD"))
    private void onSwingHand(Hand hand, CallbackInfo ci) {
        var player = ((ServerPlayerEntity)(Object)this);
        var stack = player.getStackInHand(hand);

        if (stack.getItem() == ServerSideTetris.TETRIS_ITEM) {
            var state = TetrisState.fromItem(stack);

            state.onClick(player.isSneaking(), false);

            var stateNbt = new NbtCompound();
            state.writeToNbt(stateNbt);
            stack.setSubNbt("state", stateNbt);

            player.setStackInHand(hand, stack);
        }
    }
}
