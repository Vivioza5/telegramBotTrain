# Training course  telegram bot
Telegram bot training course to boost my bot building skills.


## Configuration
Variable must be provided:
- `BOT_TOKEN` - telegram bot token
- `BOT_NAME` - telegram bot name

## Deploy
You can run application locally:
* Clone repository
* Create a bot with "botfather"
* Set values for variables
* Run the project's main file.

You can run application remote:
* create jar file from project
* copy jar file to linux server 
* type commands:
```
cd /etc/systemd/system 
touch training-bot.service
```
 
* use script from folder scripts *.service

* type commands:

```systemctl start training-bot.service```
## Languages
- [ ] Java / TelegramPollingBot
