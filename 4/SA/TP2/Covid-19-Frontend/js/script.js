//Grafico lines
var ctxL = document.getElementById("lineChart").getContext('2d');

nrDeDiasTotal = 30;
nrDeDiasPrevisao = 7;
tipoDeGraficoSelecionado = "Total_Cases";

function makegraphic(graphicType, nrDays, countriesNameList, countriesKeyList){
    var globalMatrix = makePrevision(countriesKeyList);
    document.getElementById("graph-container").innerHTML = '&nbsp;';
    document.getElementById("graph-container").innerHTML = '<canvas id="lineChart"></canvas>';
    var ctxL = document.getElementById("lineChart").getContext("2d");

    var myLineChart = new Chart(ctxL, {
        type: 'line',
        data: {
            labels: getDays(1, nrDays, nrDeDiasPrevisao),
            datasets: graphicLines(graphicType, nrDays, countriesNameList, countriesKeyList, globalMatrix)
        },
        options: {
            responsive: true
        }
    });
}

//Desenha as linhas do gráfico
function graphicLines(graphicType, nrDays, countriesNameList, countriesKeyList, globalMatrix){
    var result = [];
    var algNameKey, algName, algKey;

    for(var i = 0; i < countriesKeyList.length; i++) {
        result.push({
            label: countriesNameList[i],
            data: graphicLine(graphicType,countriesKeyList[i],nrDays),
            backgroundColor: [
                'rgba(255, 255, 255, 0)',
            ],
            borderColor: [
                'rgba(0, 10, 130, .7)',
            ],
            borderWidth: 2
        });
    }

    var innerArrayLength = globalMatrix[0].length;
    for (var i = 1; i < globalMatrix.length; i++) {
        var row = [];
        for (var j = 0; j < innerArrayLength; j++) {
            var item = globalMatrix[i][j];
            if(item != undefined){
                row.push(item);
            }
        }
        algNameKey = row[0];
        algName = algNameKey.substring(0, algNameKey.length - 2);
        algKey = algNameKey.slice(-2);
        row.shift();

        for(var un=0; un<nrDays; un++) {
            row.unshift(null);
        }

        result.push({
            label: countries[algKey] + " - " + algName,
            data: row,
            backgroundColor: [
                'rgba(255, 255, 255, 0)',
            ],
            borderColor: [
                'rgba(200, 99, 132, .7)',
            ],
            borderWidth: 2
        });
    }

    return(result);
}

//buscar os resultados dos infetados
function graphicLine(graphicType, countryKey, nrDays) {
    var apiAccess = 'http://localhost:8000/countryHistory?Country=' + countryKey;
    var result = [];
    var dayData, total;

    $.ajax({
        url: apiAccess,
        async: false,
        dataType: 'json',
        success: function(data) {
            var days = getDays(2, nrDays, 0);

            for(let i = 0; i < nrDays; i++) {
                dayyy = days[i];
                dayData = data[0][dayyy];
                apiAccess2 = 'http://localhost:8000/countryData?Country=' + countryKey;

                if(graphicType == "Total_Cases") {
                  //  if(i != nrDays - 1) {
                        total = dayData.total_cases;
                  //  } else { //para conseguir ir buscar os dados de hoje
                  //      $.ajax({
                  //          url: apiAccess2,
                  //          async: false,
                  //          dataType: 'json',
                  //          success: function(data2) {
                  //              total = data2.total;
                  //          }
                  //      });
                  //  }
                } else if (graphicType == "Total_Deaths") {
                  //  if(i != nrDays - 1) {
                        total = dayData.total_deaths;
                  //  } else { //para conseguir ir buscar os dados de hoje
                  //      $.ajax({
                  //          url: apiAccess2,
                  //          async: false,
                  //          dataType: 'json',
                  //          success: function(data2) {
                  //              mortes = data2.deaths;
                  //          }
                  //      });
                  //  }
                } else if (graphicType == 'Total_Recoveries') {
                    total = dayData.total_recoveries;
                } else if (graphicType == 'New_Daily_Cases') {
                    total = dayData.new_daily_cases;
                } else if (graphicType == 'New_Daily_Deaths') {
                    total = dayData.new_daily_deaths;
                }

                result.push(total.toString());
            }
        }
    });

    return (result);
}
//fim

//pedido previsão e dá parse do csv
function makePrevision(countriesKeyList) {
    countriesKeyListComma = countriesKeyList.join(",");
    var apiAccess = 'http://localhost:5000/predictions?days=' + nrDeDiasPrevisao + '&field=' + tipoDeGraficoSelecionado + '&country=' + countriesKeyListComma;
    var matrix = [];

    jQuery.ajax({
        url: apiAccess,
        success: function(allText) {
            var allTextLines = allText.split(/\r\n|\n/);
            var headers = allTextLines[0].split(',');
            
            for(var j=0; j<headers.length; j++) {
                matrix[j] = new Array(allTextLines.length);
            }
        
            for (var i=0; i<allTextLines.length; i++) {
                var data = allTextLines[i].split(',');
                for (var j=0; j<headers.length; j++) {
                    matrix[j].push(data[j]);
                }
            }
        },
        async:false
      });

    return matrix;
}
//fim

