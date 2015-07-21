/**
 * File that parses Elastic search queries into JSON and calls the appropiate d3.js code
 *
 * Created by juanito on 14/06/15.
 */

var datasetName = "project_em_20";
var elasticUrl = "http://192.168.2.109:9200/";
var indexName = "project";
$(document).ready(function () {
    "use strict";

    d3.json(elasticUrl + indexName + "/inferred/_search?q=dataSetName:" + datasetName,
        function (data) {
            if (data.hits == undefined || data.hits.hits === undefined) {
                console.error("No data retrieved from ES");
            }

            var inferedInfo = [];
            for (var k in data.hits.hits) {
                var hit = data.hits.hits[k];
                if (hit != undefined && hit._source != undefined) {
                    var source = hit._source;
                    inferedInfo[source.date] = source.topics_inferred;
                }
            }

            console.log(inferedInfo);
        });

    d3.json(elasticUrl + indexName + "/topics/_search?q=dataSetName:" + datasetName,
        function (data) {
            if (data.hits == undefined || data.hits.hits === undefined) {
                console.error("No data retrieved from ES");
            }

            var ds = $('#dataset');
            var topicInfo = [];

            for (var k in data.hits.hits) {
                var hit = data.hits.hits[k];
                if (hit != undefined && hit._source != undefined) {
                    var source = hit._source;
                    topicInfo[source.topicName] = source.topics;
                }
            }

            console.log(topicInfo);

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
                height: 300
            });
            ds.on("change", function (ev) {
                var id = $("#dataset")[0].options[this.selectedIndex].id;
                topic_graph.data('lda').setCurrentDataset(topicInfo[id]);
            }).trigger("change");
        });


})
;
