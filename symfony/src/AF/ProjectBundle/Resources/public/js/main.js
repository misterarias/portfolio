/**
 * File that parses Elastic search queries into JSON and calls the appropiate d3.js code
 *
 * Created by juanito on 14/06/15.
 */

var datasetName = "small";
var topicInfo = [];
var inferedInfo = [];
$(document).ready(function () {
    "use strict";

    d3.json("http://master.devarias.com:9200/results/_search?q=dataSetName:" + datasetName, function (data) {
        console.log(data);
        if (data.hits == undefined || data.hits.hits === undefined) {
            console.error("No data retrieved from ES");
        }

        for (var k in data.hits.hits) {
            var hit = data.hits.hits[k];
            if (hit != undefined && hit._source != undefined) {
                var source = hit._source;
                if (hit._type == "topics") {
                    topicInfo[source.topicName] = source.topics;
                } else if (hit._type == "inferred") {
                    inferedInfo[source.date] = source.topics_inferred;
                }
            }
        }
    });


});
