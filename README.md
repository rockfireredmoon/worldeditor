World Editor is a tool for working with the large dataset of [game data](https://github.com/rockfireredmoon/iceee-data) required for [iceee](http://github.com/rockfireredmoon/iceee), our [Earth Eternal](http://www.theanubianwar.com) server. It's basic aim is to give content creators (quests, items, creatures and more) the tools they need to be able to do their thing without getting involved in editing raw data files.

While we have had some delegation of this task, with developers directly editing data files in GitHub, the barrier-to-entry of this is still quite high. This tool is intended to make it possible for non-coders to more easily edit this data.

Intended aims are :-

 * Remove the need for most developers to directly edit game server data files
 * Eases use of Git for the simple task of pushing and pulling new content.
 * Make it easier for developers to locate existing data with searches and sortable, filterable tables.
 * Make it easier for developers to add and edit content by providing web based forms, menus, search-and-select dialogs and other features aimed at streamlining content creatiion.
 * Provide validation of all input and eliminate the mistakes that we get from the current by-hand approach.

## Usage

To access the tool, visit :-

http://www.theanubianwar.com:8880

.. and login with your TAW site login.

Once you have logged on, you will be present with a page showing a table of 'entities' taking up most of the left of the screen, with a slimmer Create/Edit panel to the right.

 1. Choose the type of entity to edit using the menu bar at the top of the window. Some entities are in submenus.
 1. Click on any entity in the table to select it for editing
 1. Narrow down what is displayed in the tables using filters in the table column headers
 1. You can resort the table by clicking the column headers
 1. Click on the New button at the top of the screen to create a new entity
 1. When creating a new entity, depending on the type of entity you will have different options for choosing an ID. This application will try to choose the default ID for you so in most cases you do not need to worry about it. Some entity types have no ID generation (usually non-numeric ones)
 1. When you have entered all the details for any entity,click Save at the bottom to either create a new entity or update an existing one.
 1. When you have finished making your changes, you should go the Stores page and Commit/Push the store. This sends the changes to GitHub so other people (and the development server) can be updated with your changes.

World Editor is capable of editing mulitple branches of the Earth Eternal game data. These branches are of course stored at  GitHub. The stores page is used to define what branches are available for editing, for you to choose with branch you are working in, and for you to push/pull changes to and from GitHub.

In practice (as of writing this), there will likely only be 1 or 2 entries on this page. One for the current focus of development (Valkals) and I will add a 2nd store for TAW when the time is right.

Note, nobody other myself really needs to add any futher stores or otherwise edit them. All you as developers need to be concerned with is the selection of the active store and the GitHub push/pull features.

## Selecting The Active Store

In most cases you will not need to do this, ash when you first login the first available store in the list will be automatically selected for you. However, if you need to select another store, locate it in the stores table and click on it's Activate button in the top of the Edit Panel.
Pulling Incoming Changes

It is possible that changes have been made to the data by others externally. For example, a developer may have edited a quest data file directly on GitHub. When you visit the stores page, a check is made (every 2 minutes) to see if there are any such external changes. If there are, a message will be displayed and the button Pull will be visible. Click on the button to retrieve changes.

# The Entity User Interface

Many pages of the world editor user interface are split up into 4 different areas.

## Menu Bar

The Menu Bar provides access to all entities you can manage as well as all other tools it provides to help in EE development. Click on each menu item to access that page or to open the submenu.

The menu bar will also show your current username and the store you are currently working on. Click on your username to signout. If you do not specifically sign out, you will be automatically logged out after 10 minutes of inactivity.

## Entity Action Bar

This is where you can perform specific actions related to either the type of entity or the currently selected entity. It's contents may change for different entity types, but in general you will see the following :-
New 	This will create a new entity of the current type and make it the currently edited item in the Edit Panel. It's values will be set to the types defaults, and the next available ID will be chosen. You may wish to choose a different ID (or auto-ID type) and/or select the target File depending on the type.
Raw 	This will display the raw entity data. Less useful now, it can still help sometimes to see this..
Export 	This will export all entities of this type to a downloadable file. Again, less useful these days now everything goes through GitHub

## Entity Table

This lists all entities of type currently selected.

## Navigation

You may 'page' through the table using the Navigation Bar at the top of the area by clicking on the page number. You scroll through the visible page numbers using the arrow buttons to either side of the page numbers.
Sorting

You may sort the entire table using the value of any column. For example, to sort alphabetically by Ability Name, click on the Name table header. To reverse the direction the sort, click on it again.
Filtering

You may filter the rows displayed by typing all or part of a search term in the fields at the top of each table column. For example, to find all abilities with the word 'Storm' them, type in 'storm' in the text field in the Name column of Abilities, and then eithe press the Enter key on your keyboard, or click the little magnifying glass icon.

So filter fields provide a drop down list of their possible values to help you.

Click on the eraser icon to clear any filters.

## Row Selection

Select any row by clicking anywhere on any column except the first (that contains the icons), or click on the pen icon. This will place the details of the entity selected in the Edit Panel for viewing and editing.

There are 3 icons at the start of each of row. These perform actions on that entity.
Edit 	Edit the entity. Has same effect as just clicking on the row.
Delete 	Delete the entity entirely. A confirmation dialog will be show.
Clone 	This takes a copy of the entity which it then places in the Edit Panel and assigns a new ID. For example, this is useful when creating a number of similar creatures, allowing you to quickly creature new creatures based on another.

## Edit Panel

The Edit Panel shows all the details of the currently selected entity, or of course a new entity you are creating. It contents will be different for each entity type, apart from the Edit Action Bar at the bottom. This is used to Save or Reset your current edit (no changes are applied to the game database until you press this), or you can use the Next and Previous buttons to navigate to the next or previous entity in the table.
Abilities

TODO
Books

TODO
Zones

TODO
Interactions

TODO
AIScripts

TODO
Quests

TODO
Items

TODO
Maps

TODO
Spawns

TODO
Loot
Loot Sets
Loot Packages
Loot Creatures

TODO
Tools

TODO
Known Isssues

 * First time loading "Creature" page (or anything that links to it such as Quests) can take a very long time (it must load ALL spawns world/wide). Can take up to 2 minutes. Subsequent loads are fast
 * Selecting an individual creature in the tabble that has a lot of spawns can take a short while (e.g. 10 seconds)
