package com.flemmli97.mobbattle.items;

import com.flemmli97.mobbattle.MobBattleTab;
import com.flemmli97.mobbattle.handler.LibTags;
import com.flemmli97.mobbattle.handler.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.List;

public class MobArmy extends Item {

    public MobArmy() {
        super(new Item.Properties().maxStackSize(1).group(MobBattleTab.customTab));
    }

    @Override
    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        return !player.isCreative();
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag b) {
        list.add(new TranslationTextComponent("tooltip.army.first").mergeStyle(TextFormatting.AQUA));
        list.add(new TranslationTextComponent("tooltip.army.second").mergeStyle(TextFormatting.AQUA));
        list.add(new TranslationTextComponent("tooltip.army.third").mergeStyle(TextFormatting.AQUA));
        list.add(new TranslationTextComponent("tooltip.army.forth").mergeStyle(TextFormatting.AQUA));
    }

    public BlockPos[] getSelPos(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundNBT compound = stack.getTag();
            BlockPos pos1 = null;
            if (compound.contains(LibTags.savedPos1) && compound.getIntArray(LibTags.savedPos1).length == 3)
                pos1 = new BlockPos(compound.getIntArray(LibTags.savedPos1)[0], compound.getIntArray(LibTags.savedPos1)[1], compound.getIntArray(LibTags.savedPos1)[2]);
            BlockPos pos2 = null;
            if (compound.contains(LibTags.savedPos2) && compound.getIntArray(LibTags.savedPos2).length == 3)
                pos2 = new BlockPos(compound.getIntArray(LibTags.savedPos2)[0], compound.getIntArray(LibTags.savedPos2)[1], compound.getIntArray(LibTags.savedPos2)[2]);
            return new BlockPos[]{pos1, pos2};
        }
        return new BlockPos[]{null, null};
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext ctx) {
        ItemStack stack = ctx.getItem();
        if (!ctx.getWorld().isRemote) {
            CompoundNBT compound = stack.getTag();
            if (compound == null)
                compound = new CompoundNBT();
            if (!compound.contains(LibTags.savedPos1) || compound.getIntArray(LibTags.savedPos1).length != 3) {
                compound.putIntArray(LibTags.savedPos1, new int[]{ctx.getPos().getX(), ctx.getPos().getY(), ctx.getPos().getZ()});
            } else if (!ctx.getPos().equals(
                    new BlockPos(compound.getIntArray(LibTags.savedPos1)[0], compound.getIntArray(LibTags.savedPos1)[1], compound.getIntArray(LibTags.savedPos1)[2]))) {
                compound.putIntArray(LibTags.savedPos2, new int[]{ctx.getPos().getX(), ctx.getPos().getY(), ctx.getPos().getZ()});
            }
            stack.setTag(compound);
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote && stack.hasTag()) {
            if (player.isSneaking()) {
                stack.getTag().remove(LibTags.savedPos1);
                stack.getTag().remove(LibTags.savedPos2);
                player.sendMessage(new TranslationTextComponent("tooltip.army.reset").mergeStyle(TextFormatting.RED), player.getUniqueID());
            } else if (stack.getTag().contains(LibTags.savedPos1) && stack.getTag().contains(LibTags.savedPos2)) {
                BlockPos pos1 = new BlockPos(stack.getTag().getIntArray(LibTags.savedPos1)[0], stack.getTag().getIntArray(LibTags.savedPos1)[1],
                        stack.getTag().getIntArray(LibTags.savedPos1)[2]);
                BlockPos pos2 = new BlockPos(stack.getTag().getIntArray(LibTags.savedPos2)[0], stack.getTag().getIntArray(LibTags.savedPos2)[1],
                        stack.getTag().getIntArray(LibTags.savedPos2)[2]);
                AxisAlignedBB bb = Utils.getBoundingBoxPositions(pos1, pos2);
                List<MobEntity> list = player.world.getEntitiesWithinAABB(MobEntity.class, bb);
                String team = stack.hasDisplayName() ? stack.getDisplayName().getUnformattedComponentText() : "DEFAULT";

                for (MobEntity living : list) {
                    Utils.updateEntity(team, living);
                }
                player.sendMessage(new TranslationTextComponent("tooltip.armor.add.box", team).mergeStyle(TextFormatting.GOLD), player.getUniqueID());
            }
        }
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        if (entity instanceof MobEntity && !player.world.isRemote) {
            String team = stack.hasDisplayName() ? stack.getDisplayName().getUnformattedComponentText() : "DEFAULT";
            Utils.updateEntity(team, (MobEntity) entity);
            player.sendMessage(new TranslationTextComponent("tooltip.armor.add", team).mergeStyle(TextFormatting.GOLD), player.getUniqueID());
        }
        return true;
    }
}
