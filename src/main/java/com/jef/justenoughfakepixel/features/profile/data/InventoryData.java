package com.jef.justenoughfakepixel.features.profile.data;

import com.jef.justenoughfakepixel.features.profile.vars.EquipmentSlot;
import lombok.AllArgsConstructor;

import java.util.HashMap;

@AllArgsConstructor
public class InventoryData {

    public HashMap<EquipmentSlot,String> armorData;
    public HashMap<Integer,String> invData;

}
