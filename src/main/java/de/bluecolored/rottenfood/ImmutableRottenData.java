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

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

public class ImmutableRottenData extends AbstractImmutableData<ImmutableRottenData, RottenData> {

	private long lastUpdate;
	private long age;
	
	public ImmutableRottenData() {
		this(-1, 0);
	}
	
	public ImmutableRottenData(long lastUpdate, long age){
		this.lastUpdate = lastUpdate;
		this.age = age;
	}
	
	public ImmutableValue<Long> lastUpdate(){
		return Sponge.getRegistry().getValueFactory()
			.createValue(RottenData.UPDATE_TIME, this.lastUpdate, -1L).asImmutable();
	}
	
	public ImmutableValue<Long> age(){
		return Sponge.getRegistry().getValueFactory()
			.createValue(RottenData.AGE, this.age, 0L).asImmutable();
	}
	
	@Override
	public RottenData asMutable() {
		return new RottenData(lastUpdate, age);
	}

	@Override
	public int getContentVersion() {
		return 1;
	}

	@Override
	public DataContainer toContainer() {
		DataContainer c = super.toContainer();
		
		c.set(RottenData.UPDATE_TIME, this.lastUpdate);
		c.set(RottenData.AGE, this.age);
		
		return c;
	}
	
	@Override
	protected void registerGetters() {
		registerFieldGetter(RottenData.UPDATE_TIME, () -> lastUpdate);
		registerKeyValue(RottenData.UPDATE_TIME, this::lastUpdate);
		
		registerFieldGetter(RottenData.AGE, () -> age);
		registerKeyValue(RottenData.AGE, this::age);
	}
	
}
