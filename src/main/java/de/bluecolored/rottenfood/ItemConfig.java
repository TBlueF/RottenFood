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

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class ItemConfig {
	public static final TypeToken<ItemConfig> TOKEN = TypeToken.of(ItemConfig.class);
	
	private long stackingInterval;
	private boolean showAge;
	
	private List<ExtendedItemType> items;
	private List<ItemAgeStateConfig> ageStates;
	private List<ItemAgeingModifierConfig> ageingModifiers;
	
	private ItemConfig(List<ExtendedItemType> items, List<ItemAgeStateConfig> ageStates, List<ItemAgeingModifierConfig> agingModifiers, long stackingInterval, boolean showAge){
		this.items = Collections.unmodifiableList(items);

		ageStates.sort((as1, as2) -> {
			return (int) Math.signum((double) as1.getAge() - (double) as2.getAge());
		});
		
		this.ageStates = Collections.unmodifiableList(ageStates);
		this.ageingModifiers = Collections.unmodifiableList(agingModifiers);
		
		this.stackingInterval = stackingInterval;
		this.showAge = showAge;
	}

	public boolean matchesItemStack(ItemStack is) {
		boolean anyFound = false;
		for (ExtendedItemType eit : items) {
			if (eit.matches(is)) {
				anyFound = true;
				break;
			}
		}

		return anyFound;
	}

	public long getStackingInterval(){
		return stackingInterval;
	}
	
	public boolean isShowingAge(){
		return showAge;
	}
	
	public List<ExtendedItemType> getItems(){
		return items;
	}
	
	public List<ItemAgeStateConfig> getAgeStates() {
		return ageStates;
	}
	
	public List<ItemAgeingModifierConfig> getAgingModifiers(){
		return ageingModifiers;
	}
	
	public static Builder builder(){
		return new Builder();
	}
	
	static class Builder {

		private long stackingInterval;
		private boolean showAge;
		
		private List<ExtendedItemType> items;
		private List<ItemAgeStateConfig> ageStates;
		private List<ItemAgeingModifierConfig> ageingModifiers;
		
		public Builder() {
			stackingInterval = 60 * 1000;
			items = Lists.newArrayList();
			ageStates = Lists.newArrayList();
			ageingModifiers = Lists.newArrayList();
		}
		
		public Builder addItem(ExtendedItemType item){
			items.add(item);
			return this;
		}
		
		public Builder addAgeState(ItemAgeStateConfig ageState){
			ageStates.add(ageState);
			return this;
		}
		
		public Builder addAgingModifier(ItemAgeingModifierConfig mod){
			ageingModifiers.add(mod);
			return this;
		}
		
		public Builder stackingInterval(long stackingInterval){
			this.stackingInterval = stackingInterval;
			return this;
		}
		
		public Builder showAge(boolean show){
			showAge = show;
			return this;
		}
		
		public ItemConfig build(){
			return new ItemConfig(items, ageStates, ageingModifiers, stackingInterval, showAge);
		}
		
	}
	
}
