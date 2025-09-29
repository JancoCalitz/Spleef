# SpleefPlugin

SpleefPlugin is a custom Minecraft mini-game plugin designed for Paper/Spigot servers.  
It brings the classic **Spleef** gameplay to life by automatically pasting an arena schematic and dynamically modifying the arena floor during matches to keep the game unpredictable and fun.

## Overview
When the plugin is enabled, it automatically pastes a preconfigured arena schematic (`phoenixarena.schem`) into the **genworld** world.  
During gameplay, the plugin periodically removes and restores blocks within a circular arena radius to simulate the collapsing floor effect that is central to Spleef. Roughly 30% of the arena floor disappears at set intervals, forcing players to stay on the move and adding tension to every match.

## Features
- Automatic schematic pasting for arena setup (requires **FastAsyncWorldEdit** or **WorldEdit**).  
- Dynamic arena floor mechanics:  
  - Randomised blocks are removed to simulate collapsing ice/snow.  
  - Blocks regenerate after a short delay to reset the field.  
- Fully automated event loop with no manual setup required after startup.  
- Configurable arena location, radius, and schematic file path (in the code).  

## Technical
- **Minecraft:** Spigot/Paper 1.21.1  
- **Language:** Java 21  
- **Build Tool:** Maven  
- **Dependencies:** WorldEdit API (via FastAsyncWorldEdit or WorldEdit)  

## Installation
1. Ensure you have **FastAsyncWorldEdit** or **WorldEdit** installed.  
2. Place the schematic file (`phoenixarena.schem`) in the `plugins/FastAsyncWorldEdit/schematics/` directory.  
3. Build the plugin with Maven or use the precompiled JAR.  
4. Place the JAR in your server’s `plugins/` folder.  
5. Start the server and the plugin will paste the arena automatically into `genworld`.  

---

## Author
This plugin was developed by **Penta** and is shown here for demonstration purposes. Rights reserved.
