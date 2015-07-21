/**
 * Setup and describe the visualization
 * Created by juanito on 5/06/15.
 */
(function ($) {
    "use strict";

    $.lda = function (element, options) {
        var defaults = {
            /** Reset to parent element width on every redraw */
            width: 700,
            /** Height of the graph */
            height: 250,
            /** Margin used to offset bar height and graph dimensions */
            margin: 20,
            /** Used to scale bar width */
            barScale: 1.0,
            /** Minimum ms between slider events */
            minInterval : 500
    };
        var $element = $(element), // reference to the jQuery version of DOM element
            element = element;    // reference to the actual DOM element
        var lda = this; // me
        var currentDataset = null;
        var maximumValue;
        var minimumValue;

        lda.setCurrentDataset = function (dataset) {
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

        lda.init = function () {
            lda.settings = $.extend({}, defaults, options);
        };

        var redraw = function () {


            // this, with inverted hscale, makes bars fall from the top
            function barHeigth(value) {
                return lda.settings.height - hScale(value);
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
            lda.settings.width = $(element).width();

            var container = d3.select(element)
                    .attr("width", lda.settings.width)
                    .attr("height", lda.settings.height)
                ;

            var svg;
            if ($("svg").length == 0) {
                svg = container.append("svg")
                    .classed("canvas", true)
                    .attr("width", lda.settings.width)
                    .attr("height", lda.settings.height)
                ;
            } else {
                svg = container.select("svg")
                    .attr("width", lda.settings.width)
                    .attr("height", lda.settings.height)
                ;
            }
            svg.on("mousemove", mousemove); // important!!!

            // Apply settings
            var data = currentDataset,
                adjust = 0.05, // % to adjust domains
                iTransitionDuration = 1800,
                xScale = d3.scale.linear()
                    .domain([0, data.length])
                    .range([0, lda.settings.width]),
                hScale = d3.scale.linear()
                    .domain([(1-adjust) * minimumValue, maximumValue * (1+adjust)])
                    .range([ lda.settings.height - lda.settings.margin,  -  lda.settings.margin]),
                cScale = d3.scale.linear()
                    .domain([(1-adjust) * minimumValue, maximumValue * (1+adjust)])
                    .range(['blue', 'red']),
                barWidth = ((lda.settings.width - 2 * lda.settings.margin) / data.length) / lda.settings.barScale;

            var myBars = svg.selectAll("rect")
                .data(data);

            myBars.exit().transition().duration(iTransitionDuration)
                .attr("opacity", 0)
                .attr("y", lda.settings.height)
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
                    tText += "<span>Weight <strong>" + (100*d.weight).toString().substring(0,8) + "</strong> %</span>";

                    tooltip
                        .style("opacity", 1.0)
                        .html(tText);
                })
                .on("mouseout", function (d) {
                    tooltip
                        .style("opacity", 0.0);
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

    $.fn.lda = function (options) {
        return this.each(function () {
            // if plugin has not already been attached to the element
            if (undefined == $(this).data('lda')) {
                var lda = new $.lda(this, options);
                $(this).data('lda', lda);
            }
        });
    };
})(jQuery);