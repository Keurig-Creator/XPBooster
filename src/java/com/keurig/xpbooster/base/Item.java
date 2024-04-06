package com.keurig.xpbooster.base;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.List;

@Getter
@Setter
public class Item {
    public Material material;
    public String title;
    protected List<String> lore;
    public boolean glowing;
}
