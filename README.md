# myRuns

myRuns is an exercise app for android. 
It allows users to track their exercise activity and displays
the user's location in a map. Previous entries are stored in the history tab 
and a settings tab allows the user to edit the app to their preferences

<p align="middle">
  <img src="images/start.png" alt="drawing" width="150"/>
  <img src="images/history.png" alt="drawing" width="150"/>
  <img src="images/settings.png" alt="drawing" width="150"/>
 </0>

This app utilizes the GoogleMaps API to display the user's location and track their movement in order to display the speed,
distance, etc. The app also features activity recognition that detects whether the user is sitting, walking, or running through a classification model in Weka.

<p align="middle">
  <img src="images/map.png" alt="drawing" width="150"/>
  <img src="images/entry.png" alt="drawing" width="150"/>
</p>

## Change Log
14/01/2023 
```bash
calendar.get(Calendar.MONTH) 
```
in MapsActivity and StartEntries is changed into
```bash
calendar.get(Calendar.MONTH + 1) 
```
to avoid `invalid value for monthofyear (valid values 1 - 12): 0`
