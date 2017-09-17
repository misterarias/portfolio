/**
 * Event management functions
 *
 * Created by juanito on 14/06/15.
 */

var current_month = "Jan";
var months = [
    {key: "Jan", label: "January"},
    {key: "Feb", label: "February"},
    {key: "Mar", label: "March"},
    {key: "Apr", label: "April"},
    {key: "May", label: "May"},
    {key: "Jun", label: "June"},
    {key: "July", label: "July"},
    {key: "Aug", label: "August"},
    {key: "Sept", label: "September"},
    {key: "Oct", label: "October"},
    {key: "Nov", label: "November"},
    {key: "Dec", label: "December"}
];
$(document).ready(function populateDatasets() {
    "use strict";
    console.log("On load");


    // I want to thank Symfony 2 for making it so "easy" to serve a static file
    d3.csv($("#data_locator").val(), function (data) {
        data.forEach(function (item) {
            var country = item['ISO_3DIGIT'];
            for (var m in months) {
                var current_month = months[m];

                // Super consistent dataset :(
                var temp = item[current_month.key + "_temp"];
                if (temp == undefined) {
                    temp = item[current_month.key + "_Temp"];
                }

                // Add to dataset
                if (csv_data[current_month.key] == undefined) {
                    csv_data[current_month.key] = [];
                }
                csv_data[current_month.key].push({country: country, temp: temp});
            }

        });


        // Now that we've got data, populate controls
        var ds = $('#dataset');
        months
            .forEach(function (month) {
                var option = new Option();
                option.id = month.key;
                option.innerHTML = month.label;
                ds.append(option);
            });

        $("#main_graph").viz({
            width: $("#main_graph").width(),
            height: 300,
            clamp: $("#clamp")[0].checked,
            nice: $("#nice")[0].checked
        });
        ds.on("change", function (ev) {
            var month_key = months[this.selectedIndex].key
            $("#dataset_date").text(months[this.selectedIndex].label + " 1961-1999");
            $("#main_graph").data('viz').setCurrentDataset(csv_data[month_key]);
        }).trigger("change");
    });

});
