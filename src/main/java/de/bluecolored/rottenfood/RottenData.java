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

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.Value;

public class RottenData extends AbstractData<RottenData, ImmutableRottenData> {

	private long lastUpdate;
	private long age;
	
	public RottenData() {
		this(-1, 0);
	}
	
	public RottenData(long lastUpdate, long age){
		this.lastUpdate = lastUpdate;
		this.age = age;
	}
	
	public Value<Long> lastUpdate(){		
		return Sponge.getRegistry().getValueFactory()
			.createValue(RottenDataBuilder.UPDATE_TIME, this.lastUpdate, -1L);
	}
	
	public Value<Long> age(){		
		return Sponge.getRegistry().getValueFactory()
			.createValue(RottenDataBuilder.AGE, this.age, 0L);
	}
	
	public long getLastUpdate() {
		return lastUpdate;
	}

	public long getAge() {
		return age;
	}

	@Override
	public Optional<RottenData> fill(DataHolder dataHolder, MergeFunction overlap) {
		throw new UnsupportedOperationException("Filling is not supported by this DataType!");
	}

	@Override
	public Optional<RottenData> from(DataContainer container) {		
		if (!container.contains(RottenDataBuilder.UPDATE_TIME.getQuery(), RottenDataBuilder.AGE.getQuery())) {
            return Optional.empty();
		}
		
		this.lastUpdate = container.getLong(RottenDataBuilder.UPDATE_TIME.getQuery()).get();
		this.age = container.getLong(RottenDataBuilder.AGE.getQuery()).get();
		
		return Optional.of(this);
	}

	@Override
	public RottenData copy() {
		return new RottenData(lastUpdate, age);
	}

	@Override
	public ImmutableRottenData asImmutable() {
		return new ImmutableRottenData(lastUpdate, age);
	}

	@Override
	public int getContentVersion() {
		return 1;
	}

	@Override
	public DataContainer toContainer() {
		DataContainer c = super.toContainer();
		
		c.set(RottenDataBuilder.UPDATE_TIME, this.lastUpdate);
		c.set(RottenDataBuilder.AGE, this.age);
		
		return c;
	}
	
	@Override
	protected void registerGettersAndSetters() {
		registerFieldGetter(RottenDataBuilder.UPDATE_TIME, this::getLastUpdate);
		registerKeyValue(RottenDataBuilder.UPDATE_TIME, this::lastUpdate);
		registerFieldSetter(RottenDataBuilder.UPDATE_TIME, f -> lastUpdate = f);
		
		registerFieldGetter(RottenDataBuilder.AGE, this::getAge);
		registerKeyValue(RottenDataBuilder.AGE, this::age);
		registerFieldSetter(RottenDataBuilder.AGE, f -> age = f);
	}
	
}
