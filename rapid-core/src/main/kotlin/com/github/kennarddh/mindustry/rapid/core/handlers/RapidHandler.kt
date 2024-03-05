package com.github.kennarddh.mindustry.rapid.core.handlers

import com.github.kennarddh.mindustry.genesis.core.commons.runOnMindustryThread
import com.github.kennarddh.mindustry.genesis.core.handlers.Handler
import com.github.kennarddh.mindustry.genesis.core.timers.annotations.TimerTask
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.gen.Building
import mindustry.gen.Groups
import mindustry.type.Item
import mindustry.type.Liquid
import mindustry.world.blocks.power.PowerGenerator.GeneratorBuild
import mindustry.world.blocks.production.GenericCrafter.GenericCrafterBuild
import mindustry.world.consumers.*

class RapidHandler : Handler {
    val allItems = Items.serpuloItems.add(Items.erekirItems).toSet()
    val allLiquids = setOf(
        Liquids.water,
        Liquids.slag,
        Liquids.oil,
        Liquids.cryofluid,
        Liquids.arkycite,
        Liquids.gallium,
        Liquids.neoplasm,
        Liquids.ozone,
        Liquids.hydrogen,
        Liquids.nitrogen,
        Liquids.cyanogen
    )

    fun updateBuilding(building: Building) {
        val items = mutableListOf<Item>()
        val liquids = mutableListOf<Liquid>()

        building.block.consumers.forEach {
            when (it) {
                is ConsumeItems -> {
                    items.addAll(it.items.map { it.item })
                }

                is ConsumeItemFilter -> {
                    val validItems = allItems.filter { item -> it.filter.get(item) }

                    validItems.forEach { item ->
                        items.add(item)
                    }
                }

                is ConsumeLiquidFilter -> {
                    val validLiquids = allLiquids.filter { liquid -> it.filter.get(liquid) }

                    validLiquids.forEach { liquid ->
                        liquids.add(liquid)
                    }
                }

                is ConsumeLiquid -> {
                    liquids.add(it.liquid)
                }

                is ConsumeLiquids -> {
                    liquids.addAll(it.liquids.map { it.liquid })
                }
            }
        }

        items.forEach {
            building.items.set(it, Int.MAX_VALUE)
        }

        liquids.forEach {
            building.liquids.set(it, Float.MAX_VALUE)
        }
    }

    //    @EventHandler
//    @EventHandlerTrigger(Trigger.update)
    @TimerTask(1f, 1f)
    fun onUpdate() {
        runOnMindustryThread {
            Groups.build.each { building ->
                if (building is GenericCrafterBuild || building is GeneratorBuild) {
                    updateBuilding(building)
                }
            }
        }
    }
}