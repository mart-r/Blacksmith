

**Blacksmith Citizens2 Trait**
------------------------------

Status: [![Build Status](http://84.200.222.238:8080/buildStatus/icon?job=Github.HurricanKai.Blacksmith)](http://84.200.222.238:8080/job/Github.HurricanKai.Blacksmith/)

*Backed by [Citizens2](https://github.com/CitizensDev/Citizens2)* 

*Get Latest Build	[Jenkins](http://ci.citizensnpcs.co/job/Blacksmith/)* 

*Major Builds [Github Releases](https://github.com/HurricanKai/Blacksmith/releases)* 


----------


**Config:**
-------------

- **Config Values:**
 - BASE_PRICE = The Basis Price Added to Any Other Price
 - PRICE_PER_DURABILITY_POINT = Price added to Full Price per Durability point
 - BUSY_WITH_PLAYER_MESSAGE = Message to Display if already Busy with another Player
 - BUSY_WITH_REFORGE_MESSAGE = Message to Display if Currently Reforging Item of Player 
     - [Ignored if DISABLED_COOLDOWN = true]
 - COOLDOWN_UNEXPIRED_MESSAGE = Message to Display if internal Cooldown = 0
 - COST_MESSAGE = Message to Display to Show Cost
     - `<price>` gets replaced with the price
     - `<item>` gets replaced with the Item
     - `<MaxDurability>` gets replaced with the Items Max Durabilty
     - `<Durability` gets replaced with the Items Current Durability
 - DISABLE_COOLDOWN = true if REFORGE_COOLDOWN shoud be Disabled
 - DISABLE_DELAY true if Min and Max -_Reforge_DELAY soud be Ignored
 - ENCHANTMENT_MODIFIER = Price Added Per Enchantment Level
 - FAIL_CHANCE = Chance to Fail
 - FAIL_MESSAGE = Message to Display if Failed
 - INSUFFICIENT_FUNDS_MESSAGE = Message to Display if not Enought 
     - `<price>` gets replaced with Price
     - `<currentfounds>` gets replaced with Current Player Founds
 - INVALID_ITEM_MESSAGE = Message to Display if Item is not Valid
 - ITEM_UNEXPECTEDLY_CHANGED_MESSAGE = Error Message, gets send if the Item changed while init!
 - EXTRA_ENCHANTMENT_CHANCE = Chance to Add a New Enchantment
 - MAX_ENCHANTMENTS = Max Enchantments befor unable to Reforge
 - MAX_REFORGE_DELAY = Maximum Reforge Delay
     - [Ignored if DISABLED_COOLDOWN = true]
 - MIN_REFORGE_DELAY = Minimum Reforge Delay
     - [Ignored if DISABLED_COOLDOWN = true]
 - REFORGE_COOLDOWN = Cooldown befor able to Reforge item of **SAME** player again
     - [Ingored if DISABLED_COOLDOWN = true]
 - START_REFORGE_MESSAGE = Message to Display on Starting Reforge
 - SUCCESS_MESSAGE = Message to Display on Succes
 - DROP_ITEM = If true, Drops the Item. Else, Gives to Player. !! NOT USING /GIVE IT REPLACES THE CURRENT MAIN HAND!
