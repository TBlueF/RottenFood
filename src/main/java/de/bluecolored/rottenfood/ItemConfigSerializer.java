/*
 * The MIT License (MIT)
 * 
 * Copyright (c) Blue <https://www.bluecolored.de>
 * Copyright (c) CraftedNature <https://www.craftednature.de>
 * Copyright (c) contributors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.bluecolored.rottenfood;

import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextParseException;
import org.spongepowered.api.text.serializer.TextSerializers;

import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

public class ItemConfigSerializer implements TypeSerializer<ItemConfig> {

	@Override
	public ItemConfig deserialize(TypeToken<?> type, ConfigurationNode config) throws ObjectMappingException {
		
		ItemConfig.Builder builder = ItemConfig.builder();
		
		for (ConfigurationNode item : config.getNode("items").getChildrenList()){
			builder.addItem(item.getValue(TypeToken.of(ItemType.class)));
		}
		
		for (ConfigurationNode state : config.getNode("states").getChildrenList()){
			long age = state.getNode("age").getLong(-1) * 1000;
			if (age < 0) throw new ObjectMappingException("Config-value 'age' is missing or below zero! (must be at or above 0)");
			
			String sName = state.getNode("name").getString(null);
			Text name = null;
			if (sName != null){
				try {
					name = TextSerializers.JSON.deserialize(sName);
				} catch (TextParseException ex){
					name = TextSerializers.FORMATTING_CODE.deserializeUnchecked(sName);
				}
			}
			
			String sLore = state.getNode("lore").getString(null);
			Text lore = null;
			if (sLore != null){
				try {
					lore = TextSerializers.JSON.deserialize(sLore);
				} catch (TextParseException ex){
					lore = TextSerializers.FORMATTING_CODE.deserializeUnchecked(sLore);
				}
			}
			
			ItemType replacement = state.getNode("replacement-item").getValue(TypeToken.of(ItemType.class));
			
			builder.addAgeState(new ItemAgeStateConfig(age, name, lore, replacement));
		}

		for (ConfigurationNode mod : config.getNode("ageing-modifier").getChildrenList()){
			double multiplier = mod.getNode("multiplier").getDouble(1);
			
			ItemType item = mod.getNode("item").getValue(TypeToken.of(ItemType.class));
			if (item == null) throw new ObjectMappingException("Config-value 'item' in 'ageing-modifier' is missing!");
			
			int minItems = mod.getNode("min-item-count").getInt(1);
			if (minItems < 0) throw new ObjectMappingException("Config-value 'min-item-count' is at or below zero! (must to be above 0)");
			
			builder.addAgingModifier(new ItemAgeingModifierConfig(multiplier, item, minItems));
		}
		
		builder.stackingInterval(config.getNode("item-stacking-intervall").getLong(60) * 1000);
		
		builder.showAge(config.getNode("show-age").getBoolean(false));
		
		return builder.build();
	}

	@Override
	public void serialize(TypeToken<?> type, ItemConfig obj, ConfigurationNode value) throws ObjectMappingException {
		throw new UnsupportedOperationException("Serializing is not supported for ItemConfigs!");
	}
	
}
