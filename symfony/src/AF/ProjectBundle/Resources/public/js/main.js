/**
 * File that parses Elastic search queries into JSON and calls the appropiate d3.js code
 *
 * Created by juanito on 14/06/15.
 */

var datasetName = "small";

$(document).ready(function () {
  //  "use strict";


    d3.json("http://master.devarias.com:9200/results/_search?q=dataSetName:" + datasetName, function (data) {
        if (data.hits == undefined || data.hits.hits === undefined) {
            console.error("No data retrieved from ES");
        }

        var ds = $('#dataset');
        var topicInfo = [];
        var inferedInfo = [];

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

        console.log(topicInfo);
        console.log(inferedInfo);

        // Now that we've got data, populate controls
        for (var topic in topicInfo) {
            var option = new Option();
            option.id = topic;
            option.innerHTML = topic;
            ds.append(option);
        }
        var topic_graph = $("#topic_graph");
        topic_graph.lda({
            width: topic_graph.width(),
            height: 300,
            margin: 0
        });
        ds.on("change", function (ev) {
            var id =  $("#dataset")[0].options[this.selectedIndex].id;
            topic_graph.data('lda').setCurrentDataset(topicInfo[id]);
        }).trigger("change");
    });


})
;
