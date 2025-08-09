package woojooin.planitbatch.global.config;

import java.io.FileInputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

@Configuration
public class FirebaseConfig {
	@Value("classpath:firebase/firebase-admin-sdk.json")
	private Resource resource;

	private FirebaseApp firebaseApp;

	@PostConstruct
	public FirebaseApp initFirebase() {
		try {
			FileInputStream serviceAccount =
				new FileInputStream(resource.getFile());

			FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.build();

			return FirebaseApp.initializeApp(options);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Bean
	public FirebaseMessaging initFirebaseMessaging() {
		return FirebaseMessaging.getInstance(firebaseApp);
	}
}
