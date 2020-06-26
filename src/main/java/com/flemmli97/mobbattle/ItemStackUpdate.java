package com.flemmli97.mobbattle;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ItemStackUpdate implements IMessage {

    public NBTTagCompound compound;

    public ItemStackUpdate() {
    }

    public ItemStackUpdate(NBTTagCompound compound) {
        this.compound = compound;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.compound = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, this.compound);
    }

    public static class Handler implements IMessageHandler<ItemStackUpdate, IMessage> {

        @Override
        public IMessage onMessage(ItemStackUpdate msg, MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().player;
            if(player != null && player.getHeldItemMainhand().getItem() == ModItems.mobEffectGiver){
                player.getHeldItemMainhand().setTagCompound(msg.compound);
            }
            return null;
        }
    }
}