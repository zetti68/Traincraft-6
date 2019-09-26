package train.common.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockGeneratorWindMill extends ItemBlock{

	public ItemBlockGeneratorWindMill(Block id) {
		super(id);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		par3List.add("\u00a77" + "RF generator.");
		par3List.add("\u00a77" + "Max Production (storm): 15 RF/t.");
	}
}
