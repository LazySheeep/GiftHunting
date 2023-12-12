package io.lazysheeep.gifthunting;

import org.bukkit.Bukkit;

enum Permission
{
    OP("gifthunting.op"),
    PLAYER("gifthunting.player");

    Permission(String name)
    {
        this.name = name;
        this.value = Bukkit.getPluginManager().getPermission(name);
    }

    public final String name;
    public final org.bukkit.permissions.Permission value;
}
