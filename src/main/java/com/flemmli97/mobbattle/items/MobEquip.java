package com.flemmli97.mobbattle.items;

import java.util.List;

import com.flemmli97.mobbattle.MobBattle;
import com.flemmli97.mobbattle.ModItems;
import com.flemmli97.mobbattle.items.entitymanager.EntityAIItemPickup;
import com.flemmli97.mobbattle.items.entitymanager.Team;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MobEquip extends ItemSword{
		
	public MobEquip()
	{
		super(ModItems.mob_mat);
        this.setUnlocalizedName("mob_equip");
        this.setMaxStackSize(1);
        this.setCreativeTab(MobBattle.customTab);
        this.setRegistryName(new ResourceLocation(MobBattle.MODID, "mob_equip"));
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.NONE;
	}

	@Override
	public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
		return HashMultimap.create();
	}
	
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag b) {
		list.add(TextFormatting.AQUA + "Right click block to set first, and then second corner of the box");
		list.add(TextFormatting.AQUA + "Right click into air to to make entities able to pickup items");
		list.add(TextFormatting.AQUA + "Shift-Right click to reset box");
	}
	
	public BlockPos[] getSelPos(ItemStack stack)
	{
		if(stack.hasTagCompound())
		{
			NBTTagCompound compound = stack.getTagCompound();
			BlockPos pos1=null;
			if(compound.hasKey("Position1") && compound.getIntArray("Position1")!=null)
				pos1 = new BlockPos(compound.getIntArray("Position1")[0], compound.getIntArray("Position1")[1], compound.getIntArray("Position1")[2]);
			BlockPos pos2=null;
			if(compound.hasKey("Position2") && compound.getIntArray("Position2")!=null)
				pos2 = new BlockPos(compound.getIntArray("Position2")[0], compound.getIntArray("Position2")[1], compound.getIntArray("Position2")[2]);
			return new BlockPos[] {pos1, pos2};
		}
		return new BlockPos[] {null, null};
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = playerIn.getHeldItem(hand);
		if(stack.getMetadata()==0 || stack.getMetadata()==2)
		{
			NBTTagCompound compound = stack.getTagCompound();
			if(compound==null)
				compound = new NBTTagCompound();
			if(!compound.hasKey("Position1") || compound.getIntArray("Position1")==null)
			{
				compound.setIntArray("Position1", new int[] {pos.getX(), pos.getY(), pos.getZ()});
			}
			else if(!pos.equals(new BlockPos(compound.getIntArray("Position1")[0], compound.getIntArray("Position1")[1], compound.getIntArray("Position1")[2])))
			{
				compound.setIntArray("Position2", new int[] {pos.getX(), pos.getY(), pos.getZ()});
			}
			stack.setTagCompound(compound);
	        return EnumActionResult.SUCCESS;
		}
        return EnumActionResult.PASS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if(player.isSneaking() && stack.hasTagCompound())
		{
			stack.getTagCompound().removeTag("Position1");
			stack.getTagCompound().removeTag("Position2");
			if(!player.world.isRemote)
				player.sendMessage(new TextComponentString(TextFormatting.RED + "Reset Positions"));
		}
		else if(stack.getTagCompound().hasKey("Position1") && stack.getTagCompound().hasKey("Position2"))
		{
			BlockPos pos1 = new BlockPos(stack.getTagCompound().getIntArray("Position1")[0],stack.getTagCompound().getIntArray("Position1")[1],stack.getTagCompound().getIntArray("Position1")[2]);
			BlockPos pos2 = new BlockPos(stack.getTagCompound().getIntArray("Position2")[0],stack.getTagCompound().getIntArray("Position2")[1],stack.getTagCompound().getIntArray("Position2")[2]);
			AxisAlignedBB bb = Team.getBoundingBoxPositions(pos1, pos2);
			List<EntityCreature> list = player.world.getEntitiesWithinAABB(EntityCreature.class, bb);
			for(EntityCreature living : list)
			{
				if(!player.world.isRemote)
				{
					living.addTag("PickUp");
					living.tasks.addTask(10, new EntityAIItemPickup(living));
				}
			}
			if(!player.world.isRemote)
			{
				player.sendMessage(new TextComponentString(TextFormatting.GOLD + "Entities in box can now pickup items"));
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		if (entity instanceof EntityCreature && !player.world.isRemote)
		{		
			entity.addTag("PickUp");
			((EntityCreature)entity).tasks.addTask(10, new EntityAIItemPickup((EntityCreature) entity));
			player.sendMessage(new TextComponentString(TextFormatting.GOLD + "Entity can pickup items now"));
		}
	    return true;
	}
	
	 @SideOnly(Side.CLIENT)
	    public void initModel() {
	        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	    }
}