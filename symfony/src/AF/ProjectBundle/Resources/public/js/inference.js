/**
 * Setup and describe a topic inference visualization
 * Created by juanito on 5/06/15.
 */
(function ($) {
    "use strict";

    $.inference = function (element, options) {
        var defaults = {
            /** Reset to parent element width on every redraw */
            width: 700,
            /** Height of the graph */
            height: 250,
            /** Padding used to offset graph on the edges */
            padding: 40,
            /** Minimum ms between slider events */
            minInterval: 500
        };
        var $element = $(element), // reference to the jQuery version of DOM element
            element = element;    // reference to the actual DOM element
        var inference = this; // me
        var maximumDate;
        var minimumDate;
        var uniqueLabels = [],
            uniqueColors = [],
            uniqueValues = [];

        inference.addDataSet = function (dataset) {

            for (var k in dataset) {
                var item = dataset[k];
                if ($.inArray(item.topicName, uniqueLabels) == -1) {
                    uniqueValues[item.topicName] = [];
                    uniqueLabels.push(item.topicName);
                    // I like blue :P
                    uniqueColors.push("rgb(" +
                        Math.floor(Math.random() * 128) + "," +
                        Math.floor(Math.random() * 128) + "," +
                        Math.floor(Math.random() * 255) + ")");
                }
                // This way I'll have a list of lists, for path drawing
                uniqueValues[item.topicName].push(item);
            }
            for (var values in uniqueValues) {
                uniqueValues[values].sort(function (a, b) {
                    return d3.ascending(a.date.getTime(), b.date.getTime());
                });
            }

            redraw(dataset);
        };

        inference.init = function () {
            inference.settings = $.extend({}, defaults, options);
        };

        var redraw = function (data) {

            // Mouse events
            var tooltip = d3.select("body")
                .append("div")
                .attr("id", "tooltip")
                .html("")
                .attr("class", "graph_tooltip")
                .style("opacity", 0);

            function mousemove() {
                tooltip
                    .style("left", (d3.event.pageX + 20) + "px")
                    .style("top", (d3.event.pageY - 12) + "px");
            }

            // display date format
            var date_format = d3.time.format("%b %y");

            // Setup our canvas, if not there yet
            inference.settings.width = $(element).width();
            var iTransitionDuration = 400,
                xScale = d3.time.scale()
                    .domain(d3.extent(data, function (d) {
                        return d.date;
                    }))
                    .range([inference.settings.padding,
                        inference.settings.width - 2 * inference.settings.padding]),
                hScale = d3.scale.linear()
                    .domain(d3.extent(data, function (d) {
                        return d.chance;
                    }))
                    .range([inference.settings.height - inference.settings.padding,
                        inference.settings.padding]),
                yAxis = d3.svg.axis()
                    .orient("left")
                    .scale(hScale)
                    .ticks(7),
                rScale = d3.scale.pow()
                    .domain(d3.extent(data, function (d) {
                        return d.chance;
                    }))
                    .range([0.2, 1]),
                radius = 2, lineOpacity = 0.5, lineWidth = 1,
                xAxis = d3.svg.axis()
                    .orient("bottom")
                    .scale(xScale)
                    .ticks(d3.time.months, 1)
                    .tickFormat(date_format)
                ;

            var container = d3.select(element)
                    .attr("width", inference.settings.width)
                    .attr("height", inference.settings.height)
                ;

            var svg = container.append("svg")
                    .classed("canvas", true)
                    .classed("inference", true)
                    .attr("width", inference.settings.width)
                    .attr("height", inference.settings.height)
                ;

            var lineFunction = d3.svg.line()
                .x(function (d) {
                    return xScale(d.date);
                })
                .y(function (d) {
                    return hScale(d.chance);
                })
                .interpolate("linear");

            // Append axis and events
            svg.on("mousemove", mousemove);
            svg.append("g")
                .attr("class", "yaxis")
                .attr("transform", "translate(" + inference.settings.padding.toString() + ",0)")
                .call(yAxis)
            ;
            svg.append("g")
                .attr("class", "xaxis")   // give it a class so it can be used to select only xaxis labels  below
                .attr("transform",
                "translate(0," + (inference.settings.height - inference.settings.padding).toString() + ")")
                .call(xAxis);

            svg.selectAll("line.yGrid").data(hScale.ticks(7)).enter()
                .append("line")
                .attr(
                {
                    "class": "yGrid",
                    "x1": inference.settings.padding,
                    "x2": inference.settings.width - 2 * inference.settings.padding,
                    "y1": function (d) {
                        return hScale(d);
                    },
                    "y2": function (d) {
                        return hScale(d);
                    }
                });

            svg.selectAll("line.xGrid").data(xScale.ticks(d3.time.months, 1)).enter()
                .append("line")
                .attr(
                {
                    "class": "xGrid",
                    "x1": function (d) {
                        return xScale(d);
                    },
                    "x2": function (d) {
                        return xScale(d);
                    },
                    "y1": inference.settings.height - inference.settings.padding,
                    "y2": inference.settings.padding

                });

            svg.selectAll(".xaxis text")  // select all the text elements for the xaxis
                .attr("transform", function (d) {
                    return "translate(" + this.getBBox().height * -2 + ","
                        + this.getBBox().height + ")rotate(-45)";
                });

            // Create a path for each topic
            for (var t in uniqueValues) {
                var class_name = t.toLowerCase().replace(" ", "");
                svg.append("path")
                    .classed(class_name, true)
                    .attr("d", lineFunction(uniqueValues[t]))
                    .attr("stroke", function (d, i) {
                        return uniqueColors[uniqueLabels.indexOf(t)];
                    })
                    .attr("stroke-width", lineWidth)
                    .attr("fill", "none")
                    .attr("opacity", lineOpacity)
                ;
                var myCircles = svg.selectAll("circle." + class_name)
                    .data(uniqueValues[t], function (d, i) {
                        return d.chance + d.date;
                    }).enter();

                myCircles.append("circle")
                    .attr("r", radius)
                    .classed(class_name, true)
                    .attr("cx", function (d) {
                        return xScale(d.date);
                    })
                    .attr("cy", function (d) {
                        return hScale(d.chance);
                    })
                    .attr("opacity", function (d) {
                        return rScale(d.chance)
                    })
                    .style('fill', function (d) {
                        return uniqueColors[uniqueLabels.indexOf(t)];
                    })
                    .on("mouseover", function (d, i) {
                        var tText = "<span><strong>" + d.topicName + "</strong> (" +
                            (100 * d.chance).toString().substring(0, 5) + "%)<br/><small>" +
                            (1 + d.date.getDay()) + "/" + (1 + d.date.getMonth()) + "/" + (1900 + d.date.getYear()) +
                            "</small></span>";

                        d3.select(this).transition().duration(iTransitionDuration)
                            .attr("r", 1.3 * radius)
                        ;
                        tooltip
                            .style("opacity", 1.0)
                            .html(tText);
                    })
                    .on("mouseout", function (d) {
                        d3.select(this).transition().duration(iTransitionDuration)
                            .attr("r", radius)
                        ;
                        tooltip.style("opacity", 0.0);
                    })
            }

            // add legend
            var legend = svg.append("g")
                .attr("class", "legend")
                .attr("height", 100)
                .attr("width", 100)
                .attr('transform', 'translate(0,50)');

            legend.selectAll('rect')
                .data(uniqueLabels)
                .enter()
                .append("rect")
                .attr("x", inference.settings.width - 65)
                .attr("y", function (d, i) {
                    return i * 20;
                })
                .attr("width", 10)
                .attr("height", 10)
                .style("fill", function (d, i) {
                    return uniqueColors[i];
                });

            legend.selectAll('text')
                .data(uniqueLabels)
                .enter()
                .append("text")
                .attr("x", inference.settings.width - 52)
                .attr("y", function (d, i) {
                    return i * 20 + 9;
                })
                .text(function (d, i) {
                    return uniqueLabels[i];
                })
                .on("mouseover", function (d, i) {
                    for (var k in uniqueLabels) {
                        var class_name = uniqueLabels[k].toLowerCase().replace(" ", "");
                        var elem = d3.select("." + class_name).transition().duration(iTransitionDuration);
                        if (k == i) {
                            elem.attr("opacity", 1)
                                .attr("r", 1.3*radius)
                        } else {
                            elem.attr("opacity", 0)
                                .attr("r", 0)
                        }
                    }
                })
                .on("mouseout", function (d, i) {
                    for (var k in uniqueLabels) {
                        var class_name = uniqueLabels[k].toLowerCase().replace(" ", "");
                        d3.select("." + class_name).transition().duration(iTransitionDuration)
                            .attr("opacity", lineOpacity)
                            .attr("r", radius)

                        ;
                    }
                })
            ;

        };


        this.init();
    };

    $.fn.inference = function (options) {
        return this.each(function () {
            // if plugin has not already been attached to the element
            if (undefined == $(this).data('inference')) {
                var inference = new $.inference(this, options);
                $(this).data('inference', inference);
            }
        });
    };
})(jQuery);
