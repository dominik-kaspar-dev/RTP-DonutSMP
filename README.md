# RTP-DonutSMP

RTP plugin for DonutSMP with a GUI world selector and configurable RTP settings.

## Requirements

- Paper 1.21.x
- Java 21
- Vault + an economy plugin (for price support)

## Build

```bash
mvn -q -DskipTests package
```

Jar output: `target/rtp-donutsmp-1.0.0.jar`

## Commands

- `/rtp` - Open the RTP menu.
- `/rtp reload` - Reload config and menu layout.
- `/rtp set <center|radius|cooldown|price> <world> <value>`
  - `center` supports `here` or `x y z`.
- `/rtp set warmup <seconds>`

## Permissions

- `rtpdonutsmp.use` - Use `/rtp` and the menu.
- `rtpdonutsmp.admin` - Use `/rtp reload` and `/rtp set`.

## Config

Main config: `config.yml`

- `settings.max-tries` - Safe location search attempts.
- `settings.global-cooldown` - Global per-player cooldown.
- `settings.warmup-seconds` - Time before teleport starts.
- `worlds.<key>` - World settings (center, radius, cooldown, price, display name, icon, lore).

Menu layout: `menu.yml`

- `menu.size` - Inventory size (9-54, rounded to nearest multiple of 9).
- `menu.title` - Menu title.
- `menu.layout` - Slot -> world key mapping.

Example layout (supports more than 3 worlds):

```yml
menu:
	size: 27
	title: "&6RTP Worlds"
	layout:
		"11": "world"
		"13": "world_nether"
		"15": "world_the_end"
		"20": "mining_world"
```
