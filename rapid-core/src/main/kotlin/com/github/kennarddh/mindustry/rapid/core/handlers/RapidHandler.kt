package com.github.kennarddh.mindustry.rapid.core.handlers

import com.github.kennarddh.mindustry.genesis.core.commons.runOnMindustryThread
import com.github.kennarddh.mindustry.genesis.core.handlers.Handler
import com.github.kennarddh.mindustry.genesis.core.timers.annotations.TimerTask
import com.github.kennarddh.mindustry.rapid.core.commons.Logger
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.gen.Groups
import mindustry.type.ItemStack
import mindustry.type.LiquidStack
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

    //    @EventHandler
//    @EventHandlerTrigger(Trigger.update)
    @TimerTask(1f, 1f)
    fun onUpdate() {
        runOnMindustryThread {
            Groups.build.each { building ->
                Logger.info(building::class.simpleName)

                if (building is GenericCrafterBuild || building is GeneratorBuild) {
                    val items = mutableListOf<ItemStack>()
                    val liquids = mutableListOf<LiquidStack>()

                    Logger.info(building.block.consumers.contentToString())

                    building.block.consumers.forEach {
                        when (it) {
                            is ConsumeItems -> {
                                items.addAll(it.items)
                            }

                            is ConsumeItemFilter -> {
                                val validItems = allItems.filter { item -> it.filter.get(item) }

                                validItems.forEach { item ->
                                    items.add(ItemStack(item, 100))
                                }
                            }

                            is ConsumeLiquidFilter -> {
                                val validLiquids = allLiquids.filter { liquid -> it.filter.get(liquid) }

                                validLiquids.forEach { liquid ->
                                    liquids.add(LiquidStack(liquid, 100f))
                                }
                            }

                            is ConsumeLiquid -> {
                                liquids.add(LiquidStack(it.liquid, it.amount))
                            }

                            is ConsumeLiquids -> {
                                liquids.addAll(it.liquids)
                            }
                        }
                    }

                    Logger.info(items.toString())
                    Logger.info(liquids.toString())

                    items.forEach {
                        building.items.set(it.item, building.block.itemCapacity * 10)
                    }

                    liquids.forEach {
                        building.liquids.set(it.liquid, building.block.liquidCapacity * 10)
                    }
                }
            }
        }
    }
}