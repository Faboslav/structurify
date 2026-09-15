Structurify includes several useful commands that can help with modpack development, configuration, and debugging structure generation or placement.

## Locate structure command:
This command behaves mostly the same as the vanilla locate command, with one important difference: it runs asynchronously.
As a result, the world is not blocked while the command is executing.

```
/structurify locate structure <structure>
```

For example to locate any village, you can use the
```
/structurify locate structure #minecraft:village
```

## Dump command:
This command creates a `structurify_dump.json` file in your config directory.
The file contains the default settings for all currently loaded structures.

```
/structurify dump
```

## Debug command:
Debug is the most complex command of the mod, it can be enabled
```
/structurify debug enable
```

and then configured further with:
```
/structurify debug debug_mode
```