package com.kumarsgoyal.rabbitmq.ampq;

import org.springframework.beans.factory.annotation.Value;

public class RabbitMqCredentials {
    @Value("${rabbitmq.username}")
    private String user;

    @Value("${rabbitmq.password}")
    private String password;

    @Value("${rabbitmq.vhost}")
    private String vhost;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVhost() {
        return vhost;
    }

    public void setVhost(String vhost) {
        this.vhost = vhost;
    }

    @Override
    public String toString() {
        return "RabbitMqCredentials{" +
                       "user='" + user + '\'' +
                       ", password='" + password + '\'' +
                       ", vhost='" + vhost + '\'' +
                       '}';
    }
}
