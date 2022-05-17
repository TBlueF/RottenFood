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

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextParseException;
import org.spongepowered.api.text.serializer.TextSerializers;

public class ExtendedItemTypeSerializer implements TypeSerializer<ExtendedItemType> {

	@Override
	public ExtendedItemType deserialize(TypeToken<?> type, ConfigurationNode config) throws ObjectMappingException {
		String itemDesc = config.getString();
		if (itemDesc == null) return null;

		String itemTypeString;
		String unsafeDamageString;

		int n = itemDesc.indexOf(':');
		int i = itemDesc.lastIndexOf(':');
		if (i == n) {
			itemTypeString = itemDesc;
			unsafeDamageString = null;
		} else {
			itemTypeString = itemDesc.substring(0, i);
			unsafeDamageString = itemDesc.substring(i + 1);
		}

		ItemType it = Sponge.getRegistry()
				.getType(ItemType.class, itemTypeString)
				.orElseThrow(() -> new ObjectMappingException("Failed to read item! (No such ItemType: '" + itemTypeString + "')"));

		if (unsafeDamageString != null) {
			try {
				int unsafeDamage = Integer.parseInt(unsafeDamageString);
				return new ExtendedItemType(it, unsafeDamage);
			} catch (NumberFormatException nfe) {
				throw new ObjectMappingException("Failed to read item! (could not parse unsafe damage)", nfe);
			}
		}

		return new ExtendedItemType(it);
	}

	@Override
	public void serialize(TypeToken<?> type, ExtendedItemType obj, ConfigurationNode value) throws ObjectMappingException {
		throw new UnsupportedOperationException("Serializing is not supported for ExtendedItemType!");
	}
	
}
