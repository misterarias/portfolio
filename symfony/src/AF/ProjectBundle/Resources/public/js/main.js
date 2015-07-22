/**
 * File that parses Elastic search queries into JSON and calls the appropiate d3.js code
 *
 * Created by juanito on 14/06/15.
 */

var indexName = "test";
var elasticUrl = "http://192.168.2.109:9200/";

$(document).ready(function () {
    "use strict";

    d3.json(elasticUrl + indexName + "/topics/_search",
        function (data) {
            if (data.hits == undefined || data.hits.hits === undefined) {
                console.error("No data retrieved from ES");
            }

            var ds = $('#dataset');
            var termsInfo = [], datesInfo = [], topics = [];

            for (var k in data.hits.hits) {
                var hit = data.hits.hits[k];
                if (hit != undefined && hit._source != undefined) {
                    var source = hit._source;

                    topics.push(source.topicName);
                    termsInfo[source.topicName] = source.terms;
                    datesInfo[source.topicName] = source.dates;
                }
            }

            console.log(termsInfo);
            console.log(datesInfo);

            // Now that we've got data, populate controls
            for (var topic in topics) {
                var option = new Option();
                option.id = topics[topic];
                option.innerHTML = topics[topic];
                ds.append(option);
            }

            var inferred_graph = $("#infered_graph");
            inferred_graph.inference({
                width: inferred_graph.width(),
                height: 300,

            });

            var topic_graph = $("#topic_graph");
            topic_graph.topics({
                width: topic_graph.width(),
                height: 300,
                barScale: 0.87,
                padding: 40
            });

            ds.on("change", function (ev) {
                var id = $("#dataset")[0].options[this.selectedIndex].id;

                //  topic_graph.data('topics').setCurrentDataset(termsInfo[id]);
                inferred_graph.data('inference').setCurrentDataset(datesInfo[id]);
            }).trigger("change");
        });
})
;
