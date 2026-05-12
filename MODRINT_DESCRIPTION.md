# RTP-DonutSMP

RTP-DonutSMP is a Paper plugin that adds a GUI-based random teleport system with per-world settings and safe defaults. Players use `/rtp` to open a menu, pick a world, and get teleported to a safe random location inside that world's configured radius.

It is built for server owners who want a clean, configurable RTP flow with a layout file, editable messages, and live admin commands.

## Why RTP-DonutSMP

- GUI world selector with a simple slot layout file
- Per-world control of center, radius, cooldown, price, icon, and lore
- Global warmup with action bar countdown and cancel-on-move
- Safe location search (avoids water, lava, leaves)
- Vault economy support for RTP pricing
- Admin commands to update settings without restarts
- LuckPerms-friendly permission nodes

## Features

- Supports more than 3 worlds in the menu
- Warmup countdown in the action bar (configurable)
- Cancel teleport if the player moves
- Safe location search with configurable attempt limit
- Global per-player cooldown
- Lore placeholders for radius, price, cooldown, and center

## Commands

- /rtp
- /rtp reload
- /rtp set <center|radius|cooldown|price> <world> <value>
- /rtp set warmup <seconds>

## Permissions

- rtpdonutsmp.use
- rtpdonutsmp.admin

## Config Structure

The plugin uses two files:

- config.yml for settings, messages, and world definitions
- menu.yml for menu size, title, and slot -> world layout

Example world settings:

```yml
settings:
	max-tries: 20
	global-cooldown: true
	warmup-seconds: 5

worlds:
	world:
		enabled: true
		display-name: "&aOverworld"
		icon: GRASS_BLOCK
		center:
			x: 0.0
			y: 64.0
			z: 0.0
		radius: 5000
		cooldown: 300
		price: 0.0
		lore:
			- "&7Radius: &f{radius}"
			- "&7Price: &f{price}"
			- "&7Cooldown: &f{cooldown}s"
```

Example menu layout (add more worlds by adding more slots):

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

## Messages

All user-facing text is editable in config.yml under `messages`, including:

- no-permission
- player-only
- world-not-found
- cooldown
- warmup-actionbar
- warmup-cancelled
- teleport-success
- teleport-failed

## Compatibility

- Server software: Paper
- Minecraft API target: 1.21.x
- Java: 21

## Install

1. Build or download the release jar.
2. Place it in your server plugins folder.
3. Start the server once to generate config files.
4. Edit config.yml and menu.yml.
5. Run /rtp reload.
