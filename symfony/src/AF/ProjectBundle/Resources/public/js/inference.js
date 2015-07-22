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
            /** Used to scale bar width */
            barScale: 0.87,
            /** Minimum ms between slider events */
            minInterval: 500
        };
        var $element = $(element), // reference to the jQuery version of DOM element
            element = element;    // reference to the actual DOM element
        var inference = this; // me
        var currentDataset = null;
        var maximumValue = 1.0;
        var minimumValue = 0.0;

        inference.setCurrentDataset = function (dataset) {
            currentDataset = dataset;
            redraw();
        };

        inference.init = function () {
            inference.settings = $.extend({}, defaults, options);
        };
        var redraw = function () {

            // display date format
            var date_format = d3.time.format("%d %b");

            // Setup our canvas, if not there yet
            inference.settings.width = $(element).width();
            var data = currentDataset,
                adjust = 0.08, // % to adjust domains
                iTransitionDuration = 1800,

                xScale = d3.time.scale()
                    .domain(d3.extent(data, function (k) {
                        return k;
                    }))
                    .range([inference.settings.padding, inference.settings.width - 2 * inference.settings.padding]),

                hScale = d3.scale.linear()
                    .domain([(1 - adjust) * minimumValue, maximumValue * (1 + adjust)])
                    .range([inference.settings.height - inference.settings.padding, inference.settings.padding]),
                yAxis = d3.svg.axis()
                    .orient("left")
                    .scale(hScale)
                    .ticks(7),
                xAxis = d3.svg.axis()
                    .orient("bottom")
                    .scale(xScale)
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

                svg.selectAll("line.horizontalGrid").data(hScale.ticks(7)).enter()
                    .append("line")
                    .attr(
                    {
                        "class": "horizontalGrid",
                        "x1": inference.settings.padding,
                        "x2": inference.settings.width - 2 * inference.settings.padding,
                        "y1": function (d) {
                            return hScale(d);
                        },
                        "y2": function (d) {
                            return hScale(d);
                        }
                    });

            } else {
                // It alerady exists!! Resize in case settings haave changed
                svg = container.select("svg.inference")
                    .attr("width", inference.settings.width)
                    .attr("height", inference.settings.height)
                ;
            }

            /*   var myBars = svg.selectAll("rect")
             .data(data, function (item) {
             return item.topic;
             });

             myBars.exit().transition().duration(iTransitionDuration)
             .attr("opacity", 0)
             .attr("y", inference.settings.height)
             ;

             myBars.enter().append("rect")
             .attr("opacity", 1)
             .attr("x", function (d, i) {
             return xScale(i);
             })
             .attr("y", function (d) {
             return barYPos(d.chance);
             })
             .attr("width", barWidth)
             .attr("height", function (d) {
             return barHeigth(d.chance);
             })
             .style('fill', function (d) {
             return cScale(d.chance);
             })
             .on("mouseover", function (d, i) {
             var tText = "<span>Term: <strong>" + d.term + "</strong></span><br/>";
             tText += "<span>Weight <strong>" + (100 * d.chance).toString().substring(0, 8) + "</strong> %</span>";

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
             return cScale(d.chance);
             })
             .attr("x", function (d, i) {
             return xScale(i);
             })
             .attr("width", barWidth)
             .attr("height", function (d) {
             return barHeigth(d.chance);
             })
             .attr("y", function (d) {
             return barYPos(d.chance);
             })
             ;*/
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
