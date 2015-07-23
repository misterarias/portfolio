/**
 * File that parses Elastic search queries into JSON and calls the appropiate d3.js code
 *
 * Created by juanito on 14/06/15.
 */

var indexName = "project";
var elasticUrl = "http://localhost:9200/" + indexName + "/topics/_search";
var fixedUrl = $("#data_locator").val();
$(document).ready(function () {
    "use strict";


    d3.json(elasticUrl, function (data) {
            var termsInfo = [], datesInfo = [], topics = [];

            if (data == undefined || data.hits == undefined || data.hits.hits === undefined) {
                console.error("No data retrieved from ES");


                // Debug code
                topics = ["Topicaso", "Topiquito", "Topotamadre"];
                for (var topic in topics) {

                    termsInfo[topics[topic]] = [];
                    for (var i = 0; i < 30; i++) {
                        termsInfo[topics[topic]].push({
                            topicName: topics[topic],
                            term: 'term' + i,
                            weight: Math.random()
                        });
                        datesInfo.push({topicName: topics[topic], date: new Date(2013, 2, i), chance: Math.random()});
                    }
                }
            } else {
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

                var topic_container = $("#topic_" + topic);
                var tbody = topic_container.find("#table tbody");
                for (var k = 0; k < Math.min(10, info.length); k++) {

                    tbody.append('<tr><th scope="row">' + (1 + k).toString() +
                        '</th><td>' + info[k].term +
                        '</td><td>' + (info[k].weight).toString().substring(0, 6) +
                        '</td></tr>'
                    );
                }
            }
            var graph_height = 0, graph_width = 0;
            for (var topic in topics) {
                var topic_container = $("#topic_" + topic);

                if (!showOne) {
                    topic_container.removeClass("hide").show();
                    showOne = true;

                    // Once it's visible, it has dimensions!
                    graph_height = topic_container.find("#table").height();
                    // Get the width of the fluid container
                    graph_width= topic_container.find("#graph").width();

                } else {
                    topic_container.removeClass("hide").hide();
                }

                var topic_graph = topic_container.find("#graph");
                topic_graph.topics({
                    height: graph_height,
                    width: graph_width,
                    barScale: 0.87,
                    padding: 50,
                    svgClassName: "topic_graph_" + topic
                });

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
