# Citi Bike Closest Station Calculator 

This project aims to provide a solution for finding the closest [CitiBike](https://citibikenyc.com/how-it-works) stations based 
on user-provided location data. The application consists of three key parts:

### Part 1: CitiBike API
In this phase, we request and retrieve data from the CitiBike API. 
This data will include information on station locations and their statuses, which will be used in the 
subsequent steps.

### Part 2: AWS Lambda Function
An AWS Lambda function will be developed to take two sets of Latitude and Longitude coordinates (from/to) as 
input. The Lambda will query the CitiBike data and return the closest available CitiBike stations for 
renting and returning bikes.


### Part 3: Map Visualization GUI
Developed an application that visually displays the "starting point" and "destination" on a map. 
The map will also show the nearest CitiBike stations based on the location data, providing users with an 
intuitive way to interact with the data.

### Part 4: Optimize the Lambda with AWS S3
To reduce unnecessary API calls, a cache for station information is implemented using AWS S3. 
The Lambda checks if the cached data is less than 1 hour old; if not, it fetches the latest data 
from the CitiBike API, updates the cache, and uploads the data to S3. 
This optimization improves performance by minimizing redundant requests.

![MapView.png](screenshots%2FMapView.png)

### Resources
* [Citi Bike JSON Information](https://gbfs.citibikenyc.com/gbfs/en/station_information.json)
* [Citi Bike JSON Status](https://gbfs.citibikenyc.com/gbfs/en/station_status.json)
* [Open Street Map](https://github.com/msteiger/jxmapviewer2)
* [JProgressBar](https://docs.oracle.com/javase/tutorial/uiswing/components/progress.html#indeterminate)
