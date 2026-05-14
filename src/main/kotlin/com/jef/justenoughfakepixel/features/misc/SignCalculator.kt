package com.jef.justenoughfakepixel.features.misc

import com.jef.justenoughfakepixel.core.JefConfig
import com.jef.justenoughfakepixel.events.SignSubmitEvent
import com.jef.justenoughfakepixel.init.RegisterEvents
import com.jef.justenoughfakepixel.mixins.accessors.GuiEditSignAccessor
import com.jef.justenoughfakepixel.utils.CalculatorUtils
import com.jef.justenoughfakepixel.utils.Utils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiEditSign
import net.minecraft.util.EnumChatFormatting.*
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.math.BigDecimal

@RegisterEvents
class SignCalculator {

    private var lastSource: String? = null
    private var lastResult: BigDecimal? = null
    private var lastError: String? = null

    @SubscribeEvent
    fun onSignDrawn(event: GuiScreenEvent.DrawScreenEvent.Post) {
        if (!JefConfig.feature.misc.signCalculator) return

        val gui = event.gui as? GuiEditSign ?: return
        val sign = (gui as GuiEditSignAccessor).`jef$getTileSign`()

        val trigger = sign.signText[1].unformattedText
        if (trigger != "^^^^^^^^^^^^^^^" && trigger != "^^^^^^") return

        val source = sign.signText[0].unformattedText
        refresh(source)

        val mc = Minecraft.getMinecraft()
        val result = lastResult
        val rendered = when {
            result != null -> {
                val formatted = CalculatorUtils.FORMAT.format(result)
                if (mc.fontRendererObj.getStringWidth(formatted) > 90) {
                    "$WHITE$lastSource $YELLOW= ${RED}Result too long"
                } else {
                    "$WHITE$lastSource $YELLOW= $GREEN$formatted"
                }
            }

            lastError != null -> "$RED$lastError"
            else -> "${RED}No calculation"
        }

        Utils.drawStringCentered(rendered, mc.fontRendererObj, gui.width / 2f, 58f, false, 0x808080FF.toInt())
    }

    @SubscribeEvent
    fun onSignSubmit(event: SignSubmitEvent) {
        if (!JefConfig.feature.misc.signCalculator) return

        val trigger = event.lines[1]
        if (trigger != "^^^^^^^^^^^^^^^" && trigger != "^^^^^^") return

        refresh(event.lines[0])

        val result = lastResult
        if (result != null) {
            event.lines[0] = result.toPlainString()
        }
    }

    private fun refresh(source: String) {
        if (source == lastSource) return
        lastSource = source

        if (source.isEmpty()) {
            lastResult = null
            lastError = null
            return
        }

        try {
            lastResult = CalculatorUtils.calculate(source)
            lastError = null
        } catch (e: CalculatorUtils.CalculatorException) {
            lastError = e.message
            lastResult = null
        }
    }
}
