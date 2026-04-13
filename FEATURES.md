# JustEnoughFakepixel Features


## QoL

- **Block Selection Overlay** — Replaces the vanilla block selection with a custom filled or outline highlight.
- **Enchant Parser** — Colors enchants by level, sorts ultimates to the top, and supports normal/compressed/expanded layout and chroma animation.
- **Gyro Wand Helper** — Shows the AoE ring when holding the Gyrokinetic Wand, plus a cooldown timer.
- **Roman Numerals** — Converts Roman numerals to integers.
- **Prevent Cursor Reset** — Stops the mouse cursor from resetting when opening GUIs.
- **Skyblock ID** — Shows the internal SkyBlock item ID at the bottom of tooltips.
- **Disable Enchant Glint** — Removes the enchantment glint.
- **Brewing Helper** — Highlights brewing stands.
- **Missing Enchants** — Hold Shift on an enchanted item to see missing enchants.
- **Confirm Disconnect** — Makes you click twice to disconnect so you don't do it by accident.


## Misc

- **Performance HUD** — Shows FPS, TPS, ping, coords, and rotation in a small overlay.
- **Search Bar** — Adds a search bar to inventory GUIs with item highlighting.
- **Item Cooldowns** — Tracks cooldowns for abilities and invincibility timers with a HUD overlay.
- **Current Pet** — Shows your active pet as a HUD overlay.
- **Item Pickup Log** — Shows recently picked up or dropped items in a HUD.
- **Inventory Buttons** — Adds clickable shortcut buttons to inventories; configure with `/jefbuttons`.
- **Item Stack Tips** — Shows enchant levels on books and floor numbers on Catacombs passes.
- **Party Finder Floor Labels** — Shows F1–F7, M1–M7, or ENT on listings in the Party Finder.
- **Skill XP Display** — Hold Shift on a skill item to see XP remaining to max.
- **No Swap Animation** — Removes the item lowering animation when switching hotbar slots.
- **Show Own Nametag** — Shows your own nametag in third person.
- **Disable Entity Fire** — Hides the fire overlay on burning entities.
- **SkyBlock XP in Chat** — Sends SkyBlock XP gains from the action bar into chat. *(Needs server support; probably won't work on Hypixel.)*


## Dungeons

- **Blood Mob Highlight** — Highlights blood room mobs with a box or glow.
- **Boss Highlights** — Highlights Bonzo, Scarf, Scarf's minions, and the Professor with configurable colors.
- **Dungeon Overlay** — Run timers and end-of-run stats in chat.
- **Dungeon Breaker Overlay** — Shows Dungeon Breaker charges while in a dungeon.
- **Dungeon Room Overlay** — Shows the name of your current dungeon room.
- **CSGO Chest Opening** — Opening an obsidian/bedrock chest plays a CS:GO crate opening animation.


## Mining

- **Fetchur Overlay** — Shows today's Fetchur item.
- **Gemstone Powder Tracker** — Tracks powder and chest drops in Crystal Hollows.
- **HOTM Powder Display** — Adds powder spent vs. max cost to HOTM perk tooltips; hold Shift to see the cost for the next 10 levels.


## Fishing

- **Trophy Fish Tracker** — Tracks trophy fish counts with an overlay, chat message formatting, and Odger tooltip totals.
- **Fishing Timer** — Shows a timer while fishing with a configurable alert time.


## Diana

- **Diana Tracker** — Tracks playtime, burrows, and mob rates during the Diana event.
- **Event Overlay** — HUD for the event stats.
- **Loot Overlay** — HUD for chimeras, rare drops, and coins.
- **Inquisitor HP Overlay** — Live HP bar for the nearest Minos Inquisitor.
- **Diana Mob HP Overlay** — Live HP bar for the nearest non-inquisitor Diana mob.


## Farming

- **Lock Mouse** — Locks your yaw and pitch so you don't accidentally move the camera while farming.


## Scoreboard

- **Custom Scoreboard** — Replaces the vanilla sidebar with a custom one. Lines, order, colors, and scale are configurable.


## Waypoints

- **JEF Ordered Waypoints** — `/jw guide`
- **Waypoint Manager** — GUI to manage waypoint groups.
- **Auto Advance** — Automatically moves to the next waypoint when you're close enough for long enough.


## Commands

+ `/jef [category]` — Opens the JEF config, optionally jumping to a category.
+ `/jef reload` — Reloads repo data (timers and other remote config).
+ `/diana <reset|toggle>` — Resets or pauses Diana tracking.
+ `/pdt <reset|toggle>` (`/powdertracker`) — Resets or pauses the powder tracker.
+ `/lockmouse` — Toggles mouse lock.
+ `/jw guide` — All the commands for JEF ordered waypoints.
+ `/waypoint` — Opens the waypoint group manager.
+ `/jefbuttons` — Opens the inventory button editor.


## Party Commands

+ `!help` for Diana commands.
+ `!pb` to view personal bests of dungeon floors and phases.
  - Usages: `!pb f1`–`m7`, `br`, `p1`, `p2`, `p3`, `p4`, `p5`
+ `!jef` to view a user's JEF version.
