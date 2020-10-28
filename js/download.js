var xmlhttp = new XMLHttpRequest();
var url = "https://raw.githubusercontent.com/Seba244c/Amber-Engine/master/download.json";
var json = null;

xmlhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
        var jo = JSON.parse(this.responseText);
        parse(jo);
    }
};

xmlhttp.open("GET", url, true);
xmlhttp.send();

function parse(jo) {
    json = jo;
    document.getElementById('downloadLatest').href = json.download;
}
