#mac 安装rabbitmq
##http://www.rabbitmq.com/install-standalone-mac.html
###brew install rabbitmq
###安装完成后需要将/usr/local/sbin添加到$PATH，可以将下面这两行加到~/.bash_profile或者~/.profile：
#### RabbitMQ Config
#### export PATH=$PATH:/usr/local/sbin
#### rabbitmq-server
#### rabbitmq-plugins enable rabbitmq_management
#### http://localhost.com:15672/