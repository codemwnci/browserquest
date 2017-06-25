/**
 * Created by Jerome on 03-03-17.
 * Modified to work without SocketIO by Wayne 21-6-17
 */

var Client = {};
Client.socket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/ws/");
Client.socket.onclose = function () { alert("WebSocket connection closed") };

Client.sendTest = function(){
    console.log("test sent");
    Client.socket.send(JSON.stringify({msg: 'test'}));
};

Client.askNewPlayer = function(){
    Client.socket.send(JSON.stringify({msg: 'newplayer'}));
};

Client.sendClick = function(x,y){
  Client.socket.send(JSON.stringify({msg: 'click', data: {x:x,y:y}}));
};


Client.socket.onmessage = function (res) {
    var json = JSON.parse(res.data)
    var msg = json.msg;
    var data = json.data;

    if (msg == 'newplayer') {
        Game.addNewPlayer(data.id,data.x,data.y);
    }
    else if (msg == 'allplayers') {
        for(var i = 0; i < data.length; i++){
            Game.addNewPlayer(data[i].id,data[i].x,data[i].y);
        }
    }
    else if(msg == 'move') {
        Game.movePlayer(data.id,data.x,data.y);
    }
    else if (msg == 'remove') {
        Game.removePlayer(data.id);
    }
};
