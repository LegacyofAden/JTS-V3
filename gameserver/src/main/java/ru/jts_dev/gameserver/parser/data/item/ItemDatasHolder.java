/*
 * Copyright (c) 2015, 2016, 2017 JTS-Team authors and/or its affiliates. All rights reserved.
 *
 * This file is part of JTS-V3 Project.
 *
 * JTS-V3 Project is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JTS-V3 Project is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JTS-V3 Project.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.jts_dev.gameserver.parser.data.item;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import ru.jts_dev.gameserver.constants.ItemClass;
import ru.jts_dev.gameserver.constants.SlotBitType;
import ru.jts_dev.gameserver.parser.ItemDatasBaseListener;
import ru.jts_dev.gameserver.parser.ItemDatasLexer;
import ru.jts_dev.gameserver.parser.ItemDatasParser;
import ru.jts_dev.gameserver.parser.ItemDatasParser.ItemContext;
import ru.jts_dev.gameserver.parser.ItemDatasParser.SetContext;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Camelion
 * @since 07.01.16
 */
@Component
public class ItemDatasHolder extends ItemDatasBaseListener {
    private static final Logger log = LoggerFactory.getLogger(ItemDatasHolder.class);
    private final Map<Integer, SetData> setsData = new HashMap<>(250);
    private final Map<Integer, ItemData> itemData = new HashMap<>(18000);
    private final ApplicationContext context;

    @Autowired
    public ItemDatasHolder(ApplicationContext context) {
        this.context = context;
    }

    public final Map<Integer, SetData> getSetsData() {
        return Collections.unmodifiableMap(setsData);
    }

    public final Map<Integer, ItemData> getItemData() {
        return Collections.unmodifiableMap(itemData);
    }

    /**
     * parse itemdata set section
     * {@see ItemDatas.g4} `set` rule
     *
     * @param ctx - parsed {@link SetContext}
     */
    @Override
    public final void exitSet(final SetContext ctx) {
        final int setId = ctx.int_object().value;
        final int slotChest = ctx.slot_chest().int_object().value;
        final SetData data = new SetData(setId, slotChest);

        if (ctx.slot_legs() != null)
            data.setSlotLegs(ctx.slot_legs().int_list().value);

        if (ctx.slot_head() != null)
            data.setSlotHead(ctx.slot_head().int_list().value);

        if (ctx.slot_gloves() != null)
            data.setSlotGloves(ctx.slot_gloves().int_list().value);

        if (ctx.slot_feet() != null)
            data.setSlotFeet(ctx.slot_feet().int_list().value);

        if (ctx.slot_lhand() != null)
            data.setSlotLhand(ctx.slot_lhand().int_list().value);

        data.setSlotAdditional(ctx.slot_additional().name_object().identifier_object().getText());
        data.setSetSkill(ctx.set_skill().name_object().identifier_object().getText());
        data.setSetEffectSkill(ctx.set_effect_skill().name_object().identifier_object().getText());
        data.setSetAdditionalEffectSkill(ctx.set_additional_effect_skill().name_object().identifier_object().getText());

        if (ctx.set_additional2_condition() != null)
            data.setSetAdditional2Condition(Integer.valueOf(ctx.set_additional2_condition().int_object().getText()));

        if (ctx.set_additional2_effect_skill() != null)
            data.setSetAdditional2EffectSkill(ctx.set_additional2_effect_skill().name_object().identifier_object().getText());

        data.setStrInc(ctx.str_inc().int_list().value);
        data.setConInc(ctx.con_inc().int_list().value);
        data.setDexInc(ctx.dex_inc().int_list().value);
        data.setIntInc(ctx.int_inc().int_list().value);
        data.setMenInc(ctx.men_inc().int_list().value);
        data.setWitInc(ctx.wit_inc().int_list().value);
        setsData.put(setId, data);
    }

