package com.flemmli97.mobbattle.items;

import com.flemmli97.mobbattle.MobBattleTab;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.List;

public class MobEffect extends Item {

    public MobEffect() {
        super(new Item.Properties().maxStackSize(1).group(MobBattleTab.customTab));
    }

    @Override
    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        return !player.isCreative();
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> list, ITooltipFlag flagIn) {
        list.add(new TranslationTextComponent("tooltip.effect.remove").mergeStyle(TextFormatting.AQUA));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        if (!player.world.isRemote && entity instanceof LivingEntity) {
            LivingEntity e = (LivingEntity) entity;
            //Clear Potion effects
            e.clearActivePotions();
            player.sendMessage(new TranslationTextComponent("tooltip.effect.remove.clear").mergeStyle(TextFormatting.GOLD), player.getUniqueID());
        }
        return true;
    }
}
