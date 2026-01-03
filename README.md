A very simple mediaplayer for MP3 and WAV files controlled by simple MQTT command.<br>
I created it for playing MP3 sounds on a Raspberry 3b, controlled by Home assistant with MQTT installed.<br>
The resource monitor is in idle around1%, when playing a song between 5-35%.<br>

==> important for Raspberry is to install default-jdk, other Java runtimes may not work.<br>

The play command is done by a JSON string like below<br>
**{"action":"play","song":"/mnt/mp3/Jingles/dishready.mp3"}**<br>
  --> The song location can be on a mounted folder or local folder, it should work in Linux and Windows.<br>
Sent to the topic (in my case, according to config.ini)<br>
**home/nodes/sensor/raspiplayer**<br>

In the config.ini file defines MQTT server and other stuff for communication with broker<br>

a possible Home assistant integration can be:<br>
mqtt:<br>
- button:<br>
    command_template: '{"action":"play","song":"/mnt/mp3/Jingles/dishready.mp3"}'<br>
    command_topic: home/nodes/sensor/raspiplayer<br>
    device:<br>
      identifiers:<br>
      - 01KDZ7TW7ARQEPK6NA09ETYNPZ<br>
      name: RaspiPlayer<br>
    device_class: update<br>
    entity_category: config<br>
    name: playDish<br>
    payload_press: PRESS<br>
    qos: 0.0<br>
    unique_id: 01KDZ7TW7ARQEPK6NA09ETYNPZ_a081db703754443cbe11d2b3a2d1beb2<br>

<br>
I installed the java program on a Raspberry PI 3B as a service, the installationprocess was/is:

1. install Java runtime<br>
    -> sudo apt install default-jdk -y<br>
2. control it<br>
    -> java --version<br>
    _openjdk 21.0.9 2025-10-21<br>
    OpenJDK Runtime Environment (build 21.0.9+10-Debian-1deb13u1)<br>
    OpenJDK 64-Bit Server VM (build 21.0.9+10-Debian-1deb13u1, mixed mode, sharing)_<br>
3. copy jar file AND config.ini to your unit (I copied it to /home/pi/apps)
4. create a systemd configuration<br>
  ->sudo nano /etc/systemd/system/raspiplayer.service<br>
    [Unit]<br>
    Description=raspiplayer Java Application<br>
    After=network.target<br>
    <br>
    [Service]<br>
    User=pi<br>
    WorkingDirectory=/home/pi/apps<br>
    ExecStart=/usr/bin/java -jar player.jar config.ini<br>
    SuccessExitStatus=143<br>
    Restart=always<br>
    RestartSec=5<br>
    
    [Install]<br>
    WantedBy=multi-user.target<br>
<br>
5. initialize new service by:<br>
    -> sudo systemctl daemon-reload<br>
    -> sudo systemctl enable raspiplayer.service<br>
6. start the service and check status<br>
    -> sudo systemctl start raspiplayer.service<br>
    -> systemctl status raspiplayer<br>
7. restart Raspi and check again if service is running<br>
<br>
<br>
<br>
Special thanks to:<br>
- Init_io for his library https://github.com/Init-io/JavaMQTT
