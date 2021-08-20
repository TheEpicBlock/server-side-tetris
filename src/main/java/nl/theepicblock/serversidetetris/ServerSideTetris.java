package nl.theepicblock.serversidetetris;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ItemGroup;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;

public class ServerSideTetris implements ModInitializer {
	public static TetrisItem TETRIS_ITEM = new TetrisItem(new FabricItemSettings().maxCount(1).group(ItemGroup.MISC));
	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("serversidetetris","tetrisitem"), TETRIS_ITEM);

		CommandRegistrationCallback.EVENT.register((thing, thingg) -> {
			thing.register(CommandManager.literal("testtest").executes((ctx) -> {
				var player = ctx.getSource().getPlayer();

				var dust = new DustParticleEffect(new Vec3f(1, 1, 1), 0.1f);
				var packet = new ParticleS2CPacket(dust, true, player.getX()+1, player.getY()+1, player.getZ(), 0, 0, 0, 0, 0);

				player.networkHandler.sendPacket(packet);
				return 1;
			}));
		});
	}
}
