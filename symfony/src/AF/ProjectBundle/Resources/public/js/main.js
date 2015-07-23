/**
 * File that parses Elastic search queries into JSON and calls the appropiate d3.js code
 *
 * Created by juanito on 14/06/15.
 */

var indexName = "test";
var elasticUrl = "http://localhost:9200/";

$(document).ready(function () {
    "use strict";


    d3.json(elasticUrl + indexName + "/topics/_search",
        function (data) {
            var termsInfo = [], datesInfo = [], topics = ["Topicaso", "Topiquito", "Topotamadre"];

            if (data == undefined || data.hits == undefined || data.hits.hits === undefined) {
                console.error("No data retrieved from ES");
                for (var topic in topics) {

                    termsInfo[topics[topic]] = [];
                    for (var i = 0; i < 30; i++) {
                        termsInfo[topics[topic]].push({topicName: topics[topic], term: 'term' + i, weight: Math.random()});
                        datesInfo.push({topicName: topics[topic], date: new Date(2013, 2, i), chance: Math.random()});
                    }
                }
            }
            else {
                for (var i in data.hits.hits) {
                    var hit = data.hits.hits[i];
                    if (hit != undefined && hit._source != undefined) {
                        var source = hit._source;

                        topics.push(source.topicName);
                        termsInfo[source.topicName] = source.terms;

                        for (var item in source.dates) {
                            var dateInfo = source.dates[item];
                            dateInfo.topicName = source.topicName;
                            dateInfo.date = new Date(dateInfo.date);
                            datesInfo.push(dateInfo)
                        }
                    }
                }
            }

            var inferred_graph = $("#infered_graph");
            inferred_graph.inference({
                width: inferred_graph.width(),
                height: 500
            });

            var ds = $("#topicSelector");
            var showOne = false;
            for (var topic in topics) {
                var info = termsInfo[topics[topic]];

                var option = new Option();
                option.id = topics[topic];
                option.innerHTML = topics[topic];
                ds.append(option);

                var tbody = $("#topic_" + topic + " #table tbody");
                for (var k = 0; k < Math.min(5, info.length); k++) {

                    tbody.append('<tr><th scope="row">' + (1 + k).toString() +
                        '</th><td>' + info[k].term +
                        '</td><td>' + (info[k].weight).toString().substring(0, 6) +
                        '</td></tr>'
                    );
                }

                var topic_graph = $("#topic_" + topic + " #graph");

                topic_graph.topics({
                    barScale: 0.87,
                    padding: 40,
                    svgClassName: "topic_graph_" + topic
                });

                if (!showOne) {
                    $("#topic_" + topic).removeClass("hide").show();
                    showOne = true;
                } else {
                    $("#topic_" + topic).removeClass("hide").hide();
                }
                topic_graph.data('topics').setCurrentDataset(info);
            }
            inferred_graph.data('inference').addDataSet(datesInfo);

            ds.on("change", function (ev) {
                for (topic in topics) {
                    var item = $("#topic_" + topic);
                    item.hide()
                }
                $("#topic_" + ds[0].selectedIndex).fadeIn("slow");
            });


        }
    )
    ;
})
;
