package com.github.kennarddh.mindustry.rapid.core.handlers

import com.github.kennarddh.mindustry.genesis.core.commons.runOnMindustryThread
import com.github.kennarddh.mindustry.genesis.core.events.annotations.EventHandler
import com.github.kennarddh.mindustry.genesis.core.events.annotations.EventHandlerTrigger
import com.github.kennarddh.mindustry.genesis.core.handlers.Handler
import com.github.kennarddh.mindustry.rapid.core.commons.BlockConsumersCache
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.game.EventType
import mindustry.gen.Building
import mindustry.gen.Groups
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.type.Liquid
import mindustry.type.LiquidStack
import mindustry.world.Block
import mindustry.world.blocks.power.NuclearReactor
import mindustry.world.blocks.power.PowerGenerator.GeneratorBuild
import mindustry.world.blocks.production.GenericCrafter.GenericCrafterBuild
import mindustry.world.consumers.*
import java.util.concurrent.ConcurrentHashMap

class RapidHandler : Handler {
    private val allItems = Items.serpuloItems.add(Items.erekirItems).toSet()
    private val allLiquids = setOf(
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

    private val blockConsumersCaches = ConcurrentHashMap<Block, BlockConsumersCache>()

    private fun buildCache(block: Block): BlockConsumersCache {
        val items = mutableListOf<Item>()
        val liquids = mutableListOf<Liquid>()

        block.consumers.forEach { consumer ->
            when (consumer) {
                is ConsumeItems -> {
                    items.addAll(consumer.items.map { it.item })
                }

                is ConsumeItemFilter -> {
                    val validItems = allItems.filter { item -> consumer.filter.get(item) }

                    validItems.forEach { item ->
                        items.add(item)
                    }
                }

                is ConsumeLiquidFilter -> {
                    val validLiquids = allLiquids.filter { liquid -> consumer.filter.get(liquid) }

                    validLiquids.forEach { liquid ->
                        liquids.add(liquid)
                    }
                }

                is ConsumeLiquid -> {
                    liquids.add(consumer.liquid)
                }

                is ConsumeLiquids -> {
                    liquids.addAll(consumer.liquids.map { it.liquid })
                }
            }
        }

        val itemsCache: MutableList<ItemStack> = mutableListOf()
        val liquidsCache: MutableList<LiquidStack> = mutableListOf()

        items.forEach {
            if (block is NuclearReactor) {
                itemsCache.add(ItemStack(it, block.itemCapacity))
            } else {
                itemsCache.add(ItemStack(it, Int.MAX_VALUE))
            }
        }

        liquids.forEach {
            liquidsCache.add(LiquidStack(it, Float.MAX_VALUE))
        }

        val cache = BlockConsumersCache(itemsCache, liquidsCache)

        blockConsumersCaches[block] = cache

        return cache
    }

    private fun updateBuilding(building: Building) {
        val cachedConsumers = blockConsumersCaches.computeIfAbsent(building.block) { buildCache(building.block) }

        cachedConsumers.items.forEach {
            building.items.set(it.item, it.amount)
        }

        cachedConsumers.liquids.forEach {
            building.liquids.set(it.liquid, it.amount)
        }
    }

    @EventHandler
    fun onBlockBuildEndEvent(event: EventType.BlockBuildEndEvent) {
        if (event.breaking) return

        if (event.tile.build is GenericCrafterBuild || event.tile.build is GeneratorBuild) {
            runOnMindustryThread {
                updateBuilding(event.tile.build)
            }
        }
    }

    @EventHandler
    @EventHandlerTrigger(EventType.Trigger.update)
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