
function countdown(){
    var hour;
    var minute;
    var seconds;
    var allTime;
    var startup;
    var operationState;
}

 function sensors(){
        var unit;
        var temp;
 }

function virtualDevice(){

    var mac
    var sn
    var version

   var sensor

   var countdown = countdown

}


var device = new virtualDevice();

function getDevice(){
    return device;
}

function initDevice() {

     device.mac = "00";
     device.sn = "01";
     device.version = "02";

     device.sensor =new sensors();
     device.sensor.unit = "03";
     device.sensor.temp = "04";

     device.countdown = new countdown();
     device.countdown.hour = "05";
     device.countdown.minute = "06";
     device.countdown.seconds = "07";
     device.countdown.allTime = "08";
     device.countdown.startup = "09";
     device.countdown.operationState = "0A";

}

function showDevice(){
      console.log("a","mac = "+device.mac);
      console.log("a","sn = "+device.sn);
      console.log("a","version = "+device.version);
      console.log("a","unit = "+device.sensor.unit);
      console.log("a","temp = "+device.sensor.temp);
      console.log("a","hour = "+device.countdown.hour);
      console.log("a","minute = "+device.countdown.minute);
      console.log("a","seconds = "+device.countdown.seconds);
      console.log("a","allTime = "+device.countdown.allTime);
      console.log("a","startup = "+device.countdown.startup);
      console.log("a","operationState = "+device.countdown.operationState);
}

