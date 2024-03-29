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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextParseException;
import org.spongepowered.api.text.serializer.TextSerializers;

import com.google.common.collect.Lists;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;

@Plugin(
	id = RottenFood.PLUGIN_ID,
	name = "RottenFood",
	version = "1.3 (API 7.4)",
	description = "This plugin gives items an expirationdate",
	url = "http://www.bluecolored.de/rottenfood/",
	authors = {"Blue (TBlueF, Lukas Rieger)", "Chaaya", "BlueColored", "craftednature"}
	)
public class RottenFood {
	public static final String PLUGIN_ID = "rottenfood";
	
	@Inject private Logger logger;
	
	private List<ItemConfig> itemConfigs;
	
	private long updateIntervall = 20;
	private Task updateTask;
	
	private String ageFormat;
	
	@Listener
    public void onPreInit(GamePreInitializationEvent evt) {
		logger.info("Pre-Initializing...");
		
		DataRegistration.builder()
			.dataClass(RottenData.class)
			.immutableClass(ImmutableRottenData.class)
			.builder(new RottenDataBuilder())
			.id(PLUGIN_ID + "_rottendata")
			.name("Rotten Food")
			.build();

		TypeSerializers.getDefaultSerializers().registerType(ItemConfig.TOKEN, new ItemConfigSerializer());
		TypeSerializers.getDefaultSerializers().registerType(ExtendedItemType.TOKEN, new ExtendedItemTypeSerializer());
	}
	
	@Listener
	public void onInit(GameInitializationEvent evt){
		logger.info("Initializing...");
		
		itemConfigs = Lists.newArrayList();
		
		try {
			load();
		} catch (IOException | ObjectMappingException e) {
			logger.error("Failed to load or create config-file!", e);
		}

	}
	
	@Listener
	public void onStart(GameStartingServerEvent evt){
		logger.info("Starting...");
		startUpdateTask();
	}
	
	public void startUpdateTask(){
		if (updateTask != null) updateTask.cancel();
		updateTask = Sponge.getScheduler().createTaskBuilder()
		.intervalTicks(updateIntervall)
		.execute(() -> updatePlayerInventories())
		.submit(this);
	}
	
	public void updatePlayerInventories(){
		for (Player p : Sponge.getServer().getOnlinePlayers()){
			updateInventory(p.getInventory(), false);
		}
	}
	
	@Listener
	public void onReload(GameReloadEvent evt){
		logger.info("Reloading...");
		
		try {
			load();
		} catch (IOException | ObjectMappingException e) {
			logger.error("Failed to load or create config-file!", e);
			return;
		}
		
		startUpdateTask();
	}
	
	@Listener
	public void onOpenInventory(InteractInventoryEvent.Open evt){
		scheduleInventoryUpdate(evt.getTargetInventory(), true);
	}
	
	public void scheduleInventoryUpdate(Inventory inv, boolean forceUpdate){
		Sponge.getScheduler().createTaskBuilder()
		.delayTicks(0 )
		.execute(() -> updateInventory(inv, forceUpdate))
		.submit(this);
	}
	
	public void updateInventory(Inventory inv, boolean forceUpdate){
		if (inv instanceof Slot) {
			updateItem((Slot) inv, inv, forceUpdate);
		} else if (inv instanceof GridInventory){
			inv.slots().forEach(i -> {
				if (i instanceof Slot){
					updateItem((Slot) i, inv, forceUpdate); 
				}
			});
		} else {
			for (Inventory child : inv){
				updateInventory(child, forceUpdate);
			}
		}
	}
	
