package com.keurig.xpbooster.util;

import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteItemNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableItemNBT;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ItemBuilder {

    private final ItemStack is;

    public ItemBuilder(Material m) {
        this(m, 1);
    }

    public ItemBuilder(ItemStack is) {
        this.is = is;
    }

    public ItemBuilder(Material m, int amount) {
        if (Bukkit.getVersion().contains("1.7")) {
            is = new ItemStack(m, amount);
        } else {
            is = new ItemStack(XMaterial.matchXMaterial(m).parseMaterial(), amount);
        }
    }

    public ItemBuilder(Material m, int amount, byte durability) {
        if (Bukkit.getVersion().contains("1.7")) {
            is = new ItemStack(m, amount, durability);
        } else {
            is = new ItemStack(XMaterial.matchXMaterial(m).parseMaterial(), amount, (short) durability);
        }
    }

    public static ItemBuilder item(Material m) {
        return new ItemBuilder(m);
    }

    public static ItemBuilder item(ItemStack is) {
        return new ItemBuilder(is);
    }

    public static ItemBuilder fromString(String material) {
        material = material.toUpperCase();
        return item(Material.valueOf(material));
    }

    public static ItemBuilder fromConfig(ConfigurationSection section) {
        if (section.getString("material") == null) {
            return null;
        }

        ItemBuilder is = ItemBuilder.fromString(section.getString("material"));
        return is;
    }

    @Override
    public ItemBuilder clone() {
        return new ItemBuilder(is);
    }

    public ItemBuilder setCustomModelData(int data) {
        ItemMeta im = is.getItemMeta();
        im.setCustomModelData(data);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder modify(Consumer<ReadWriteItemNBT> consumer) {
        NBT.modify(is, consumer);
        return this;
    }

    public ItemBuilder get(Consumer<ReadableItemNBT> consumer) {
        NBT.get(is, consumer);
        return this;
    }

    public ItemBuilder setDurability(short dur) {
        is.setDurability(dur);
        return this;
    }

    public ItemBuilder setDurability(int dur) {
        is.setDurability((short) dur);
        return this;
    }

    public ItemBuilder setName(String name) {
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(Chat.color(name));
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder setLocalizedName(String name) {
        ItemMeta im = is.getItemMeta();
        im.setLocalizedName(Chat.color(name));
        is.setItemMeta(im);
        return this;
    }


    public ItemBuilder setUnColoredName(String name) {
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(name);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addUnsafeEnchantment(Enchantment ench, int level) {
        if (level < 1) {
            return this;
        }
        is.addUnsafeEnchantment(ench, level);
        return this;
    }

    public ItemBuilder removeEnchantment(Enchantment ench) {
        is.removeEnchantment(ench);
        return this;
    }

    public ItemBuilder setSkullOwner(String owner) {
        try {
            SkullMeta im = (SkullMeta) is.getItemMeta();
            im.setOwner(owner);
            is.setItemMeta(im);
        } catch (ClassCastException expected) {
        }
        return this;
    }

    public ItemBuilder setGlow(boolean value) {
        ItemMeta itemMeta;


        if (value) {
            addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
            itemMeta = is.getItemMeta();
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        } else {
            removeEnchantment(Enchantment.LURE);
            itemMeta = is.getItemMeta();
            itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        is.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment ench, int level) {
        if (level < 1) {
            return this;
        }
        ItemMeta im = is.getItemMeta();
        im.addEnchant(ench, level, true);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment ench, int level) {
        if (level < 1) {
            return this;
        }
        is.addEnchantment(ench, level);
        return this;
    }

    public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
        is.addEnchantments(enchantments);
        return this;
    }

    public ItemBuilder setInfinityDurability() {
        is.setDurability(Short.MAX_VALUE);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        ItemMeta im = is.getItemMeta();
        im.setLore(List.of(lore));
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        ItemMeta im = is.getItemMeta();
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder removeLoreLine(String line) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        if (!lore.contains(line))
            return this;
        lore.remove(line);
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder removeLoreLine(int index) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        if (index < 0 || index > lore.size())
            return this;
        lore.remove(index);
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addLoreLine(String line) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (im.hasLore())
            lore = new ArrayList<>(im.getLore());
        lore.add(Chat.color(line));
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addUnTranslatedLoreLine(String line) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (im.hasLore())
            lore = new ArrayList<>(im.getLore());
        lore.add(ChatColor.translateAlternateColorCodes('&', line));
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addLoreLine(String line, int pos) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        lore.set(pos, line);
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addLoreLineToTop(String line) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add(line);
        lore.addAll(im.getLore());
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addLoreLineToTop(String... line) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.addAll(List.of(line));
        lore.addAll(im.getLore());
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemStack toItemStack() {
        return is;
    }
}