# Coding Exercise

## Description

This repository contains a working implementation of an API Endpoint that uses a third party service called Dark Sky to inform on current weather conditions based on a client's geo location. Solve the tasks explained below using the information and pre-conditions available in this document, as well as any resource or library you might find online.


## Tasks
Implement the following tasks, making sure that you work thoroughly, without losing sight of the objective.
In case you you feel like the tasks are too overwhelming to fully solve in this short time period, try and prioritize what you think is more important.

1. In order to get yourself acquainted with the code base, extend the current implementation to also return a frost warning if the temperature drops below 0. The new contract has already been defined and you have agreed that the new JSON property should be named frost_warning.

2. *Dark Sky* has been known to be unreliable in the past, both in terms of the reported weather conditions, as well as regarding service availability. Hence you're meant to explore and implement an alternative based on the *Open Weather* API, for which your Product owner has given you the following acceptance criteria:

	* If both services are operational, our service should return the average of both.
	* If one service is not operational, returns an error, or does not respond within 2 seconds, we should automatically fall back to the other


## Additional Information

### Open Weather API Documentation

* https://openweathermap.org/current