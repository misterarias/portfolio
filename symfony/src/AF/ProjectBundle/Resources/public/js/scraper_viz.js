/**
 * Created by juanito on 5/06/15.
 */
var numbers = [
    24, 9, 3, 22, 29, 11, 26, 11, 8, 19,
    14, 11, 22, 29, 11, 13, 12, 17, 18, 10,
    25, 7, 5, 26, 11, 8, 25, 14, 23, 19
];
var numbersSecond = [
    14, 11, 22, 29, 11, 13, 12, 17, 18, 10,
    25, 7, 5, 26, 11, 8, 25, 14, 23, 19,
    24, 9, 3, 22, 29, 11, 26, 11, 8, 19];
var randomData = randomDataset(numbers.length);
var currentDataset = numbers,
    width = $('#main_graph').width(),
    margin = 20,
    height = 240;

function randomDataset(dataLen) {
    var data = [];
    for (i = 0; i < dataLen; i++) {
        data[i] = Math.floor(maximumValue * Math.random());
    }
    return data;
}

var maximumValue = 30;
var xScale = d3.scale.linear().domain([0, numbers.length]).range([margin, width - margin]).clamp(true);
var cScale = d3.scale.linear().domain([0, maximumValue]).range(['blue', 'red']);


var svg = d3.select("#main_graph").append("svg")
    .classed("canvas", true)
    .attr("width", width)
    .attr("height", height);

// Controls
d3.select("#primero").on("click", function () {
    currentDataset = numbers;
    redraw();
    return false;
});
d3.select("#segundo").on("click", function () {
    currentDataset = numbersSecond;
    redraw();
    return false;
});
d3.select("#random").on("click", function () {
    randomData = randomDataset(numbers.length);
    currentDataset = randomData;
    redraw();
    return false;
});

var barScale = 2.0;
var lastTimestamp = 0
$("#barScale").slider({
    min: 500, max: 2000, value: 1000, step: 1,
    formatter: function (value) {
        return 'Current scale: ' + value / 1000;
    }
}).on("slide", function (_ev) {
    var newTs = _ev.timeStamp;
    if (newTs - lastTimestamp < 1000) {
        return;
    }
    lastTimestamp = newTs;

    barScale = _ev.value / 1000; // scaled
    redraw();
    return false;
}).on("change", function (_ev) {
    var newTs = _ev.timeStamp;
    if (newTs - lastTimestamp < 1000) {
        return;
    }
    lastTimestamp = newTs;

    barScale = _ev.value.newValue / 1000; // scaled
    redraw();
    return false;
})
;

redraw();

function redraw() {
    var data = currentDataset;
    var myBars = svg.selectAll("rect").data(data);
    var barWidth = ((width - 2 * margin) / data.length) / barScale;
    var hScale = d3.scale.linear().domain([0, barScale * maximumValue]).range([margin, height - margin]).clamp(true);

    function barHeigth(value) {
        return height -hScale(value);
    }

    myBars.enter().append("rect")
        .attr("x", function (d, i) {
            return xScale(i);
        })
        .attr("y", function (d) {
            return barHeigth(d);
        })
        .attr("width", barWidth)
        .attr("height", function (d, i) {
            return hScale(d);
        })
        .style('fill', function (d, i) {
            return cScale(i);
        })
    ;

    myBars.transition().duration(2000)
        .style('fill', function (d, i) {
            return cScale(d);
        })

        .attr("height", function (d, i) {
            return hScale(d);
        })
        .attr("y", function (d) {
            return barHeigth(d);
        })
    ;

    var myTexts = svg.selectAll("text").data(data);
    myTexts.exit().remove();

    myTexts.transition().duration(2000)
        .attr("y", function (d) {
            return barHeigth(d) - 10;
        })
        .text(function (d) {
            return d;
        });

    myTexts.enter().append("text")
        .attr('class', 'texto')
        .attr('text-anchor', 'left')
        .attr("x", function (d, i) {
            return xScale(i); // Only with thinner bars+ barWidth / 2;
        })
        .attr("y", function (d) {
            return barHeigth(d) - 10;
        })
        .text(function (d) {
            return d;
        });
}
