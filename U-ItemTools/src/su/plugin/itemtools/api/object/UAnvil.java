package su.plugin.itemtools.api.object;

import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.ChatMessage;
import net.minecraft.server.v1_12_R1.ContainerAnvil;
import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.PacketPlayOutOpenWindow;
import net.minecraft.server.v1_12_R1.PlayerInventory;
import net.minecraft.server.v1_12_R1.World;
import su.plugin.itemtools.api.ItemToolsAPI;

public class UAnvil extends ContainerAnvil {

	public UAnvil(PlayerInventory playerinventory, World world, BlockPosition blockposition, EntityHuman entityhuman) {
		super(playerinventory, world, blockposition, entityhuman);
	}
	
	public static void openAnvil(Player player) {
		if(!ItemToolsAPI.getAnvilPlayers().contains(player.getUniqueId().toString())) {
			ItemToolsAPI.getAnvilPlayers().add(player.getUniqueId().toString());
		}
		
		EntityPlayer ep = ((CraftPlayer) player).getHandle();
		UAnvil anvil = new UAnvil(ep.inventory, ep.world, new BlockPosition(0, 0, 0), ep);
		int c = ep.nextContainerCounter();
		ep.playerConnection.sendPacket(new PacketPlayOutOpenWindow(c, "minecraft:anvil", new ChatMessage("Anvil", new Object[0]), 0));
		ep.activeContainer = anvil;
		ep.activeContainer.windowId = c;
		ep.activeContainer.addSlotListener(ep);
		
		player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 0.5F, 0.5F);
	}
	
}