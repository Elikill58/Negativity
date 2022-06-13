# Negativity

It's a Minecraft AntiCheat for multiples platforms :
- **Spigot** 1.7.10 to 1.19 (*and fork like PaperSpigot or Tuinity*)
- **Sponge** API 7, 8 & 9
- **Fabric** v0.53.0 for 1.18

Proxies:
- **BungeeCord**
- **Velocity**

If you are using proxy, you should put the plugins on all sub servers where you want to check players.

## Informations

Need help ? Have a question or something to suggest ?

Contact me via Discord private messages (`Elikill58#0743`) or in my server ([join it here](https://discord.gg/KHRVTX2)).

Suggestions and bug reports can also be filled in [this repository issue tracker](https://github.com/Elikill58/Negativity/issues).

## How to install ?

Check [installation wiki](https://github.com/Elikill58/Negativity/wiki/Installation) for all informations according to platform.

## I want to test it before download !

You can check it on **server.negativity.fr** (server in 1.13.2, you can connect with 1.13.2 to 1.19). Warn: it can be offline.

## What are the detected cheat ?

We made a wiki for all [detected cheat](https://github.com/Elikill58/Negativity/wiki/Cheat) which explain what every hack does.

## How detection works ? What do you check ?

If they are enabled, I'm checking a lot of things : movement, action, packet ...
Else, I'm doing anything. So, if you don't need detection, disable it and you will keep resources.

## And for bad connection ? TPS drop ?

By default, if the player have a **ping higher than 150 ms**, it will not create alert.

Also by default, if the server is **less than 19 TPS**, alert will be disabled

## Translation

We support a lot of languages :
- **English** (*en_US*, default lang) By Elikill58 & RedNesto
- **French** (*fr_FR*) By Elikill58
- **Portuguese** (*pr_BR*) By jheyson
- **Norwegian** (*no_NO*) By SuchHero
- **Russian** (*ru_RU*) By VidTu
- **Chinese** (*zh_CN*) By f0rb1d, SGYRE & Dust
- **German** (*de_DE*) By CodingAir
- **Dutch** (*nl_NL*) By DeveloperBoy
- **Swedish** (*sv_SV*) By YungSloop
- **Spanish** (*es_ES*) By SolitaSolaa
- **Vietnamese** (*vi_VN*) By HuyMCYTTM#7592
- **Italian** (*it_IT*) By Herobrine99dan#1564
- **Czech Republic** (*cs_CZ*) By Disordeon#1824
- **Albanian** (*sq_SQ*) By ErzenX#2439
- **Polish** (*pl_PL*) By Tytano#5336
- **Romanian** (*ro_RO*) By @Edward205
- **Egyptian Arabic** (*ar_EG*) By Andro Sameh#6837
- **Hungarian** (*hu_HU*) By HasX#1966
- **Korean** (*ko_KR*) By soshimee#4397
- **Indonesian** (*id_ID*) By Dave9123#0757
- **Thai** (*th_TH*) By IAmOverD҉A҉R҉K҉E҈S҉T҉#2498
- **English (GB)** (*en_GB*) By NEWBTW#2108

To change the lang, just use "/nlang" or change "Translation.default" to the one that you want.

You find a grammar error on your language ? You want to add your own ? Contact me on discord (link below)

(For developer, you can set your own translation system)

## I don't understand the config, HELP !

For general config, [click here](https://github.com/Elikill58/Negativity/wiki/Configurations).

For ban config, [click here](https://github.com/Elikill58/Negativity/wiki/Bans).

For permissions config, [click here](https://github.com/Elikill58/Negativity/wiki/Permissions).

## Build the plugin

Prerequisites:
- [Git](https://git-scm.com)
- JDK 8 (any distribution should work, [AdoptOpenJDK](https://adoptopenjdk.net/?variant=openjdk8&jvmVariant=hotspot) for example)

In the following instructions you will have to execute Gradle tasks. You **must** use the Gradle wrapper distributed in this repository.

Examples will be using `./gradlew`, if you are on Windows you need to use `gradlew.bat` instead.

1. Clone this repository: `git clone https://github.com/Elikill58/Negativity.git`
2. Get Spigot 1.7.10 and 1.13.2 jars and put them in `/spigot/spigotJars/` (you need to create this directory too)
3. You should use buildtools for Spigot 1.17/1.18/1.18.2/1.19 (or comment them in `settings.gradle`)
4. Setup a Forge workspace for the Sponge plugin: `./gradlew :sponge7:setupDecompWorkspace`. This step will take some time to complete
5. Build the plugin: `./gradlew build`
 - You can find the all-in-one jar in `/build/libs/`
 - Platform-specific jars are available in their own project subdirectories (`/spigot/build/libs/` for example)
