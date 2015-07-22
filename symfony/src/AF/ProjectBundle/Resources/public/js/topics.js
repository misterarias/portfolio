/**
 * Setup and describe a Terms per topic visualization
 * Created by juanito on 5/06/15.
 */
(function ($) {
    "use strict";

    $.topics = function (element, options) {
        var defaults = {
            /** Reset to parent element width on every redraw */
            width: 700,
            /** Height of the graph */
            height: 250,
            /** Padding used to offset graph on the edges */
            padding: 40,
            /** Used to scale bar width */
            barScale: 0.87,
            /** Minimum ms between slider events */
            minInterval: 500
        };
        var $element = $(element), // reference to the jQuery version of DOM element
            element = element;    // reference to the actual DOM element
        var topics = this; // me
        var currentDataset = null;
        var maximumValue;
        var minimumValue;

        topics.setCurrentDataset = function (dataset) {
            currentDataset = dataset;
            // Cannot use d3.max since this is an object

            maximumValue = -999.99;
            minimumValue = 999.99;
            currentDataset.forEach(function (d) {
                var fVal = parseFloat(d.weight);
                if (maximumValue < fVal) {
                    maximumValue = fVal;
                }
                if (minimumValue > fVal) {
                    minimumValue = fVal;
                }
            });
            redraw();
        };

        topics.init = function () {
            topics.settings = $.extend({}, defaults, options);
        };
        var redraw = function () {

            // this, with inverted hscale, makes bars fall from the top
            function barHeigth(value) {
                return topics.settings.height - topics.settings.padding - hScale(value);
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

            // Setup our canvas, if not there yet
            topics.settings.width = $(element).width();
            var data = currentDataset,
                adjust = 0.08, // % to adjust domains
                iTransitionDuration = 1000,
                xScale = d3.scale.linear()
                    .domain([0, data.length])
                    .range([topics.settings.padding, topics.settings.width - 2 * topics.settings.padding]),
                hScale = d3.scale.linear()
                    .domain([(1 - adjust) * minimumValue, maximumValue * (1 + adjust)])
                    .range([topics.settings.height - topics.settings.padding, topics.settings.padding]),
                cScale = d3.scale.pow()
                    .domain([(1 - adjust) * minimumValue, maximumValue * (1 + adjust)])
                    .range(['blue', 'red']),
                barWidth = ((topics.settings.width - topics.settings.padding) / data.length) * topics.settings.barScale,
                yAxis = d3.svg.axis()
                    .orient("left")
                    .scale(hScale)
                    .ticks(7)
                    .tickFormat(d3.format(".1%")),

                xAxis = d3.svg.axis()
                    .orient("bottom")
                    .ticks(currentDataset.length)
                    .scale(xScale)
                    .tickFormat("")
                ;

            var container = d3.select(element)
                    .attr("width", topics.settings.width)
                    .attr("height", topics.settings.height)
                ;

            var svg;
            if ($("svg.topics").length == 0) {
                // Create and configure the canvas
                svg = container.append("svg")
                    .classed("canvas", true)
                    .classed("topics", true)
                    .attr("width", topics.settings.width)
                    .attr("height", topics.settings.height)
                ;

                svg.append("g")
                    .attr("class", "xaxis")   // give it a class so it can be used to select only xaxis labels  below
                    .attr("transform",
                    "translate(0," + (topics.settings.height - topics.settings.padding).toString() + ")")
                    .call(xAxis)
                ;


                svg.append("g")
                    .attr("class", "yaxis")
                    .attr("transform", "translate(" + topics.settings.padding.toString() + ",0)")
                    .call(yAxis)
                ;

            } else {
                // It already exists!! Resize in case settings haave changed
                svg = container.select("svg.topics")
                    .attr("width", topics.settings.width)
                    .attr("height", topics.settings.height)
                ;
            }

            // Append axis and events
            svg.on("mousemove", mousemove);

            var lines = svg.selectAll("line.horizontalGrid").data(hScale.ticks(7));
            lines.exit()
                .transition().duration(iTransitionDuration)
                .attr("opacity", 0)
                .transition().duration(iTransitionDuration)
                .remove();
            lines.enter()
                .append("line")
                .attr(
                {
                    "class": "horizontalGrid",
                    "x1": topics.settings.padding,
                    "x2": topics.settings.width - 2 * topics.settings.padding,
                    "y1": function (d) {
                        return hScale(d);
                    },
                    "y2": function (d) {
                        return hScale(d);
                    }
                });

            lines.exit().remove()
                .transition().duration(iTransitionDuration)
                .call(yAxis);

            svg.select(".yaxis")
                .transition().duration(iTransitionDuration).ease("sin-in-out")  // https://github.com/mbostock/d3/wiki/Transitions#wiki-d3_ease
                .call(yAxis);

            var myBars = svg.selectAll("rect")
                .data(data, function (item) {
                    return item.term;
                });

            myBars.exit().transition().duration(iTransitionDuration)
                .attr("opacity", 0)
                .attr("y", topics.settings.height)
            ;

            myBars.enter().append("rect")
                .attr("opacity", 1)
                .attr("x", function (d, i) {
                    return xScale(i);
                })
                .attr("y", function (d) {
                    return barYPos(d.weight);
                })
                .attr("width", barWidth)
                .attr("height", function (d) {
                    return barHeigth(d.weight);
                })
                .style('fill', function (d) {
                    return cScale(d.weight);
                })
                .on("mouseover", function (d, i) {
                    var tText = "<span>Term: <strong>" + d.term + "</strong></span><br/>";
                    tText += "<span>Weight <strong>" + (100 * d.weight).toString().substring(0, 8) + "</strong> %</span>";

                    tooltip
                        .style("opacity", 1.0)
                        .html(tText);
                })
                .on("mouseout", function (d) {
                    tooltip.style("opacity", 0.0);
                })
            ;

            myBars.transition().duration(iTransitionDuration)
                .attr("opacity", 1)
                .style('fill', function (d) {
                    return cScale(d.weight);
                })
                .attr("x", function (d, i) {
                    return xScale(i);
                })
                .attr("width", barWidth)
                .attr("height", function (d) {
                    return barHeigth(d.weight);
                })
                .attr("y", function (d) {
                    return barYPos(d.weight);
                })
            ;
        };


        this.init();
    };

    $.fn.topics = function (options) {
        return this.each(function () {
            // if plugin has not already been attached to the element
            if (undefined == $(this).data('topics')) {
                var topics = new $.topics(this, options);
                $(this).data('topics', topics);
            }
        });
    };
})(jQuery);