    /**
     * parse itemdata set section
     * {@see ItemDatas.g4} `item` rule
     *
     * @param ctx - parsed {@link ItemContext}
     */
    @Override
    public void exitItem(final ItemContext ctx) {
        long start = System.nanoTime();
        final int itemId = ctx.item_id().value;
        final ItemClass itemClass = ctx.item_class().value;
        final String name = ctx.name_object().value;
        final ItemClass itemType = ctx.item_type().value;
        final List<SlotBitType> slotBitTypes = ctx.slot_bit_type_list().value;

        final ItemData data = new ItemData(itemId, itemClass, name, itemType, slotBitTypes);
        data.setArmorType(ctx.armor_type_wrapper().value);
        data.setEtcItemType(ctx.etcitem_type_wrapper().value);
        data.setWeaponType(ctx.weapon_type_wrapper().value);

        data.setDelayShareGroup(ctx.delay_share_group().value);
        data.setItemMultiSkillList(ctx.item_multi_skill_list().value);
        data.setRecipeId(ctx.recipe_id().value);
        data.setBlessed(ctx.blessed().value);
        data.setWeight(ctx.weight().value);
        data.setDefaultAction(ctx.default_action_wrapper().value);
        data.setConsumeType(ctx.consume_type_wrapper().value);

        data.setInitialCount(ctx.initial_count().value);
        data.setSoulshotCount(ctx.soulshot_count().value);
        data.setSpiritshotCount(ctx.spiritshot_count().value);
        data.setReducedSoulshot(ctx.reduced_soulshot().value);
        data.setReducedSpiritshot(ctx.reduced_spiritshot().value);
        data.setReducedMpConsume(ctx.reduced_mp_consume().value);
        data.setImmediateEffect(ctx.immediate_effect().value);
        data.setExImmediateEffect(ctx.ex_immediate_effect().value);

        data.setDropPeriod(ctx.drop_period().value);
        data.setDuration(ctx.duration().value);
        data.setUseSkillDistime(ctx.use_skill_distime().value);
        data.setPeriod(ctx.period().value);
        data.setEquipReuseDelay(ctx.equip_reuse_delay().value);

        data.setPrice(ctx.price().value);
        data.setDefaultPrice(ctx.default_price().value);

        data.setItemSkill(ctx.item_skill().value);
        data.setCriticalAttackSkill(ctx.critical_attack_skill().value);
        data.setAttackSkill(ctx.attack_skill().value);
        data.setMagicSkill(ctx.magic_skill().value);
        data.setMagicSkillUnknownValue(ctx.magic_skill().unk);
        data.setItemSkillEnchantedFour(ctx.item_skill_enchanted_four().value);

        data.setCapsuledItems(ctx.capsuled_items().value);
        data.setMaterialType(ctx.material_type_wrapper().value);

        data.setCrystalType(ctx.crystal_type_wrapper().value);
        data.setCrystalCount(ctx.crystal_count().value);

        data.setIsTrade(ctx.is_trade().value);
        data.setIsDrop(ctx.is_drop().value);
        data.setIsDestruct(ctx.is_destruct().value);
        data.setIsPrivateStore(ctx.is_private_store().value);
        data.setKeepType(ctx.keep_type().value);

        data.setPhysicalDamage(ctx.physical_damage().value);
        data.setRandomDamage(ctx.random_damage().value);
        data.setCritical(ctx.critical().value);
        data.setHitModify(ctx.hit_modify().value);

        data.setAvoidModify(ctx.avoid_modify().value);
        data.setDualFhitRate(ctx.dual_fhit_rate().value);
        data.setShieldDefense(ctx.shield_defense().value);
        data.setShieldDefenseRate(ctx.shield_defense_rate().value);

        data.setAttackRange(ctx.attack_range().value);
        data.setDamageRange(ctx.damage_range().value);
        data.setAttackSpeed(ctx.attack_speed().value);

        data.setReuseDelay(ctx.reuse_delay().value);

        data.setMpConsume(ctx.mp_consume().value);
        data.setMagicalDamage(ctx.magical_damage().value);

        data.setDurability(ctx.durability().value);
        data.setDamaged(ctx.damaged().value);

        data.setPhysicalDefense(ctx.physical_defense().value);
        data.setMagicalDefense(ctx.magical_defense().value);

        data.setMpBonus(ctx.mp_bonus().value);

        data.setCategory(ctx.category().value);
        data.setEnchanted(ctx.enchanted().value);

        data.setBaseAttributeAttack(ctx.base_attribute_attack().value);
        data.setBaseAttributeDefend(ctx.base_attribute_defend().value);

        data.setHtml(ctx.html().value);
        data.setMagicWeapon(ctx.magic_weapon().value);
        data.setEnchantEnable(ctx.enchant_enable().value);
        data.setElementalEnable(ctx.elemental_enable().value);

        data.setUnequipSkill(ctx.unequip_skill().value);
        data.setForNpc(ctx.for_npc().value);

        data.setItemEquipOption(ctx.item_equip_option().value);

        data.setOlympiadCanUse(ctx.is_olympiad_can_use().value);
        data.setCanMove(ctx.can_move().value);
        data.setPremium(ctx.is_premium().value);

        assert !itemData.containsKey(itemId) : "Duplicate ItemId " + itemId;

        itemData.put(itemId, data);
    }

    @PostConstruct
    private void parse() throws IOException {
        log.info("Loading data file: itemdata.txt");
        final Resource file = context.getResource("scripts/itemdata.txt");
        try (InputStream is = file.getInputStream()) {
            final ANTLRInputStream input = new ANTLRInputStream(is);
            final ItemDatasLexer lexer = new ItemDatasLexer(input);
            final CommonTokenStream tokens = new CommonTokenStream(lexer);
            final ItemDatasParser parser = new ItemDatasParser(tokens);

            parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
            parser.setErrorHandler(new BailErrorStrategy());
            parser.setProfile(false);

            long start = System.nanoTime();
            final ParseTree tree = parser.file();
            log.info("ParseTime: " + (System.nanoTime() - start) / 1_000_000);


            final ParseTreeWalker walker = new ParseTreeWalker();
            start = System.nanoTime();
            walker.walk(this, tree);
            log.info("WalkTime: " + (System.nanoTime() - start) / 1_000_000);
        }
    }
}
