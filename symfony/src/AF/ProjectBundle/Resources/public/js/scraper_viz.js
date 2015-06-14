/**
 * Setup and describe the visualization
 * Created by juanito on 5/06/15.
 */
(function ($) {
    "use strict";

    $.viz = function (element, options) {
        var defaults = {
            /** Reset to parent element width on every redraw */
            width: 700,
            /** Height of the graph */
            height: 250,
            /** Margin used to offset bar height and grapg dimensions */
            margin: 20,
            /** Used to scale bar width */
            barScale: 2.0,
            /** upper bound of elements to show */
            maxSize: 50,
            /** lower bound of elements to show */
            minSize: 0,
            /** scale options */
            clamp: true,
            nice: true
        };
        var currentDataset = null;
        var maximumValue = 99, minimumValue = -50;

        var $element = $(element), // reference to the jQuery version of DOM element
            element = element;    // reference to the actual DOM element
        var viz = this; // me

        viz.setCurrentDataset = function (dataset) {
            currentDataset = dataset;

            // Cannot use d3.max since this is an object
            maximumValue = -999.99;
            minimumValue = 999.99;
            currentDataset.forEach(function (d) {
                var fVal = parseFloat(d.temp);
                if (maximumValue < fVal) {
                    maximumValue = fVal;
                }
                if (minimumValue > fVal) {
                    minimumValue = fVal;
                }
            });
            redraw();
        };

        viz.init = function () {
            viz.settings = $.extend({}, defaults, options);

            $("#barScale").slider({
                value: viz.settings.barScale * 1000, step: 1,
                formatter: function (value) {
                    return 'Current scale: ' + value / 1000;
                }
            }).on("slide", function (_ev) {
                if (spamControl()) {
                    viz.settings.barScale = _ev.value / 1000; // scaled
                    redraw();
                }
                return false;
            }).on("change", function (_ev) {
                if (spamControl()) {
                    viz.settings.barScale = _ev.value.newValue / 1000; // scaled
                    redraw();
                }
                return false;
            });
            $("#marginValue").slider({
                value: viz.settings.margin, step: 1,
                formatter: function (value) {
                    return 'Current margin: ' + value;
                }
            }).on("slide", function (_ev) {
                if (spamControl()) {
                    viz.settings.margin = _ev.value;
                    redraw();
                }
                return false;
            }).on("change", function (_ev) {
                if (spamControl()) {
                    viz.settings.margin = _ev.value.newValue;
                    redraw();
                }
                return false;
            });
            $("#heightScale").slider({
                value: viz.settings.height, step: 1,
                formatter: function (value) {
                    return 'Current height: ' + value;
                }
            }).on("slide", function (_ev) {
                if (spamControl()) {
                    viz.settings.height = _ev.value;
                    this.redraw();
                }
                return false;
            }).on("change", function (_ev) {
                if (spamControl()) {
                    viz.settings.height = _ev.value.newValue;
                    redraw();
                }
                return false;
            });
            $("#sizeScale").slider({
                value: [viz.settings.minSize, viz.settings.maxSize], step: 1,
                formatter: function (value) {
                    if (currentDataset != undefined) {
                        var first = Math.floor(currentDataset.length * viz.settings.minSize / 100),
                            last = Math.floor(currentDataset.length * viz.settings.maxSize / 100);
                        return 'From ' + first + ' to ' + last;
                    } else {
                        return "No dataset selected";
                    }
                }
            }).on("change", function (_ev) {
                if (spamControl()) {
                    viz.settings.minSize = _ev.value.newValue[0];
                    viz.settings.maxSize = _ev.value.newValue[1];
                    redraw();
                }
                return false;
            });

            // Misc. Scale options
            $("#clamp").on("change", function (_ev) {
                if (spamControl()) {
                    viz.settings.clamp = $("#clamp")[0].checked;
                    redraw();
                }
                return false;
            });
            $("#nice").on("change", function (_ev) {
                if (spamControl()) {
                    viz.settings.nice = $("#nice")[0].checked;
                    redraw();
                }
                return false;
            });
        };

        var redraw = function () {

            // this, with inverted hscale, makes bars fall from the top
            function barHeigth(value) {
                return viz.settings.height - hScale(value);
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
            viz.settings.width = $(element).width();

            var container = d3.select(element)
                    .attr("width", viz.settings.width)
                    .attr("height", viz.settings.height)
                ;

            if ($("svg").length == 0) {
                var svg = container.append("svg")
                        .classed("canvas", true)
                        .attr("width", viz.settings.width)
                        .attr("height", viz.settings.height)
                    ;
            } else {
                var svg = container.select("svg")
                        .attr("width", viz.settings.width)
                        .attr("height", viz.settings.height)
                    ;
            }
            svg.on("mousemove", mousemove); // important!!!

            // Apply settings
            var data = currentDataset.slice(
                    Math.floor(currentDataset.length * viz.settings.minSize / 100),
                    Math.floor(currentDataset.length * viz.settings.maxSize / 100)),
                iTransitionDuration = 1800,
                xScale = d3.scale.linear()
                    .domain([0, data.length])
                    .range([0, viz.settings.width]),
                hScale = d3.scale.linear()
                    .domain([0.8 * minimumValue, maximumValue * 1.2]) // +- 20%
                    .range([ viz.settings.height - viz.settings.margin,  -  viz.settings.margin]),
                cScale = d3.scale.linear()
                    .domain([0.8 * minimumValue, maximumValue * 1.2]) // +- 20%
                    .range(['blue', 'red']),
                barWidth = ((viz.settings.width - 2 * viz.settings.margin) / data.length) / viz.settings.barScale;

            xScale.clamp(viz.settings.clamp);
            hScale.clamp(viz.settings.clamp);
            cScale.clamp(viz.settings.clamp);
            if (viz.settings.nice == true) {
                xScale.nice();
                hScale.nice();
                cScale.nice();
            }

            var myBars = svg.selectAll("rect")
                .data(data);

            myBars.exit().transition().duration(iTransitionDuration)
                .attr("opacity", 0)
                .attr("y", viz.settings.height)
            ;

            myBars.enter().append("rect")
                .attr("opacity", 1)
                .attr("x", function (d, i) {
                    return xScale(i);
                })
                .attr("y", function (d) {
                    return barYPos(d.temp);
                })
                .attr("width", barWidth)
                .attr("height", function (d) {
                    return barHeigth(d.temp);
                })
                .style('fill', function (d, i) {
                    return cScale(d.temp);
                })
                .on("mouseover", function (d, i) {
                    var tText = "<span>ISO code: <strong>" + d.country + "</strong></span><br/>";
                    tText += "<span>Avg. temp <strong>" + d.temp + "</strong> ºC</span>";

                    tooltip
                        .style("opacity", 1.0)
                        .html(tText);
                })
                .on("mouseout", function (d) {
                    tooltip.transition().duration(300)
                        .style("opacity", 0.0);
                });

            myBars.transition().duration(iTransitionDuration)
                .attr("opacity", 1)
                .style('fill', function (d) {
                    return cScale(d.temp);
                })
                .attr("x", function (d, i) {
                    return xScale(i);
                })
                .attr("width", barWidth)
                .attr("height", function (d) {
                    return barHeigth(d.temp);
                })
                .attr("y", function (d) {
                    return barYPos(d.temp);
                })
            ;
        };

        this.init();
    }

    $.fn.viz = function (options) {
        return this.each(function () {
            // if plugin has not already been attached to the element
            if (undefined == $(this).data('viz')) {
                var viz = new $.viz(this, options);
                $(this).data('viz', viz);
            }
        });
    };
})(jQuery);