function initialGraphic(){
    $.get("https://ipinfo.io", function(response) {
        visitorCountryKey = [];
        visitorCountryName = [];
        visitorCountryKey.push(response.country);
        visitorCountryName.push(countries[visitorCountryKey]);

        makegraphic(tipoDeGraficoSelecionado, nrDeDiasTotal, visitorCountryName, visitorCountryKey);
    }, "jsonp");
}

initialGraphic();

function changeGraphicType(type) {
    tipoDeGraficoSelecionado = type;
    updateDate();
}

function changeGraphicNrDays(nrDays) {
    nrDeDiasPrevisao = nrDays;
    updateDate();
}

$(".dropdown a").click(function(){
    $(this).parents(".dropdown").find('.btn').html($(this).text() + ' <span class="caret"></span>');
    $(this).parents(".dropdown").find('.btn').val($(this).data('value'));
  });

//array dos ultimos 7 dias ou 15 dias: formato portugues (type = 1), formato endpoint (type = 2)
function formatDate(type, date){
    var dd = date.getDate();
    var mm = date.getMonth()+1;
    var yyyy = date.getFullYear();

    if(type == 1) {
        if(dd<10) {dd='0'+dd}
        if(mm<10) {mm='0'+mm}
        date = dd+'/'+mm+'/'+yyyy;
    } else if(type == 2) {
        date = mm + "/" +
            (dd<10 ? '0' : '') + dd + '/' +
            yyyy.toString().substr(-2);
    }

    return date;
 }

function getDays(type, daysBack, daysAhead) {
    var result = [];

    for (var i=daysBack; i>0; i--) {
        var d = new Date();
        d.setDate(d.getDate() - i);

        result.push(formatDate(type, d));
    }

    for (var i=0; i<daysAhead; i++) {
        var d = new Date();
        d.setDate(d.getDate() + i);

        result.push(formatDate(type, d));
    }

    return(result);
}
//fim


// Data atual (Footer)
var d = new Date();

var month = d.getMonth()+1;
var day = d.getDate();

var todayDate = (day<10 ? '0' : '') + day + '/' +
                (month<10 ? '0' : '') + month + '/' +
                d.getFullYear();

$("#data").html(todayDate);

//Tabela dados países
$(document).ready(function () {
    $('#dtTable').DataTable({
        dom:"<'myfilter'f>tpi",
        "lengthChange": false,
        pageLength: 8,
        "aaSorting": [],
        columnDefs: [{
            orderable: false,
            targets: 0
        }],
        select: {
        style: 'os',
        selector: 'td:first-child'
        },
        "order": [[ 2, "desc" ]],
        language: {
            "sProcessing":    "Processando...",
            "sLengthMenu":    "Mostrar _MENU_ países",
            "sZeroRecords":   "Sem resultados",
            "sEmptyTable":    "Sem dados disponível",
            "sInfo":          "Mostrando _START_ até _END_ num total de _TOTAL_",
            "sInfoEmpty":     "Mostrando 0 a 0 em 0",
            "sInfoFiltered":  "(filtrado num total de _MAX_ países)",
            "sInfoPostFix":   "",
            "sSearch":        "Procurar:",
            "sUrl":           "",
            "sInfoThousands":  ",",
            "sLoadingRecords": "Carregando...",
            "oPaginate": {
                "sFirst": "Primeiro",
                "sLast": "Último",
                "sNext": "Seguinte",
                "sPrevious": "Anterior"
            },
            "oAria": {
                "sSortAscending":  ": Ativar para ordenar a columna de maneira ascendente",
                "sSortDescending": ": Ativar para ordenar a columna de maneira descendente"
            }
        }
    });
    $('.dataTables_length').addClass('bs-select');
});

$("#checkAll").click(function () {
    $(".check").prop('checked', $(this).prop('checked'));
});


// Preencher tabela total países
var countries;

$.ajax({
    url: "http://localhost:8000/countries",
    async: false,
    dataType: 'json',
    success: function(data) {
        countries = data;
    }
});

var totalNumber;
var tbody = '', rowHTML = '';
var casesCountry = {};

$('#progress').show();


