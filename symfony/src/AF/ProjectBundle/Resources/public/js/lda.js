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
            /** Margin used to offset bar height and grapg dimensions */
            margin: 20,
            /** Used to scale bar width */
            barScale: 2.0,
            /** scale options */
            clamp: true,
            nice: true,
            /** Minimum ms between slider events */
            minInterval : 500
    };
        var $element = $(element), // reference to the jQuery version of DOM element
            element = element;    // reference to the actual DOM element
        var lda = this; // me
        var currentDataset = null;
        var lastTimestamp = 0;

        viz.setCurrentDataset = function (dataset) {
            currentDataset = dataset;
            redraw();
        };

        lda.init = function () {
            lda.settings = $.extend({}, defaults, options);
        };

        var redraw = function () {

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

            // Apply settings
            var data = currentDataset.slice(
                    Math.floor(currentDataset.length * lda.settings.minSize / 100),
                    Math.floor(currentDataset.length * lda.settings.maxSize / 100)),
                iTransitionDuration = 1800,
                xScale = d3.scale.linear()
                    .domain([0, data.length])
                    .range([0, lda.settings.width]),
                cScale = d3.scale.linear()
                    .domain([0, 1.0]) // +- 20%
                    .range(['blue', 'red']),
                barWidth = ((lda.settings.width - 2 * lda.settings.margin) / data.length) / lda.settings.barScale;

            xScale.clamp(lda.settings.clamp);
            cScale.clamp(lda.settings.clamp);
            if (lda.settings.nice == true) {
                xScale.nice();
                cScale.nice();
            }

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
                    return barYPos(d.temp);
                })
                .attr("width", barWidth)
                .attr("height", function (d) {
                    return barHeigth(d.temp);
                })
                .style('fill', function (d, i) {
                    return cScale(d.temp);
                })
            ;

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
    };


    /**
     * Do not let an event happen if a minimum threshold has not passed since last one
     * @param ts
     * @returns {boolean}
     */
    var spamControl = function (ts) {
        if (ts - lastTimestamp < lda.settings.minInterval) {
            return false;
        }
        lastTimestamp = ts;
        return true;
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