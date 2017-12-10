Dungeon Man
==========================
The basic premise of this game is procedurally generated Zelda.

How The Generation Occurs
=========================
The map for the overworld is different than the way that dungeons are generated.

Enemies
---------
Enemy placement is done through the same method in the overworld as well as the 
dungeons. This being that each tile is iterated over, at which point there is a 
random chance that an enemy will be placed in that location.

Overworld
---------
The 'walls' in the overworld are generated through the use of 'The Game Of Life'. Once 
'walls' are generated, dungeon entrances are placed using the same strategy.

Dungeon
---------
Dungeons are generated on a room-by-room basis. This meaning that once a room is created, 
a room will be generated on any one of four sides. If a room alrady exists at this position, 
then this room will be altered. Within each room, there is a chance for a block pattern to be 
placed within the room.

Acknowledgements
=========================
**Player and Enemy Sprites** - Thank you to 'LUNARSIGNALS'. Sprites can be found [here](https://opengameart.org/content/overhead-action-rpg-characters).
**Melee sounds** - Thank you to 'remaxim'. The sounds can be found [here](https://opengameart.org/content/3-melee-sounds).
**Magic Sound** -  Thank you to 'spookymodem'. The sound can be found [here](https://opengameart.org/content/magic-missiles).
**Fireball Sound** - Thank you to Julien Matthey [here](https://freesound.org/people/Julien%20Matthey/sounds/105016/).
**Game Over Music** - Thank you to 'Aszura'. The music can be found [here](https://opengameart.org/content/game-over-8-bit-style).
**OverWorld Theme** - Thank you to 'nene'. The music can be found[here](https://opengameart.org/content/long-away-home-8bit).
**Menu Theme** - Thank you to 't4ngr4m'. The music can be found [here](https://opengameart.org/content/generic-8-bit-jrpg-soundtrack).