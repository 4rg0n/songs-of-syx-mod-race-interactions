# Songs of Syx Mod Race Interactions

Will adjust the likings of each other race according to their similarities in preferences. 
For example when two races like to live only in WOOD building structures and both hate every other structure, their "liking" to each other will increase.
This will make the assignment of who likes who more dynamic.

## But why?!

This was suggested in the [Discord](https://discord.gg/USqtpm89) of Songs of Syx by the user **Twiggy#3123** and solves the following problem.
When a player has multiple race mods installed, there are usually no interactions between these. This mod will dynamically add likings between them.

## Config

There's a [configuration file](src/main/resources/mod-files/assets/init/config/RaceInteractions.txt) for tweaking which likings should be manipulated and how much does each category influence the likings.

```
** Will not manipulate any vanilla game races when true
CUSTOM_RACE_ONLY: true,
** Will not manipulate custom mod races likings to vanilla races
HONOR_CUSTOM_RACE_LIKINGS: true,
** How much each category should influence the likings. From -2.00 to 2.00
PREFERENCE_WEIGHTS: {
    FOOD: 1.0,
    CLIMATE: 1.0,
    BUILDING: 1.0,
    RELIGION: 1.0,
    WORK: 1.0,
},
** The mod needs this to know what are the vanilla races and what are modded races
VANILLA_RACES: [
    ARGONOSH, CANTOR, CRETONIAN, DONDORIAN, GARTHIMI, HUMAN, TILAPI,
],
```

# Getting started

[Maven](https://maven.apache.org/) is required to compile, package and install the mod.

**(1)** Do all at once: Install games jar file as dependency; compile mod source; prepare mod file structure; copy mod files to game mod directory
```
mvn validate install
```

**(!)** Game installation and mod directory paths are configured in the `pom.xml` in the `<profiles>` and are default set to:

**Game Installation**
* **Windows:** C:\Program Files (x86)\Steam\steamapps\common\Songs of Syx
* **Linux:** ~/.steam/steam/SteamApps/common/Songs of Syx

**Mod Directory**
* **Windows:** ${user.home}/AppData/Roaming/songsofsyx/mods/
* **Linux:** ~/.local/share/songsofsyx/mods/


**(2)** Run the game and you should see a `Race Interactions` mod. Activate it and launch.

**(3)** Start a new game. In the new game settings activate the `Race Interactions` under **Scripts**.

# Build commands

**(i)** Installs only the games `SongsOfSyx.jar` and `info/SongsOfSyx-sources.jar` as a dependency, and validate whether it was successful.
```
mvn validate
```

**(!)** This is required or the project won't find the games code.

**(i)** Building the mod only into `target/out`:
```
mvn package
```

The source code of the mod will be copied into e.g. `target/out/songs-of-syx-mod-example/V63/script/_src`.

**(i)** Build and copy the output into the games mods folder (excluding `_src`):
```
mvn install
```

**(!)** The games mod folder location varies on each OS. There are maven profiles "windows" and "linux". The "windows" profile is the default.
Maven should detect when you are building on a Linux OS and switch to the "linux" profile (not tested).
You can force a profile with e.g.

```
mvn install -P linux
```

**(i)** Deletes the `target` directory containing the packaged mod files and removes the mod from the games mod directory.
```
mvn clean
```

# Mod Info / Build Settings

In the `pom.xml` you will find `<properties>` where you can change information about the mod.
There you can also change the `<game.version.major>` property to your used game version. 
The `<game.version.minor>` property is only important when your mod really depends on stuff in this version and isn't compatible with lower versions.

Files (e.g. assets) for the mod are located in `src/main/java/resources/mod-files` and will be copied in the `package` phase.

# Debugging

You can enable **Debug Mode** and **Developer Mode** in the game launcher **settings**. 
You will get more detailed logs and in-game developer tools for testing.

## Intellij IDEA

There are two `.xml`files in the `.run/` folder:

* `Main.run.xml` launches the game directly
* `MainLaunchLauncher.run.xml` starts the game launcher

You may want to edit the `WORKING_DIRECTORY` option to your local game installation path.
It's default set to: `C:/Program Files (x86)/Steam/steamapps/common/Songs of Syx`.

And your package name may also differ in the `PATTERN` option.
It's default set to: `jake.example.*`

They should be automatically available [in the IDE](https://www.jetbrains.com/help/idea/run-debug-configuration.html). 
You can also edit them there :)

## Eclipse

* Add a new [Run Configuration](https://www.subjectcoach.com/tutorials/detail/contents/beginners-guide-to-eclipse-ide/chapter/working-with-run-configurations).
* Set the **main class** name to `init.MainLaunchLauncher`.
* In the tab **Arguments** set the **working directory** to your game installation folder e.g. `C:/Program Files (x86)/Steam/steamapps/common/Songs of Syx`.

## Testing

There's [JUnit 5](https://junit.org/junit5/) with [AssertJ](https://assertj.github.io/doc/) and [Mockito 4](https://site.mockito.org/) for testing your code.

## Developing Tips

See [doc/index.md](doc/index.md).

## Publish your Mod

### Steam

* [Official How to up- and download mods using Steam Workshop](https://steamcommunity.com/sharedfiles/filedetails/?id=2229540768)
* [(Discord) Steam Workshop Uploader](https://cdn.discordapp.com/attachments/664478122347069441/1023961932476186704/Songs_of_Syx_Workshop_Uploader.zip)

# Modding Resources

* [Discord](https://discord.com/eacfCuE)
* [Script Modding](https://docs.google.com/document/d/1FVOtfr3Y-cxH2Gw-i-OqW3Vbp0MPJp0xSyQ80UoCABE/edit)
* [Modding Guide](https://drive.google.com/file/d/1_OesG68HtJ4CwyHK7M72hQDOaCjeqgqT/view) (OUTDATED)
* [Basic Introduction](https://songsofsyx.old.mod.io/guides/introduction-adding-a-resource)
* [Basic Sprites](https://songsofsyx.old.mod.io/guides/basics-sprites)

# DISCLAIMER

The source code of the "roommod" example comes from the game files itself and were written by the game dev Jake de Laval.
I've just build the maven tooling around it.


