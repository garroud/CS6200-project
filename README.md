# CS6200
This is the implementation of the CS6200 final project by Ruichao Xiao.

## Prerequisite
java 8;
Solr 8.20;
GUI is built on SWING;

## How to run it?
1. Download the dataset on [Yelp](https://www.yelp.com/dataset). We will need Reviews, Business, Pictures for the project. 
2. Using Solr command tools to insert the data into Solr; Typically, collections YelpReviews is used to store the Review dataset, YelpBusiness is used to store Business Dataset, YelpPhotos is used to store Pictures dataset. The actual pictures can be found through photo_id;
3. After having the Solr ready, open Solr at 8893.
4. Complie the code in `src/`
```
cd src
javac *.java
```
5. Run the demo with `java src/Main`.
