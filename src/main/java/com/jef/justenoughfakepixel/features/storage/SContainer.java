package com.jef.justenoughfakepixel.features.storage;

import com.jef.justenoughfakepixel.JefMod;
import com.jef.justenoughfakepixel.utils.render.ResolutionUtils;
import lombok.AllArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;

@AllArgsConstructor
public class SContainer {

    public HashMap<Integer,String> slots;
    public String id;
    public int page;
    public Type type;
    public boolean locked;
    public int renderW;
    public int renderH;
    public int slotCount;
    public boolean empty = false;

    public SContainer(HashMap<Integer, ItemStack> slots,int page,Type type,int renderH,boolean locked){
        this.id = type.prefix + "-" + page;
        this.page = page;
        this.type = type;
        this.slots = getSlots(slots);
        this.renderH = (int)(ResolutionUtils.getYStatic(renderH));
        this.renderW = (int)(ResolutionUtils.getXStatic(307));
        slotCount = getSlotCount(renderH);
        JefMod.logger.info(renderW + " | " + slotCount);
        this.locked = locked;
    }

    public HashMap<Integer,String> getSlots(HashMap<Integer, ItemStack> slots){
        HashMap<Integer,String> items = new HashMap<>();

        slots.keySet().forEach(key -> {
            ItemStack stack = slots.get(key);
            try {
                NBTTagCompound compound = new NBTTagCompound();
                stack.writeToNBT(compound);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                CompressedStreamTools.writeCompressed(compound, outputStream);
                items.put(key, Base64.getEncoder().encodeToString(outputStream.toByteArray()));
            } catch (IOException e) {
                JefMod.logger.info("Error While trying to convert " + stack.getDisplayName() + " to String");
                return;
            }
        });
        return items;
    }

    public ItemStack getStack(Integer key){
        String encodedItem = slots.get(key);
        if(encodedItem == null || encodedItem.isEmpty()) return null;
        try {
            byte[] bytes = Base64.getDecoder().decode(encodedItem);
            NBTTagCompound nbt = CompressedStreamTools.readCompressed(new ByteArrayInputStream(bytes));

            return ItemStack.loadItemStackFromNBT(nbt);
        } catch (Exception e) {
            JefMod.logger.info("Error While trying to convert " + encodedItem + " to Itemstack");
            return null;
        }
    }

    public void draw(int mouseX, int mouseY, Minecraft mc){

    }

    private int getSlotCount(int renderH){
        switch (renderH){
            case 70 : return 9;
            case 100: return 18;
            case 140: return 27;
            case 170: return 36;
            default : return 45;
        }
    }

}
