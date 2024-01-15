# **Running the application.**

GoTo DigitalApplication.java and Run as Java Program.
The application will run at default port 8080. A spring boot application with h2 inmemory database.

### **Viewing the h2-inmemory database:**

After deployment go to browser and type: **localhost:8080/h2-console/**

Change jdbdUrl to: **jdbc:h2:mem:testdb**

User Name: **nex**

Password:

This will successfully run the project and you can see the database tables being created for you. **MEDICATION** table already will have 10 records inserted for ease.

# **Product Design**

For the product design. I have created in such a way that: I will explain with each individual apis.

### - **Registering a Drone**

Type: POST

URL: localhost:8080/drones

Request Body:

{

    "serialNumber":"12345671-1c",
    "model": "heavyWeight",
    "batteryCapacity": 24

}

=> batteryCapacity if left as it is will have default value 100.

=> Model are divided into 4 parts as in question and won't accept other values besides it.

=> Serial Numbers must not be duplicated.

=> Drone then will be registered.

### - **Loading a Drone with medication**

Type: POST

URL: localhost:8080/drones/loading/1

Request Body:

[
{

      "medicationId":2,
      "quantity":3

},

{

      "medicationId":1,
      "quantity":3

}
]

=> a drone with less than 25 percent battery cannot be loaded.

=> Total weight to be loaded on drone cannot exceed the drone total weight.

=> Heavyweight drones can hold 500 gm, middleweight 350 gm, cruiserweight 250 and lightweight 100 gms respectively. (**This is based on my design**)

=>During Loading Phase, the drone will be on LOADING state and as soon it is loaded it will be LOADED on the database table. the transition will be quick.

=> if same medication and drone is found, the item will be appened else it will create a new item on the list.

=> The loading will be succesful if the cases are followed.

### - **Available Drones for medication**

Type: GET

URL: localhost:8080/drones/loading/1

=> All the drones that have the amount of that can hold the minimum value of medications are available to be loaded, that is. if the drone weight after loading is 4gm and the least medication item is with 3 gm, that drone will be availble for loading.

=> the battery should be greater than 25 percent.

### **viewing drone medication items**

Type: POST

URL: localhost:8080/drones/1

RequestBody:

=> All the drones that have medical items that they can carry along with quantity and weight can be viewed.

### **viewing drone Battery**

Type: GET

URL: localhost:8080/drones/1/battery

=> The battery capacity can be viewed.

### **Auditing the battery level**

There is a scheduler that runs every minute and records the battery percentage.

=> For my current logic, the battery will decrease by 1 percent every minute if the drone is in IDLE state and by 2 percent if it is working on any other state.

=> there is a file called **battery-state.txt** that will log all the battery percentage.

=>There is another scheduler which runs every 5 minutes that will check all the drones that have battery zero and will recharge it to 100.

### \*Registering a Medication item.

=> I have preloaded the sql queries to ful fill the MEDICATION table. but sill created an api just in case you want to add anything inside the table. To check the validation for name, code, weight and image.

type: POST

url: localhost:8080/medication

#### **_I have documented all the apis required for the modules to run inside resoureces folder that can be used to run the apis mentioned above. The file is called drone-app-apis.postman_collection.json_**

# \*\*UNIT TEST CASES

For unit test cases I have written using mock and junit 5 for all the service classes. That can be run using the IDE and tested.

## _\*\*All the conditions are satisfied as asked by the examinee. If anything let me know. I did not write the tokenization system for api as it was out of the scope. Followed the best practices._
