package woojooin.planitbatch.global.config;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebListener
public class AppContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			java.sql.Driver driver = drivers.nextElement();
			try {
				DriverManager.deregisterDriver(driver);
			} catch (SQLException e) {
				log.error("JDBC Driver deregister {}", e.getMessage());
			}
		}

		AbandonedConnectionCleanupThread.checkedShutdown();
	}
}