/**
 * Created by Ayugi on 08.01.2015.
 */
    // Prepare X-edit
$(document).ready(function() {
    //toggle `popup` / `inline` mode
//        $.fn.editable.defaults.mode = 'inline';

    // make devicename editable
//        $('#devicename').editable();

    // prepare bootstrap switch
    $("[name='jan9']").bootstrapSwitch();

    $('#dp5').slider();
    $('#dp6').slider();
});