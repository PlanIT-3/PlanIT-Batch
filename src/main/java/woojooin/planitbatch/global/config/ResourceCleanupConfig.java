package woojooin.planitbatch.global.config;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.annotation.PreDestroy;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResourceCleanupConfig {

	private CloseableHttpClient httpClient;
	private PoolingHttpClientConnectionManager connectionManager;

	@Bean
	public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
		this.connectionManager = new PoolingHttpClientConnectionManager();
		return this.connectionManager;
	}

	@Bean
	public CloseableHttpClient closeableHttpClient(PoolingHttpClientConnectionManager cm) {
		this.httpClient = org.apache.http.impl.client.HttpClients.custom()
			.setConnectionManager(cm)
			.build();
		return this.httpClient;
	}

	/**
	 * 톰캣 종료 시 리소스 정리
	 */
	@PreDestroy
	public void cleanup() {
		// HttpClient 연결 종료
		try {
			if (httpClient != null) {
				httpClient.close();
			}
			if (connectionManager != null) {
				connectionManager.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// JDBC 드라이버 해제 (WAR 배포 시 메모리 릭 방지)
		try {
			Enumeration<Driver> drivers = DriverManager.getDrivers();
			while (drivers.hasMoreElements()) {
				Driver driver = drivers.nextElement();
				DriverManager.deregisterDriver(driver);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