for (countryKey in countries) {
    var countryName = countries[countryKey];
    var apiAccess = 'http://localhost:8000/countryData?Country=' + countryKey;

    $.ajax({
        url: apiAccess,
        async: false,
        dataType: 'json',
        success: function(data) {
            totalNumber = data.total;
            curedNumber = data.cured;
            deathsNumber = data.deaths;
        }
    });

    rowHTML += '<td><div class="checkbox"><label><input type="checkbox" class="check" onchange="updateDate()" id="' + countryKey + '"></label></div></td>';
    rowHTML += '<td class="align-middle">' + countryName + '</td>';
    rowHTML += '<td class="align-middle">' + totalNumber + '</td>';

    tbody += '<tr class="m-0">' + rowHTML + '</tr>';
    rowHTML = '';

    var key = countryKey
    if (!casesCountry[key]) {
        casesCountry[key] = {}
    }
    casesCountry[key].value = totalNumber
    casesCountry[key].attrs = { "href": "#" }
    casesCountry[key].tooltip = { "content": "<strong>"+countryName+"</strong><br/>Casos: "+totalNumber+"</br>Recuperados: "+curedNumber+"</br>Mortos: "+deathsNumber+"</br>"}
}

$('#progress').hide();
$('#page').show()

document.getElementById('table-data').innerHTML = tbody;
// Fim Preencher tabela total países


//Function activated when checklist is clicked
function updateDate() {
    var arr = $('input:checkbox.check:checked').map(function () {
        return this.id;
    }).get();

    //se não tiver nada selecionado, mostra os dados gerais
    if(arr.length == 0) {
        $.getJSON('http://localhost:8000/overallData', function(data) {
            $("#totalGlobal").html(data.total);
            $("#hojeGlobal").html(data.newToday);
            $("#curedGlobal").html(data.cured);
            $("#deathsGlobal").html(data.deaths);
        });

        initialGraphic();
    } else {
        var totalInfetados = 0, maisHoje = 0, recuperados = 0, mortes = 0;
        var countryKeyArr, apiAccess, countriesNames = [];

        for(countryKeyIdArr in arr){
            countryKeyArr = arr[countryKeyIdArr];
            countriesNames.push(countries[countryKeyArr]);

            //para acumular checklists
            if (countryKeyArr != "checkAll"){
                apiAccess = 'http://localhost:8000/countryData?Country=' + countryKeyArr;

                $.ajax({
                    url: apiAccess,
                    async: false,
                    dataType: 'json',
                    success: function(data) {
                        totalInfetados += data.total;
                        maisHoje += data.newToday;
                        recuperados += data.cured;
                        mortes += data.deaths;
                    }
                });

            }
        }

        makegraphic(tipoDeGraficoSelecionado, nrDeDiasTotal, countriesNames, arr);

        $("#totalGlobal").html(totalInfetados);
        $("#hojeGlobal").html(maisHoje);
        $("#curedGlobal").html(recuperados);
        $("#deathsGlobal").html(mortes);
    }
  }

// Tabela dos Dados globais (default)
$.getJSON('http://localhost:8000/overallData', function(data) {
    $("#totalGlobal").html(data.total);
    $("#hojeGlobal").html(data.newToday);
    $("#curedGlobal").html(data.cured);
    $("#deathsGlobal").html(data.deaths);
});

//Colors for map cases
var caseLegendAreaColors = [
    {
        min: 100000,
        attrs: {
            fill: "#2A002A"
        }
    },
    {
        min: 10000,
        max: 99999, 
        attrs: {
            fill: "#800080"
        }
    },
    {
        min: 1000,
        max: 9999, 
        attrs: {
            fill: "#9932CC"
        }
    },
    {
        min: 100,
        max: 999, 
        attrs: {
            fill: "#DA70D6"
        }
    },
    {
        min: 10,
        max: 99, 
        attrs: {
            fill: "#DDA0DD"
        }
    },
    {
        min: 1,
        max: 9, 
        attrs: {
            fill: "#EE82EE"
        }
    },
    {
        max: 0, 
        attrs: {
            fill: "#D8BFD8"
        }
    }
]

//Show map with colors of cases
$(function(){
    $(".cases").mapael({
        map: {
            name : "world_countries_miller",
            zoom: {
                enabled: true,
                maxLevel: 10
            },
            defaultArea: {
                attrsHover: {
                    fill: "#C0C0C0"
                },
                attrs : {
                    stroke : "#fff", 
                    "stroke-width" : 1
                }
            }
        },
        legend: {
            area: {
                display : false,
                slices : caseLegendAreaColors
            },
        },
        areas: casesCountry
    });
});

//Fuction that resize map
function resize(){   
    if($(window).width() > 700) {
        var h = $(window).width()/3,
        offsetTop = 60; 
        $(".mapael .map").css('width', h - offsetTop);
    } else {
        $(".mapael .map").css('width', '300px');
    }
}

$(document).ready(function(){
    resize();
    $(window).on("resize", function(){                      
        resize();
    });
});