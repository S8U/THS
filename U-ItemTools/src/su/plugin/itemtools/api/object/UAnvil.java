package su.plugin.itemtools.api.object;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.ContainerAnvil;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutOpenWindow;
import net.minecraft.server.v1_8_R3.PlayerInventory;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
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
		
		player.playSound(player.getLocation(), Sound.ANVIL_USE, 0.5F, 0.5F);
	}
	
}