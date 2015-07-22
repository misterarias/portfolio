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
        var currentDataset = null;
        var maximumDate;
        var minimumDate;

        inference.setCurrentDataset = function (dataset) {
            currentDataset = dataset;

            minimumDate = new Date(2100, 12, 31);
            maximumDate = new Date(1970, 1, 1);
            var dateLen = currentDataset.length;
            for (var i = 0; i < dateLen; i++) {
                var date = new Date(currentDataset[i].date);
                currentDataset[i].date = date;
                if (maximumDate < date) {
                    maximumDate = date;
                }
                if (minimumDate > date) {
                    minimumDate = date;
                }
            }

            redraw();
        };

        inference.init = function () {
            inference.settings = $.extend({}, defaults, options);
        };
        var redraw = function () {

            // this, with inverted hscale, makes bars fall from the top
            function barHeigth(value) {
                return inference.settings.height - inference.settings.padding - hScale(value);
            }

            function barYPos(value) {
                return hScale(value);
            }

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
            var data = currentDataset,
                iTransitionDuration = 400,
                xScale = d3.time.scale()
                    .domain([minimumDate, maximumDate])
                    .rangeRound([inference.settings.padding,
                        inference.settings.width - 2 * inference.settings.padding]),
                hScale = d3.scale.linear()
                    .domain([0, 1])
                    .range(
                    [inference.settings.height - inference.settings.padding,
                        inference.settings.padding]
                ),
                yAxis = d3.svg.axis()
                    .orient("left")
                    .scale(hScale)
                    .ticks(7),
                rScale = d3.scale.pow().domain([0, 1]).range([0.4, 0.9]),
                radius = 10, // px
                cScale = d3.scale.linear().domain([0, 1]).range(['blue', 'red']),
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

            var svg;
            if ($("svg.inference").length == 0) {
                // Create and configure the canvas
                svg = container.append("svg")
                    .classed("canvas", true)
                    .classed("inference", true)
                    .attr("width", inference.settings.width)
                    .attr("height", inference.settings.height)
                ;

                // Append axis and events
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

            } else {
                // It already exists!! Resize in case settings haave changed
                svg = container.select("svg.inference")
                    .attr("width", inference.settings.width)
                    .attr("height", inference.settings.height)
                ;
            }
            // Append axis and events
            svg.on("mousemove", mousemove);

            var myBars = svg.selectAll("circle")
                .data(data, function (item) {
                    return item.date + item.chance;
                });

            myBars.exit().transition().duration(iTransitionDuration)
                .attr("r", 0)
                .attr("y", inference.settings.height)
            ;

            myBars.enter().append("circle")
                .attr("r", radius)
                .attr("cx", function (d) {
                    return xScale(d.date);
                })
                .attr("cy", function (d) {
                    return barYPos(d.chance);
                })
                .attr("opacity", function (d) {
                    return rScale(d.chance)
                })
                .style('fill', function (d) {
                    return cScale(d.chance);
                })
                .on("mouseover", function (d, i) {
                    var tText = "<span>" + (100 * d.chance).toString().substring(0, 8) +
                        " % of docs on <strong>" +
                        (1 + d.date.getDay()) + "/" + (1+d.date.getMonth()) + "/" + (1900 + d.date.getYear())
                    "</strong> </span>";

                    d3.select(this)
                        .attr("opacity", function (d) {
                            return rScale(d.chance) * 2
                        });
                    tooltip
                        .style("opacity", 1.0)
                        .html(tText);
                })
                .on("mouseout", function (d) {
                    d3.select(this)
                        .attr("opacity", function (d) {
                            return rScale(d.chance)
                        });
                    tooltip.style("opacity", 0.0);
                })
            ;

            myBars.transition().duration(iTransitionDuration)
                .attr("r", radius)
                .style('fill', function (d) {
                    return cScale(d.chance);
                })
                .attr("cx", function (d) {
                    return xScale(d.date);
                })
                .attr("opacity", function (d) {
                    return rScale(d.chance)
                })
                .attr("cy", function (d) {
                    return barYPos(d.chance);
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