	public void updateItem(Slot slot, Inventory modifierScope, boolean forceUpdate){		
		Optional<ItemStack> ois = slot.peek();
		
		if (!ois.isPresent()) return;
		
		ItemStack is = ois.get();
		ItemStack old = is.copy();
		
		for (ItemConfig ic : itemConfigs){
			if (!ic.matchesItemStack(is)) continue;
			
			RottenData data = is.get(RottenData.class).orElse(new RottenData());
			long lastUpdate = data.getLastUpdate(); 
			long now = (long) Math.floor((double) System.currentTimeMillis() / (double) ic.getStackingInterval()) * ic.getStackingInterval();
			
			long delta = now - lastUpdate;
			
			double modifier = 1;
			for (ItemAgeingModifierConfig mod : ic.getAgingModifiers()){
				ExtendedItemType type = mod.getItem();
				int count = mod.getMinItemCount();
				
				if (count > countItems(modifierScope, type)) continue;
				
				modifier *= mod.getAgeMultiplier();
			}
			
			long modDelta = (long)((double) delta * modifier);
			modDelta = (long) Math.floor((double) modDelta / (double) ic.getStackingInterval()) * ic.getStackingInterval();
			
			long newAge = data.getAge() + modDelta;
			
			if (lastUpdate == -1){				
				newAge = 0;
				delta = 0;
			} 

			is.offer(new RottenData(now, newAge));
			
			Text name = null;
			Text lore = null;
			ItemStack newIs = is;
			
			for (ItemAgeStateConfig as : ic.getAgeStates()){
				if (as.getAge() > newAge) break;

				if (as.getName() != null) name = as.getName();
				if (as.getLore() != null) lore = as.getLore();
				
				if (as.getReplacement() != null){
					ExtendedItemType repl = as.getReplacement();
					ItemType replIt = repl.getItemType();

					if (replIt == ItemTypes.NONE || replIt == ItemTypes.AIR){
						newIs = ItemStack.empty();
					} else {
						newIs = ItemStack.of(replIt, is.getQuantity());

						if (repl.isUseUnsafeDamage()) {
							try {
								DataContainer container = newIs.toContainer();
								container.set(DataQuery.of("UnsafeDamage"), repl.getUnsafeDamage());
								newIs = ItemStack.builder().fromContainer(container).build();
							} catch (InvalidDataException e){
								logger.error("Failed to apply unsafe-damage to item! " +
										"(ItemStack: " + newIs + ", Damage: " + repl.getUnsafeDamage() + ")", e);
							}
						}
					}
					
					break;
				} else {
					newIs = is;
				}
			}
			
			List<Text> lorelist = Lists.newArrayList();
			if (lore != null) lorelist.add(lore);
			if (ic.isShowingAge()){
				String ageString = formatTimeInterval(newAge, ageFormat);
				Text ageText;
				try {
					ageText = TextSerializers.JSON.deserialize(ageString);
				} catch (TextParseException ex) {
					ageText = TextSerializers.FORMATTING_CODE.deserializeUnchecked(ageString);
				}
				
				lorelist.add(ageText);
			}
			
			if (name != null && !newIs.get(Keys.DISPLAY_NAME).orElse(Text.EMPTY).equals(name)) newIs.offer(Keys.DISPLAY_NAME, name);
			if (!newIs.get(Keys.ITEM_LORE).orElse(Lists.newArrayList()).equals(lorelist)) newIs.offer(Keys.ITEM_LORE, lorelist);

			if (!newIs.equalTo(old) || forceUpdate){
				slot.set(newIs);
			}
			
			return;
		}
	}
	
	public int countItems(Inventory i, ExtendedItemType type){
		int count = 0;
		
		for (Inventory isl : i.query(QueryOperationTypes.ITEM_TYPE.of(type.getItemType())).slots()){
			Slot sl = (Slot) isl;
			ItemStack is = sl.peek().orElse(null);
			if (is != null && type.matches(is)){
				count += is.getQuantity();
			}
		}
		
		return count;
	}
	
	public void load() throws IOException, ObjectMappingException {
		List<ItemConfig> configs = Lists.newArrayList();
		
		Path path = Sponge.getConfigManager().getSharedConfig(this).getConfigPath();
		
		File file = path.toFile();
		if (!file.exists()){
			logger.warn("No config file found! Generating new config..");
			logger.info("You can use '/sponge plugins reload' to reload the config file after you edited it!");
			file.getParentFile().mkdirs();
			file.createNewFile();
			URL defaultConfig = this.getClass().getResource("defaultConfig.conf");
			copyUrlToFile(defaultConfig, file);
		}
		
		ConfigurationOptions opts = ConfigurationOptions.defaults();
		ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setDefaultOptions(opts).setFile(file).build();
		CommentedConfigurationNode config = loader.load();
		
		for (ConfigurationNode iconf : config.getNode("item-configs").getChildrenList()){
			configs.add(iconf.getValue(ItemConfig.TOKEN));
		}

		this.updateIntervall = config.getNode("update-intervall").getLong(20);
		this.ageFormat = config.getNode("age-time-format").getString("&7This item is &f%D &7days old.");
		
		this.itemConfigs.clear();
		this.itemConfigs = configs;
		logger.info("Config loaded successfully!");
	}
	
	private static void copyUrlToFile(URL from, File to) throws IOException {
		try (
			FileOutputStream fos = new FileOutputStream(to);
			InputStream input = from.openStream();
		){
			byte[] buffer = new byte[4096];
			int bytesRead = input.read(buffer);
			while (bytesRead != -1){
				fos.write(buffer, 0, bytesRead);
				bytesRead = input.read(buffer);
			}
		}
	}
	
	private static String formatTimeInterval(long time, String format){
		TimeUnit c = TimeUnit.MILLISECONDS;
		
		long S = c.toSeconds(time);
		long M = c.toMinutes(time);
		long H = c.toHours(time);
		long D = c.toDays(time);

		long s = c.toSeconds(time - TimeUnit.MINUTES.toMillis(M));
		long m = c.toMinutes(time - TimeUnit.HOURS.toMillis(H));
		long h = c.toHours(time - TimeUnit.DAYS.toMillis(D));
		
		String r = format;
		r = r.replace("%S", "" + S);
		r = r.replace("%M", "" + M);
		r = r.replace("%H", "" + H);
		r = r.replace("%D", "" + D);
		r = r.replace("%s", (s > 9 ? "" : "0") + s);
		r = r.replace("%m", (m > 9 ? "" : "0") + m);
		r = r.replace("%h", (h > 9 ? "" : "0") + h);
		
		return r;
	}
	
}
