/*
 * Copyright 2019 ProtectionStones team and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.espi.ProtectionStones.commands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.espi.ProtectionStones.*;
import dev.espi.ProtectionStones.utils.WGUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

class ArgAdminHide {

    // /ps admin hide
    static boolean argumentAdminHide(CommandSender p, String[] args) {
        RegionManager mgr;
        World w;
        if (p instanceof Player) {
            mgr = WGUtils.getRegionManagerWithPlayer((Player) p);
            w = ((Player) p).getWorld();
        } else {
            if (args.length != 3) {
                PSL.msg(p, PSL.ADMIN_CONSOLE_WORLD.msg());
                return true;
            }
            if (Bukkit.getWorld(args[2]) == null) {
                PSL.msg(p, PSL.INVALID_WORLD.msg());
                return true;
            }
            w = Bukkit.getWorld(args[2]);
            mgr = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(w));
        }

        Bukkit.getScheduler().runTaskAsynchronously(ProtectionStones.getInstance(), () -> {
            // loop through regions that are protection stones and hide or unhide the block
            for (ProtectedRegion r : mgr.getRegions().values()) {
                if (ProtectionStones.isPSRegion(r)) {
                    PSRegion region = ProtectionStones.getPSRegion(w, r);
                    if (args[1].equalsIgnoreCase("hide")) {
                        Bukkit.getScheduler().runTask(ProtectionStones.getInstance(), region::hide);
                    } else if (args[1].equalsIgnoreCase("unhide")){
                        Bukkit.getScheduler().runTask(ProtectionStones.getInstance(), region::unhide);
                    }
                }
            }

            String hMessage = args[1].equalsIgnoreCase("unhide") ? "unhidden" : "hidden";
            PSL.msg(p, PSL.ADMIN_HIDE_TOGGLED.msg()
                    .replace("%message%", hMessage));
        });

        return true;
    }
}