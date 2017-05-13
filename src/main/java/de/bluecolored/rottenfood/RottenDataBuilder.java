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

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.DataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

public class RottenDataBuilder implements DataManipulatorBuilder<RottenData, ImmutableRottenData> {

	private long lastUpdate = -1;
	private long age = 0;
	
	@Override
	public Optional<RottenData> build(DataView container) throws InvalidDataException {
		if (!container.contains(RottenData.UPDATE_TIME.getQuery(), RottenData.AGE.getQuery())) {
            return Optional.empty();
		}
		
		long lastUpdate = container.getLong(RottenData.UPDATE_TIME.getQuery()).get();
		long age = container.getLong(RottenData.AGE.getQuery()).get();
		
		return Optional.of(new RottenData(lastUpdate, age));
	}

	@Override
	public DataBuilder<RottenData> from(RottenData value) {
		lastUpdate = value.getLastUpdate();
		age = value.getAge();
		return this;
	}

	@Override
	public DataBuilder<RottenData> reset() {
		lastUpdate = -1;
		age = 0;
		return this;
	}

	@Override
	public RottenData create() {
		return new RottenData(lastUpdate, age);
	}

	@Override
	public Optional<RottenData> createFrom(DataHolder dataHolder) {
		return Optional.of(dataHolder.get(RottenData.class).orElse(new RottenData()));
	}
	
}
