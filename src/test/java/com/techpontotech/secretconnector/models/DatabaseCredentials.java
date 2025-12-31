package com.techpontotech.secretconnector.models;

/**
 * Exemplo de classe para representar credenciais em formato JSON.
 */
public class DatabaseCredentials {
    private String host;
    private int port;
    private String user;
    private String pass;
    private String dialect;

    // Construtores
    public DatabaseCredentials() {}

    public DatabaseCredentials(String host, int port, String user, String pass, String dialect) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
        this.dialect = dialect;
    }

    // Getters e Setters
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

        public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    @Override
    public String toString() {
        return String.format("DatabaseCredentials{host='%s', port='%s', user='%s', pass='%s', dialect='%s'}", 
                host, port, user, pass, dialect);
    }

}
