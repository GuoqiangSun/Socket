

var virtualDevice2 = {
     mac :  "00:00:00:00:00:00",
     sn : 0x12345678,
     version : 1.1,

     sensors2 : {
         unit: 1,
         temp: 2
    },

     countdown2 : {
         hour : 0,
        minute : 0,
        seconds : 0,
        allTime : 0,
        startup : 0,
        operationState : 0
    },

}

function showVirtualDevice2() {
          console.log("a","mac = "+virtualDevice2.mac);
          console.log("a","sn = "+virtualDevice2.sn);
          console.log("a","version = "+virtualDevice2.version);

          console.log("a","unit = "+virtualDevice2.sensors2.unit);
      console.log("a","temp = "+virtualDevice2.sensors2.temp);

      console.log("a","hour = "+virtualDevice2.countdown2.hour);
      console.log("a","minute = "+virtualDevice2.countdown2.minute);
      console.log("a","seconds = "+virtualDevice2.countdown2.seconds);
      console.log("a","allTime = "+virtualDevice2.countdown2.allTime);
      console.log("a","startup = "+virtualDevice2.countdown2.startup);
      console.log("a","operationState = "+virtualDevice2.countdown2.operationState);
}

