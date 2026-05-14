package com.jef.justenoughfakepixel.events

import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.inventory.Slot
import net.minecraftforge.fml.common.eventhandler.Cancelable
import net.minecraftforge.fml.common.eventhandler.Event

/**
 * Fired just before a slot click is forwarded to PlayerControllerMP#windowClick.
 * Cancel to prevent the click from being processed.
 *
 * clickType == 4  →  drop key (Q) pressed while hovering a slot in inventory
 */
@Cancelable
class SlotClickEvent(
    val gui: GuiContainer,
    val slot: Slot?,
    val slotId: Int,
    val clickedButton: Int,
    /** 0=click, 1=shift-click, 2=hotbar-swap, 4=drop-key, 5=drag, 6=double-click */
    val clickType: Int
) : Event()
