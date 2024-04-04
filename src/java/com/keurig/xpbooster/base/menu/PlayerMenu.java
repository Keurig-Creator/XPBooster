package com.keurig.xpbooster.base.menu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.Stack;


@Getter
@Setter
@AllArgsConstructor
public class PlayerMenu {

    private final Stack<Menu> history = new Stack<>();
    private final Player player;

    public void addHistory(Menu menu) {
        history.push(menu);
    }

    public void removeHistory(Menu menu) {
        history.remove(menu);
    }

    public Menu getFirst() {
        return history.pop();
    }

    public Menu getLast() {
        return history.firstElement();
    }
}
