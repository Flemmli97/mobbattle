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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class MobStick extends Item {

    public MobStick() {
        super(new Item.Properties().maxStackSize(1).group(MobBattleTab.customTab));
    }

    @Override
    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        return !player.isCreative();
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag b) {
        if (stack.hasTag() && stack.getTag().contains(LibTags.savedEntityName)) {
            list.add(new TranslationTextComponent("tooltip.stick.contains", stack.getTag().getString(LibTags.savedEntityName)).mergeStyle(TextFormatting.GREEN));
        }
        list.add(new TranslationTextComponent("tooltip.stick.first").mergeStyle(TextFormatting.AQUA));
        list.add(new TranslationTextComponent("tooltip.stick.second").mergeStyle(TextFormatting.AQUA));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!player.world.isRemote)
            if (stack.hasTag()) {
                stack.getTag().remove(LibTags.savedEntity);
                stack.getTag().remove(LibTags.savedEntityName);
                player.sendMessage(new TranslationTextComponent("tooltip.stick.reset").mergeStyle(TextFormatting.RED), player.getUniqueID());
            }
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        if (player.world instanceof ServerWorld)
            if (stack.hasTag() && stack.getTag().contains(LibTags.savedEntity)) {
                MobEntity storedEntity = Utils.fromUUID((ServerWorld) player.world, stack.getTag().getString(LibTags.savedEntity));
                if (entity instanceof MobEntity && entity != storedEntity) {
                    MobEntity living = (MobEntity) entity;
                    Utils.setAttackTarget(living, storedEntity, true);
                    stack.getTag().remove(LibTags.savedEntity);
                    stack.getTag().remove(LibTags.savedEntityName);
                    return true;
                }
            } else if (entity instanceof MobEntity) {
                CompoundNBT compound = new CompoundNBT();
                if (stack.hasTag())
                    compound = stack.getTag();
                compound.putString(LibTags.savedEntity, entity.getCachedUniqueIdString());
                compound.putString(LibTags.savedEntityName, entity.getClass().getSimpleName());
                stack.setTag(compound);
                player.sendMessage(new TranslationTextComponent("tooltip.stick.add").mergeStyle(TextFormatting.GOLD), player.getUniqueID());
                return true;
            }
        return true;
    }
}
