package crazypants.enderio.machine.monitor;

import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;

public class ItemMJReader extends Item {

  private static final NumberFormat NF = NumberFormat.getIntegerInstance();

  public static ItemMJReader create() {

    PacketHandler.instance.addPacketProcessor(MJReaderPacketHandler.getInstance());

    ItemMJReader result = new ItemMJReader();
    result.init();
    return result;
  }

  protected ItemMJReader() {
    super(ModObject.itemMJReader.id);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName("enderio." + ModObject.itemMJReader.name());
    setMaxStackSize(64);
  }

  @Override
  public boolean onItemUseFirst(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float par8,
      float par9, float par10) {

    if(player.isSneaking()) {
      TileEntity te = world.getBlockTileEntity(x, y, z);
      if(te instanceof IConduitBundle) {
        IConduitBundle cb = (IConduitBundle) te;
        Set<ForgeDirection> cons = new HashSet<ForgeDirection>();
        for (IConduit con : cb.getConduits()) {
          cons.addAll(con.getExternalConnections());
        }

        if(cons.isEmpty()) {
          return false;
        }
        if(cons.size() == 1) {
          player.openGui(EnderIO.instance, GuiHandler.GUI_ID_EXTERNAL_CONNECTION_BASE + cons.iterator().next().ordinal(), world, x, y, z);
          return true;
        }
        player.openGui(EnderIO.instance, GuiHandler.GUI_ID_EXTERNAL_CONNECTION_SELECTOR, world, x, y, z);

      }
      return false;
    }

    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(!(te instanceof IConduitBundle) && MJReaderPacketHandler.canCreatePacket(world, x, y, z)) {
      if(world.isRemote) {
        PacketDispatcher.sendPacketToServer(MJReaderPacketHandler.createInfoRequestPacket(x, y, z, side));
      }
      return true;
    }

    return false;
  }

  protected void init() {
    GameRegistry.registerItem(this, ModObject.itemMJReader.unlocalisedName);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IconRegister iconRegister) {
    itemIcon = iconRegister.registerIcon("enderio:mJReader");
  }

}
