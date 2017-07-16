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

import org.spongepowered.api.item.inventory.ItemStack;

public class ItemAgeingModifierConfig {

	private double ageMultiplier;
	private ItemStack item;
	private int minItemCount;

	public ItemAgeingModifierConfig(double ageMultiplyer, ItemStack item) {
		this.ageMultiplier = ageMultiplyer;
		this.item = item;
		minItemCount = 1;
	}
	
	public ItemAgeingModifierConfig(double ageMultiplier, ItemStack item, int minItemCount) {
		this(ageMultiplier, item);
		this.minItemCount = minItemCount;
	}
	
	public double getAgeMultiplier(){
		return ageMultiplier;
	}
	
	public ItemStack getItem(){
		return item;
	}
	
	public int getMinItemCount(){
		return minItemCount;
	}
	
}
