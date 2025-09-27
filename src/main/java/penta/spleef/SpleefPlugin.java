package penta.spleef;

import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
//import com.sk89q.worldedit.operation.Operations;
//import com.sk89q.worldedit.operation.Operation;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;

public class SpleefPlugin extends JavaPlugin {

    private final Random random = new Random();
    private final Map<Location, Material> changedBlocks = new HashMap<>();

    private static final int RADIUS = 26;
    private static final int CENTER_X = -74;
    private static final int CENTER_Y = 160;
    private static final int CENTER_Z = -157;

    @Override
    public void onEnable() {
        getLogger().info("Spleef plugin starting...");

        try {
            pasteArenaSchematic();
            getLogger().info("Schematic paste completed.");
        } catch (Exception e) {
            getLogger().severe("Error during schematic paste: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            startBlockLoop();
            getLogger().info("Block loop started.");
        } catch (Exception e) {
            getLogger().severe("Error starting block loop: " + e.getMessage());
            e.printStackTrace();
        }

        getLogger().info("Spleef plugin enabled successfully.");
    }


    private void startBlockLoop() {
        World world = Bukkit.getWorld("genworld");
        if (world == null) {
            getLogger().warning("World 'genworld' not found. Spleef loop not started.");
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                int area = 0;
                for (int x = -RADIUS; x <= RADIUS; x++) {
                    for (int z = -RADIUS; z <= RADIUS; z++) {
                        if (Math.sqrt(x * x + z * z) <= RADIUS) {
                            area++;
                        }
                    }
                }

                int targetCount = (int) (area * 0.3); // 30% of arena
                int changed = 0;
                while (changed < targetCount) {
                    int x = CENTER_X + random.nextInt(RADIUS * 2) - RADIUS;
                    int z = CENTER_Z + random.nextInt(RADIUS * 2) - RADIUS;
                    if (Math.sqrt(Math.pow(x - CENTER_X, 2) + Math.pow(z - CENTER_Z, 2)) > RADIUS)
                        continue;

                    Location loc = new Location(world, x, CENTER_Y, z);
                    Material current = loc.getBlock().getType();
                    if (!current.isAir()) {
                        changedBlocks.put(loc, current);
                        loc.getBlock().setType(Material.AIR);

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                Material old = changedBlocks.remove(loc);
                                if (old != null && loc.getBlock().getType() == Material.AIR) {
                                    loc.getBlock().setType(old);
                                }
                            }
                        }.runTaskLater(SpleefPlugin.this, 140L);
                    }
                    changed++;
                }
            }
        }.runTaskTimer(this, 0L, 180L);
    }

    private void pasteArenaSchematic() {
        World world = Bukkit.getWorld("genworld");
        if (world == null) {
            getLogger().warning("World 'genworld' not found. Cannot paste schematic.");
            return;
        }

        File schemFile = new File("plugins/FastAsyncWorldEdit/schematics/phoenixarena.schem");
        if (!schemFile.exists()) {
            getLogger().warning("Schematic file not found: " + schemFile.getPath());
            return;
        }

        try {
            ClipboardFormat format = ClipboardFormats.findByFile(schemFile);
            Clipboard clipboard;
            try (ClipboardReader reader = format.getReader(new FileInputStream(schemFile))) {
                clipboard = reader.read();
            }

            com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(world);
            EditSession editSession = WorldEdit.getInstance().newEditSession(adaptedWorld);

            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(CENTER_X, 161, CENTER_Z))
                    .ignoreAirBlocks(false)
                    .build();

            Operations.complete(operation);
            editSession.close();
            getLogger().info("Arena schematic pasted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().warning("Failed to paste schematic: " + e.getMessage());
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Spleef plugin disabled.");
    }
}