/**
 * Created by Ayugi on 08.01.2015.
 */
    // Prepare X-edit
$(document).ready(function () {
    //toggle `popup` / `inline` mode
//        $.fn.editable.defaults.mode = 'inline';

    // make devicename editable
//        $('#devicename').editable();

    // prepare bootstrap switch
    $("[name='jan9']").bootstrapSwitch();

    $('#dp5').slider();
    $('#dp6').slider();

    var cw1 =
        Raphael.colorwheel($("#cw1"), 250, 180).color("#00F");

    cw1.onchange(function (color) {
        var colors = [parseInt(color.r), parseInt(color.g), parseInt(color.b)]
        $("#color-mon").css("background", color.hex).text("RGB: " + colors.join(", "))
    })
});