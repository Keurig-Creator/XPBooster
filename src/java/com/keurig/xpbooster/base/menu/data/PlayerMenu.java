package com.keurig.xpbooster.base.menu.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Stack;


@Getter
@Setter
@AllArgsConstructor
public class PlayerMenu {

    private final HashMap<Object, Object> store = new HashMap<>();
    private final Stack<Menu> history = new Stack<>();
    private final Player player;

    public String getString(String key) {
        return getData(key, String.class);
    }

    public void setString(String key, String value) {
        store.put(key, value);
    }


    public PlayerMenu setData(String key, Object obj) {
        store.put(key, obj);
        return this;
    }

    public PlayerMenu setData(Object key, Object obj) {
        store.put(key, obj);
        return this;
    }

    public <T> T getData(String key, Class<T> ref) {
        Object obj = store.get(key);

        if (obj != null) {
            return ref.cast(obj);
        }

        return null;
    }

    public <T> T getData(Object obj, Class<T> ref) {
        Object obj2 = store.get(obj);

        if (obj2 != null) {
            return ref.cast(obj2);
        }

        return null;
    }

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
