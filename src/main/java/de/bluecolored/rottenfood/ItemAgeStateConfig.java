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

public class ItemAgeStateConfig {

	private long age;
	private ItemType replacement;
	private Text name;
	private Text lore;
	
	public ItemAgeStateConfig(long age) {
		this.age = age;
		this.replacement = null;
		this.name = null;
		this.lore = null;
	}
	
	public ItemAgeStateConfig(long age, Text name, Text lore) {
		this.age = age;
		this.replacement = null;
		this.lore = lore;
		this.name = name;
	}

	public ItemAgeStateConfig(long age, ItemType replacement) {
		this.age = age;
		this.replacement = replacement;
		this.name = null;
		this.lore = null;
	}
	
	public ItemAgeStateConfig(long age, Text name, Text lore, ItemType replacement) {
		this.age = age;
		this.replacement = replacement;
		this.lore = lore;
		this.name = name;
	}

	public long getAge() {
		return age;
	}
	
	public Text getLore() {
		return lore;
	}
	
	public Text getName() {
		return name;
	}
	
	public ItemType getReplacement() {
		return replacement;
	}
	
}
