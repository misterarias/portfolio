package com.ariasfreire.utils

import com.ariasfreire.models.GDELTRows
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by juanito on 18/06/15.
  */
object UtilsCSV {

   var sc: SparkContext = null

   def context: SparkContext = {
     if (sc == null) {
       val conf = new SparkConf().setAppName(s"Spark CSV Parser")
       sc = new SparkContext(conf)
     }
     sc
   }

   /**
    * Read data from a headerless CSV file into an RDD of OlympicMedalRecords
    * @return RDD[OlympicMedalRecords]
    */
   def readAsRDD(): RDD[GDELTRows] = {
     val sc = context
     val dataFile = "OlympicAthletes.csv"
     val gdeltDataRDD = sc.textFile(dataFile, 2).cache()

     val gdeltRowsRDD = gdeltDataRDD.map(data => {

       val info = data.split(";")
       new GDELTRows(info(0))
   })
     gdeltRowsRDD

//     // Read CSV into an RDD[OlympicMedalRecords]
//     //var firstRead = false
//     val recordsRDD = olympicDataRdd.map(line => {
//       val info = line.split(",")
//
//       // Athlete,Age,Country,Year,Closing Ceremony Date,Sport,Gold Medals,Silver Medals,Bronze Medals,Total Medals
//       val name = info(0) // name
//       val age = getInt(info(1)) // age
//       val country = info(2) // country
//       val game = getInt(info(3)) // olympicGame
//       // jump Closing Ceremony Data!!
//       val sport = info(5) // sport
//       val gMedal = getInt(info(6)) // gold Medals
//       val sMedal = getInt(info(7)) // silver Medals
//       val bMedal = getInt(info(8)) // bronze Medals
//
//       new OlympicMedalRecords(name, age, country, game, sport, gMedal, sMedal, bMedal)
//     })
//
//     return recordsRDD;
   }
 }
