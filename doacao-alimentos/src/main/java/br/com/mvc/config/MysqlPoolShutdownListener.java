package br.com.mvc.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/** Encerra o pool quando a aplicacao e removida ou o Tomcat e desligado. */
@WebListener
public class MysqlPoolShutdownListener implements ServletContextListener {
    @Override
    public void contextDestroyed(ServletContextEvent evento) {
        MysqlSingleton.getInstancia().fecharPool();
    }
}
