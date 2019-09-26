package train.common.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.SideOnly;
import train.common.Traincraft;
import train.common.library.Info;

public class ItemWirelessTransmitter extends Item {

    public ItemWirelessTransmitter()  {
        setCreativeTab(Traincraft.tcTab);

    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon(Info.modID.toLowerCase() + ":wireless_transmitter");
    }
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        par3List.add("\u00a77" + "Allows non W-MTC trains use W-MTC");
        par3List.add("\u00a77" + "Put this in the first inventory slot of a locomotive");
    }
}
