package crazypants.enderio.gui;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.input.Keyboard;

import buildcraft.api.fuels.IronEngineFuel;
import buildcraft.api.fuels.IronEngineFuel.Fuel;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import crazypants.enderio.Config;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.util.Lang;

public class TooltipAddera {


  public static TooltipAddera instance = new TooltipAddera();

  static {
    MinecraftForge.EVENT_BUS.register(instance);
  }

  @SubscribeEvent
  public void addTooltip(ItemTooltipEvent evt) {
    if(evt.itemStack == null) {
      return;
    }
    if(Config.addFurnaceFuelTootip) {
      int time = TileEntityFurnace.getItemBurnTime(evt.itemStack);
      if(time > 0) {
        evt.toolTip.add("Burn time " + time);
      }
    }

    if(evt.itemStack.getItem() instanceof IAdvancedTooltipProvider) {
      IAdvancedTooltipProvider ttp = (IAdvancedTooltipProvider)evt.itemStack.getItem();
      addInformation(ttp, evt.itemStack, evt.entityPlayer, evt.toolTip, false);
      return;
    }

    Block blk = Block.getBlockFromItem(evt.itemStack.getItem());
    if(blk instanceof IAdvancedTooltipProvider) {
      addInformation((IAdvancedTooltipProvider)blk, evt.itemStack, evt.entityPlayer, evt.toolTip, false);
      return;
    }

    if(Config.addFuelTooltipsToAllFluidContainers) {
      addTooltipForFluid(evt.toolTip, evt.itemStack);
    }
  }

  public static void addTooltipForFluid(List list, ItemStack stk) {
    FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(stk);
    if(fluidStack == null) {
      return;
    }
    addTooltipForFluid(list, fluidStack.getFluid());
  }

  public static void addTooltipForFluid(List list, Fluid fluid) {
    if(fluid != null) {
      Fuel fuel = IronEngineFuel.getFuelForFluid(fluid);
      if(fuel != null) {
        if(showAdvancedTooltips()) {
          list.add(Lang.localize("fuel.tooltip.heading"));
          list.add(EnumChatFormatting.ITALIC + " " + PowerDisplayUtil.formatPowerPerTick(fuel.powerPerCycle));
          list.add(EnumChatFormatting.ITALIC + " " + fuel.totalBurningTime + " " + Lang.localize("fuel.tooltip.burnTime"));
        } else {
          addShowDetailsTooltip(list);
        }
      }
    }
  }

  public static void addInformation(IAdvancedTooltipProvider tt, ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    tt.addCommonEntries(itemstack, entityplayer, list, flag);
    if(showAdvancedTooltips()) {
      tt.addAdvancedEntries(itemstack, entityplayer, list, flag);
    } else {
      tt.addBasicEntries(itemstack, entityplayer, list, flag);
      addShowDetailsTooltip(list);
    }
  }

  public static void addShowDetailsTooltip(List list) {
    list.add(EnumChatFormatting.WHITE + "" + EnumChatFormatting.ITALIC + Lang.localize("item.tooltip.showDetails"));
  }

  public static boolean showAdvancedTooltips() {
    return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
  }

  public static void addDescriptionFromResources(List list,ItemStack itemstack) {
    if(itemstack.getItem() == null) {
      return;
    }
    Block blk = Block.getBlockFromItem(itemstack.getItem());
    if(blk == null) {
      return;
    }
    String keyBase = blk.getUnlocalizedName() + ".description.line";
    boolean done = false;
    int line = 1;
    while(!done) {
      String key = keyBase + line;
      String val = Lang.localize(key, false);
      if(val == null || val.trim().length() < 0 || val.equals(key) || line > 12) {
        done = true;
      } else {
        list.add(val);
        line++;
      }
    }
  }


}
