# Inhabitor

A simple server-side Fabric mod that adds utilities to make world pruning easier.

## Features

- A command to manually add/set the `inhabitedTime` of a range of chunks
- Fixes a Minecraft bug where the inhabited time is not being updated when unmodified chunks are unloaded

## Commands

`/inhabitor set <x1> <z1> <x2> <z2> <time>`: Sets the `inhabitedTime` of all chunks in the range to the specified time in ticks.

`/inhabitor add <x1> <z1> <x2> <z2> <time>`: Adds the specified time in ticks to the `inhabitedTime` of all chunks in the range.


**NOTE:** This command takes a lot of time to execute, so expect the server to lag while the command is running.
